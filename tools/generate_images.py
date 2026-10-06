#!/usr/bin/env python3
"""Generate all app artwork with Google's Nano Banana (Gemini image) model.

Reads the doll, box and UI descriptions from app/src/main/assets/content/catalog.json
and writes WebP files into app/src/main/assets/images/. The app shows emoji
placeholders for any image that does not exist yet, so you can run this at any time.

Usage:
    pip install pillow
    export GEMINI_API_KEY=...            # from https://aistudio.google.com/apikey
    python3 tools/generate_images.py              # generate everything that is missing
    python3 tools/generate_images.py --only forest_cat --force
    python3 tools/generate_images.py --dry-run    # print the prompts only

Set GEMINI_IMAGE_MODEL to try another model, e.g. gemini-3-pro-image-preview (Nano Banana Pro).
"""

from __future__ import annotations

import argparse
import base64
import io
import json
import os
import sys
import time
import urllib.error
import urllib.request
from collections import deque
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ASSETS = ROOT / "app" / "src" / "main" / "assets"
CATALOG = ASSETS / "content" / "catalog.json"
IMAGES = ASSETS / "images"

DEFAULT_MODEL = "gemini-2.5-flash-image"
API = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent"
MAX_SIDE = 768
# The first doll becomes a style reference so the whole collection looks like one toy line.
STYLE_REFERENCE = "dolls/forest_cat.webp"


def build_jobs(catalog: dict) -> list[dict]:
    img = catalog["images"]
    jobs = []
    for doll in catalog["dolls"]:
        prompt = (
            f"{img['style']}. The character is {doll['look']}. "
            f"It is a {doll['rarity']} figure from a collectible series. "
            "Single character only."
        )
        if doll["rarity"] == "secret":
            prompt += " Make it look extra special: subtle metallic gold accents and a soft magical glow."
        jobs.append({"file": f"dolls/{doll['id']}.webp", "prompt": prompt, "aspect": "1:1",
                     "removeBg": True, "styleRef": True})
    for series in catalog["series"]:
        jobs.append({"file": f"boxes/{series['id']}.webp",
                     "prompt": f"{img['boxStyle']}. The box is {series['boxLook']}.",
                     "aspect": "1:1", "removeBg": True, "styleRef": False})
    for extra in img.get("extra", []):
        prompt = extra["prompt"]
        if extra.get("useStyle"):
            prompt = f"{img['style']}. {prompt}"
        jobs.append({"file": extra["file"], "prompt": prompt, "aspect": extra.get("aspect", "1:1"),
                     "removeBg": extra.get("removeBg", False), "styleRef": False})
    return jobs


def call_gemini(api_key: str, model: str, prompt: str, aspect: str, reference: bytes | None) -> bytes:
    parts: list[dict] = []
    if reference is not None:
        parts.append({"inlineData": {"mimeType": "image/png", "data": base64.b64encode(reference).decode()}})
        prompt = ("Use the attached image only as a style reference: same toy material, rendering, lighting, "
                  "proportions and background, but a completely different character. " + prompt)
    parts.append({"text": prompt})
    body = {
        "contents": [{"parts": parts}],
        "generationConfig": {"responseModalities": ["IMAGE"], "imageConfig": {"aspectRatio": aspect}},
    }
    req = urllib.request.Request(
        API.format(model=model),
        data=json.dumps(body).encode(),
        headers={"Content-Type": "application/json", "x-goog-api-key": api_key},
        method="POST",
    )
    for attempt in range(5):
        try:
            with urllib.request.urlopen(req, timeout=180) as resp:
                data = json.load(resp)
            for cand in data.get("candidates", []):
                for part in cand.get("content", {}).get("parts", []):
                    inline = part.get("inlineData") or part.get("inline_data")
                    if inline and inline.get("data"):
                        return base64.b64decode(inline["data"])
            raise RuntimeError(f"no image in response: {json.dumps(data)[:400]}")
        except urllib.error.HTTPError as e:
            detail = e.read().decode(errors="replace")[:400]
            if e.code in (429, 500, 502, 503, 504) and attempt < 4:
                wait = 2 ** (attempt + 2)
                print(f"    HTTP {e.code}, retrying in {wait}s")
                time.sleep(wait)
                continue
            raise RuntimeError(f"HTTP {e.code}: {detail}") from None
    raise RuntimeError("gave up after retries")


def remove_white_background(im, tolerance: int = 22):
    """Flood-fill near-white pixels connected to the border into transparency."""
    im = im.convert("RGBA")
    w, h = im.size
    px = im.load()
    seen = bytearray(w * h)
    queue = deque()
    for x in range(w):
        queue.extend([(x, 0), (x, h - 1)])
    for y in range(h):
        queue.extend([(0, y), (w - 1, y)])
    limit = 255 - tolerance
    while queue:
        x, y = queue.popleft()
        i = y * w + x
        if seen[i]:
            continue
        seen[i] = 1
        r, g, b, _ = px[x, y]
        if r < limit or g < limit or b < limit:
            continue
        px[x, y] = (r, g, b, 0)
        if x > 0: queue.append((x - 1, y))
        if x < w - 1: queue.append((x + 1, y))
        if y > 0: queue.append((x, y - 1))
        if y < h - 1: queue.append((x, y + 1))
    return im


def save_webp(raw: bytes, dest: Path, remove_bg: bool) -> None:
    from PIL import Image

    im = Image.open(io.BytesIO(raw))
    im.thumbnail((MAX_SIDE, MAX_SIDE * 2))
    im = remove_white_background(im) if remove_bg else im.convert("RGB")
    dest.parent.mkdir(parents=True, exist_ok=True)
    im.save(dest, "WEBP", quality=85, method=6)


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--only", nargs="*", help="only these ids or file paths (e.g. forest_cat boxes/tales.webp)")
    ap.add_argument("--force", action="store_true", help="regenerate images that already exist")
    ap.add_argument("--dry-run", action="store_true", help="print prompts without calling the API")
    ap.add_argument("--no-style-ref", action="store_true", help="don't use the first doll as a style reference")
    args = ap.parse_args()

    catalog = json.loads(CATALOG.read_text(encoding="utf-8"))
    jobs = build_jobs(catalog)
    if args.only:
        wanted = set(args.only)
        jobs = [j for j in jobs if j["file"] in wanted or Path(j["file"]).stem in wanted]
    # Generate the style reference first so later dolls can use it.
    jobs.sort(key=lambda j: j["file"] != STYLE_REFERENCE)

    if args.dry_run:
        for j in jobs:
            print(f"{j['file']}  [{j['aspect']}]\n  {j['prompt']}\n")
        return 0

    api_key = os.environ.get("GEMINI_API_KEY") or os.environ.get("GOOGLE_API_KEY")
    if not api_key:
        print("Set GEMINI_API_KEY (get one at https://aistudio.google.com/apikey).", file=sys.stderr)
        return 2
    try:
        import PIL  # noqa: F401
    except ImportError:
        print("Pillow is required: pip install pillow", file=sys.stderr)
        return 2
    model = os.environ.get("GEMINI_IMAGE_MODEL", DEFAULT_MODEL)

    failures = 0
    for n, job in enumerate(jobs, 1):
        dest = IMAGES / job["file"]
        if dest.exists() and not args.force:
            print(f"[{n}/{len(jobs)}] skip {job['file']} (exists)")
            continue
        reference = None
        ref_path = IMAGES / STYLE_REFERENCE
        if job["styleRef"] and not args.no_style_ref and job["file"] != STYLE_REFERENCE and ref_path.exists():
            from PIL import Image

            buf = io.BytesIO()
            Image.open(ref_path).convert("RGB").save(buf, "PNG")
            reference = buf.getvalue()
        print(f"[{n}/{len(jobs)}] {job['file']} ...", flush=True)
        try:
            raw = call_gemini(api_key, model, job["prompt"], job["aspect"], reference)
            save_webp(raw, dest, job["removeBg"])
            print(f"    saved {dest.relative_to(ROOT)} ({dest.stat().st_size // 1024} KB)")
        except Exception as e:  # keep going so one bad prompt doesn't stop the batch
            failures += 1
            print(f"    FAILED: {e}", file=sys.stderr)
    print(f"done, {failures} failed")
    return 1 if failures else 0


if __name__ == "__main__":
    sys.exit(main())
