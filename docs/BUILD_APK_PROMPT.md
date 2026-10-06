# Prompt: build the Magic Box APK

Copy everything below the line and give it to a coding agent (or a developer) that has a normal Android build environment.

---

Build an installable Android APK from this repository:

- Repository: https://github.com/enayatghafoori/unboxingandroidapp
- Branch: `claude/unboxing-doll-app-kids-w7wkpv`

About the project:
- Kotlin + Jetpack Compose app ("Magic Box", package `com.magicbox.kids`), single `:app` module.
- Gradle wrapper 8.14.3, Android Gradle Plugin 8.10.1, Kotlin 2.1.21, compileSdk/targetSdk 35, minSdk 24, JDK 17.
- Dependency versions are in `gradle/libs.versions.toml`.
- The code has never been built with the real Android SDK yet. It was compiled against Compose Desktop plus the Android framework jar, so small Android-only compile errors are possible, mainly in `ui/MagicBoxApp.kt` (navigation arguments) and `MainActivity.kt`.

Steps:
1. Clone the branch and install JDK 17 and the Android SDK (platform 35, build-tools 35).
2. Run `./gradlew testDebugUnitTest`. These tests must pass.
3. Run `./gradlew assembleDebug assembleRelease`.
4. If the build fails, fix the code with the smallest change that keeps the existing behavior. Don't remove features, screens or content, and don't change `app/src/main/assets/content/catalog.json` or the images. Then rebuild.
5. Install `app/build/outputs/apk/debug/app-debug.apk` on an emulator (API 34+) and check that:
   - the app starts in Persian, right-to-left;
   - a box can be opened (tap the box 3 times, swipe across the tape, tap the lid);
   - each of the 8 mini-games opens and finishes;
   - the parent area opens after the multiplication question.
6. Deliver:
   - `app-debug.apk`
   - `app-release.apk` (signed with the debug key, as configured in `app/build.gradle.kts`)
   - a short list of every code change you made
   - if you have push access, the fixes committed to the same branch

Notes:
- Do not add internet permission, ads, analytics or in-app purchases. The app is for children and is fully offline.
- `.github/workflows/android.yml` already builds and uploads both APKs if GitHub Actions is available.
