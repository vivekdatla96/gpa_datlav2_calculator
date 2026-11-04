# GPA Calculator

A small Android app that averages five course grades (0–100) and shows the result with a color-coded status.

## Features

- Five grade fields with inline validation (empty, out of range, invalid number)
- Result card with a status line: below 60, 60 to 79, or 80 and above
- Light and dark themes built on Material 3
- Portrait and landscape layouts; the result survives rotation
- Keyboard Done on the last field computes the GPA

## Build and run

Requirements: Android Studio (Hedgehog or newer) or JDK 17 with the Android SDK, `compileSdk` 34, `minSdk` 30.

```bash
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/`. To install on a connected device or emulator:

```bash
./gradlew installDebug
```

## Project layout

```text
app/src/main/java/.../MainActivity.java   Input handling, validation and GPA logic
app/src/main/res/layout/                  Portrait screen
app/src/main/res/layout-land/             Landscape screen
app/src/main/res/values/                  Colors, strings and themes (values-night for dark mode)
```

## Author

Vivek Datla
