# پرامپت‌های تصاویر جعبه جادو

این فایل از `catalog.json` ساخته شده و ۳۰ تصویر اپ را پوشش می‌دهد. پرامپت‌ها انگلیسی هستند چون مدل‌های تصویری با انگلیسی دقیق‌تر کار می‌کنند. هر پرامپت را کامل و بدون تغییر کپی کنید.

## روش ساخت

1. در https://gemini.google.com (یا هر ابزار نانوبنانا) پرامپت‌ها را یکی‌یکی وارد کنید.
2. **یک‌دست بودن کلکسیون:** اول تصویر `forest_cat` را بسازید. برای بقیهٔ عروسک‌ها همان تصویر گربه را هم ضمیمه کنید و این جمله را اول پرامپت بگذارید:
   > Use the attached image only as a style reference: same toy material, rendering, lighting, proportions and background, but a completely different character.
3. هر تصویر را **دقیقاً با اسم فایلِ نوشته‌شده** ذخیره کنید (مثلاً `forest_cat.png`). فرمت png یا jpg فرقی ندارد.
4. همهٔ فایل‌ها را در یک پوشه بگذارید و برای من بفرستید، یا خودتان اجرا کنید:
   ```bash
   pip install pillow
   python3 tools/generate_images.py --import <پوشهٔ تصاویر>
   ```
   این دستور اندازه را درست می‌کند، پس‌زمینهٔ سفید را شفاف می‌کند و هر فایل را در جای درستش در اپ می‌گذارد.

**نسبت تصویر:** همه ۱:۱ (مربع) هستند، به‌جز `home_bg` که ۹:۱۶ (عمودی) است.

## عروسک‌ها — دوستان جنگل

### 1. میمی گربه (معمولی) — فایل: `forest_cat.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a grey kitten creature with pointy ears, a pink nose and a tiny bell collar. It is a common figure from a collectible series. Single character only.
```

### 2. بوبو سگ (معمولی) — فایل: `forest_dog.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a cream puppy creature with floppy brown ears and a red scarf. It is a common figure from a collectible series. Single character only.
```

### 3. هاپی خرگوش (معمولی) — فایل: `forest_rabbit.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a white bunny creature with very long ears, holding a small orange carrot. It is a common figure from a collectible series. Single character only.
```

### 4. بوبی خرس (معمولی) — فایل: `forest_bear.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a chubby brown bear cub creature holding a little honey pot. It is a common figure from a collectible series. Single character only.
```

### 5. فیفی قورباغه (معمولی) — فایل: `forest_frog.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a bright green frog creature with big round eyes, wearing a tiny lily-pad hat. It is a common figure from a collectible series. Single character only.
```

### 6. رورو روباه (کمیاب) — فایل: `forest_fox.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is an orange fox creature with a fluffy white-tipped tail and a little green backpack. It is a rare figure from a collectible series. Single character only.
```

### 7. هوهو جغد (کمیاب) — فایل: `forest_owl.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a round brown owl creature with huge eyes and tiny round glasses, holding a book. It is a rare figure from a collectible series. Single character only.
```

### 8. شاه لئو (مخفی ✨) — فایل: `forest_lion.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a golden lion cub creature with a big fluffy mane and a tiny shiny gold crown, sparkles around it. It is a secret figure from a collectible series. Single character only. Make it look extra special: subtle metallic gold accents and a soft magical glow.
```

## عروسک‌ها — مهمانی میوه‌ها

### 9. اپی سیب (معمولی) — فایل: `fruits_apple.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a creature wearing a red apple costume with a green leaf on top of its head. It is a common figure from a collectible series. Single character only.
```

### 10. نانا موز (معمولی) — فایل: `fruits_banana.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a creature wearing a yellow banana-peel hoodie. It is a common figure from a collectible series. Single character only.
```

### 11. اورنو پرتقال (معمولی) — فایل: `fruits_orange.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a round creature wearing an orange fruit costume, holding a tiny juice glass. It is a common figure from a collectible series. Single character only.
```

### 12. گریپی انگور (معمولی) — فایل: `fruits_grapes.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a creature wearing a purple bunch-of-grapes costume with a curly vine on its head. It is a common figure from a collectible series. Single character only.
```

### 13. بری توت‌فرنگی (معمولی) — فایل: `fruits_strawberry.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a creature wearing a red strawberry hat with little seeds and green leaves. It is a common figure from a collectible series. Single character only.
```

### 14. ملو هندوانه (کمیاب) — فایل: `fruits_watermelon.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a creature wearing a watermelon-slice costume, green outside and pink with black seeds inside, holding a beach ball. It is a rare figure from a collectible series. Single character only.
```

### 15. چی‌چی گیلاس (کمیاب) — فایل: `fruits_cherry.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is twin little creatures wearing red cherry hats joined by one green stem, holding hands. It is a rare figure from a collectible series. Single character only.
```

### 16. پرنسس پینا (مخفی ✨) — فایل: `fruits_pineapple.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a golden pineapple princess creature with spiky green leaf crown and sunglasses, sparkles around it. It is a secret figure from a collectible series. Single character only. Make it look extra special: subtle metallic gold accents and a soft magical glow.
```

## عروسک‌ها — قصه‌ها و شعرها

### 17. خاله سوسکه (معمولی) — فایل: `tales_suske.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a cute little black beetle girl creature wearing a red chador-style headscarf with white polka dots and tiny red shoes, carrying a small bundle. It is a common figure from a collectible series. Single character only.
```

### 18. کدو قلقله‌زن (معمولی) — فایل: `tales_kadu.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a round orange pumpkin creature with a tiny grandma peeking out of a door in the pumpkin, rolling happily. It is a common figure from a collectible series. Single character only.
```

### 19. شنگول (معمولی) — فایل: `tales_shangul.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a white baby goat creature with tiny horns and a blue bow, cheerful. It is a common figure from a collectible series. Single character only.
```

### 20. منگول (معمولی) — فایل: `tales_mangul.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a light brown baby goat creature with tiny horns and a yellow bow, holding a small book. It is a common figure from a collectible series. Single character only.
```

### 21. حسنی (معمولی) — فایل: `tales_hasani.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a playful village boy creature with messy black hair, a striped shirt, riding a tiny wooden toy horse. It is a common figure from a collectible series. Single character only.
```

### 22. ملانصرالدین (کمیاب) — فایل: `tales_mulla.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a funny old wise man creature with a big white turban and a long white beard, sitting backwards on a tiny grey donkey. It is a rare figure from a collectible series. Single character only.
```

### 23. رخش (کمیاب) — فایل: `tales_rakhsh.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a brave little reddish-brown horse creature with a flowing dark mane and a small ancient Persian saddle with gold details. It is a rare figure from a collectible series. Single character only.
```

### 24. سیمرغ (مخفی ✨) — فایل: `tales_simorgh.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. The character is a majestic but cute mythical Persian bird creature with long rainbow peacock-like tail feathers in turquoise, gold and purple, glowing, sparkles around it. It is a secret figure from a collectible series. Single character only. Make it look extra special: subtle metallic gold accents and a soft magical glow.
```

## جعبه‌ها

### 25. جعبهٔ دوستان جنگل — فایل: `forest.png` — نسبت 1:1

```text
3D render of a single collectible toy blind box, kid-friendly, soft pastel colors, glossy, three-quarter view, centered, isolated on a plain pure white background, no text, no letters, no logo, no watermark. The box is a cute pastel green collectible blind box, decorated with little leaves, mushrooms and tiny paw prints, with a big question mark sticker on the front.
```

### 26. جعبهٔ مهمانی میوه‌ها — فایل: `fruits.png` — نسبت 1:1

```text
3D render of a single collectible toy blind box, kid-friendly, soft pastel colors, glossy, three-quarter view, centered, isolated on a plain pure white background, no text, no letters, no logo, no watermark. The box is a cute pastel pink collectible blind box, decorated with tiny strawberries, cherries and lemons, with a big question mark sticker on the front.
```

### 27. جعبهٔ قصه‌ها و شعرها — فایل: `tales.png` — نسبت 1:1

```text
3D render of a single collectible toy blind box, kid-friendly, soft pastel colors, glossy, three-quarter view, centered, isolated on a plain pure white background, no text, no letters, no logo, no watermark. The box is a cute pastel purple collectible blind box, decorated with gold Persian tile patterns, little stars and a crescent moon, with a big question mark sticker on the front.
```

## تصاویر رابط کاربری

### 28. پس‌زمینهٔ صفحهٔ اصلی — فایل: `home_bg.png` — نسبت 9:16

```text
Dreamy pastel illustration background for a kids' app home screen: a cozy magical toy shop with shelves of colorful mystery gift boxes, soft clouds, little stars and confetti, soft lavender and peach colors, plenty of empty space in the middle, no characters, no text, no logo
```

### 29. مسکات اپ (باکسی) — فایل: `mascot.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. Boxi, the mascot of a kids' unboxing app: a small fluffy lavender creature with bunny-like ears, peeking happily out of an open gift box with a pink ribbon, waving one hand
```

### 30. تصویر صفحهٔ بازی‌ها — فایل: `games.png` — نسبت 1:1

```text
Cute collectible designer vinyl art toy figure, chibi proportions with a big head and small body, soft rounded shapes, glossy vinyl material, big sparkly eyes, a playful friendly smile, kid-friendly, warm soft studio lighting, full body, front view, centered, isolated on a plain pure white background, no text, no logo, no watermark. a cheerful pile of colorful toy alphabet blocks, a small book and a game controller shaped like a star, kid-friendly
```
