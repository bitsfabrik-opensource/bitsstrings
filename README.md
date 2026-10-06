# bitsstrings

[![Maven Central](https://img.shields.io/maven-central/v/com.bitsfabrik.bitsstrings/core.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.bitsfabrik.bitsstrings/core)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/kotlin-2.2.21-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)
![Platforms](https://img.shields.io/badge/platforms-Android%20%7C%20iOS-lightgrey.svg)

**Type-safe, native string resources for Kotlin Multiplatform, generated from a single `strings.ini`.**

Write every translation once, in one file. bitsstrings generates the native resource files for Android and iOS plus a typed Kotlin accessor for every key. You get compile-time safety in shared code, and each platform still uses its own localization system at runtime.


## Contents

- [Features](#features)
- [How it works](#how-it-works)
- [Installation](#installation-kotlin-multiplatform)
- [The `strings.ini` format](#the-stringsini-format)
- [Usage](#usage)
- [Date & time extension](#date--time-extension-optional)
- [Android-only projects](#android-only-projects)
- [Comparison with moko-resources](#comparison-with-moko-resources)
- [Building from source](#building-from-source)
- [License](#license)

## Features

- **One source of truth.** All languages side by side in a single `strings.ini`. No more hunting for the translation you forgot in file number seven.
- **Type-safe keys.** Every string and plural becomes a property on a generated object. Typos fail at compile time.
- **Native at runtime.** Android `strings.xml` / `plurals.xml` and an iOS String Catalog (`Localizable.xcstrings`). No custom lookup tables and no runtime parsing.
- **Plurals done right.** `zero`, `one`, `two`, `few`, `many`, `other`, mapped onto each platform's plural rules.
- **Placeholder normalization.** Write `%d`, `%s`, `%f` or iOS-style `%@`. The generator rewrites them to `%1$s` on Android and `%1$@` on iOS.
- **Composable `StringDesc`.** Build, format and combine strings in shared code and resolve them later on the platform.
- **Locale-aware formatting.** Numbers, currencies, custom formatters, and an optional `kotlinx-datetime` extension.
- **Works without KMP.** A dedicated plugin for plain Android apps and libraries.

| Platform | Supported |
|---|---|
| Android | ✅ minSdk 24 (`date-extension`: 26) |
| iOS | ✅ `iosX64`, `iosArm64`, `iosSimulatorArm64` |
| Android-only (no KMP) | ✅ via a dedicated plugin |

## How it works

```
                       ┌──────────────────────────────────────┐
                       │ androidMain                          │
                       │  res/values-*/strings.xml, plurals   │
                    ┌─▶│  actual object BitsStrings (R.string)│
┌──────────────┐    │  └──────────────────────────────────────┘
│ strings.ini  │────┤  ┌──────────────────────────────────────┐
└──────────────┘    │  │ commonMain                           │
   Gradle plugin    ├─▶│  expect object BitsStrings           │
                    │  └──────────────────────────────────────┘
                    │  ┌──────────────────────────────────────┐
                    │  │ iosMain + iosResourcesDir            │
                    └─▶│  actual object BitsStrings (keys)    │
                       │  Localizable.xcstrings               │
                       └──────────────────────────────────────┘
```

Generation runs automatically before compilation and on Gradle sync, so the IDE always sees up-to-date accessors. You can also run it by hand with `./gradlew generateBitsStrings`.

## Installation (Kotlin Multiplatform)

### 1. Version catalog

```toml
[versions]
bitsstrings = "1.0.0"

[libraries]
bitsfabrik-bitsstrings-core = { group = "com.bitsfabrik.bitsstrings", name = "core", version.ref = "bitsstrings" }
# optional
bitsfabrik-bitsstrings-date = { group = "com.bitsfabrik.bitsstrings", name = "date-extension", version.ref = "bitsstrings" }

[plugins]
bitsfabrik-bitsstrings-generator = { id = "com.bitsfabrik.bitsstrings.generator", version.ref = "bitsstrings" }
```

### 2. Apply the plugin

Root `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.bitsfabrik.bitsstrings.generator) apply false
}
```

Shared module `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.bitsfabrik.bitsstrings.generator)
}

kotlin {
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            export(libs.bitsfabrik.bitsstrings.core) // expose StringDesc to Swift
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.bitsfabrik.bitsstrings.core)
        }
    }
}
```

### 3. Configure (optional)

All properties have defaults. Set only the ones you want to change:

```kotlin
bitsstrings {
    resourcesPackage   = "com.example.myapp"  // default: "${project.group}.${project.name}"
    resourcesClassName = "Strings"            // default: "BitsStrings"
    stringsIniPath     = "../strings.ini"     // default: "../strings.ini", relative to the module
    iosResourcesDir    = "../iosApp"          // default: "../iosApp"
    defaultLang        = "en"                 // default: "de"
    legacyGeneration   = false                // default: false, see below
}
```

| Property | Description |
|---|---|
| `resourcesPackage` | Package of the generated accessor object |
| `resourcesClassName` | Name of the generated accessor object |
| `stringsIniPath` | Location of `strings.ini`, relative to the module |
| `iosResourcesDir` | Directory the iOS resource files are written to |
| `defaultLang` | Fallback used when a translation is missing |
| `legacyGeneration` | `true` writes `<lang>.lproj/Localizable.strings` instead of a String Catalog. Plurals are **not** supported in this mode |

### 4. Wire up iOS

The plugin writes `Localizable.xcstrings` into `iosResourcesDir`. Add that file to your app target in Xcode once. Later regenerations update it in place.

With `legacyGeneration = true`, it writes one `<lang>.lproj/Localizable.strings` per language instead. Add the folder as a **folder reference** so new languages are picked up automatically.

## The `strings.ini` format

```ini
[[General]]
    [general_label_ok]
        de = OK
        en = OK

    [general_new_messages_count]
        <zero>
            de = Keine neuen Nachrichten
            en = No new messages
        <one>
            de = Eine neue Nachricht
            en = One new message
        <other>
            de = %d neue Nachrichten
            en = %d new messages

[[Login]]
    [login_label_password_reset_successful]
        de = Es wurde eine E-Mail an %@ gesendet.
        en = An e-mail has been sent to %@.
```

| Syntax | Meaning |
|---|---|
| `[[Section]]` | Groups keys. Only for readability, it has no effect on the output |
| `[key_name]` | Defines a string. Each line below it is `<language> = <value>` |
| `<zero>` … `<other>` | Plural blocks: `zero`, `one`, `two`, `few`, `many`, `other` |
| `%d`, `%s`, `%f`, `%@` | Placeholders, rewritten per platform |

Escaping is handled for you: XML special characters are escaped on Android (supported HTML tags such as `<b>`, `<i>`, `<a href>`, `<br>` and `<p>` are kept), and quotes are escaped on iOS. When a translation is missing, the value from `defaultLang` is used.

## Usage

### Generated accessors

For the ini above, the plugin generates roughly this:

```kotlin
// commonMain
expect object BitsStrings {
    val general_label_ok: StringResource
    val general_new_messages_count: PluralsResource
    val login_label_password_reset_successful: StringResource
}

// androidMain
actual object BitsStrings {
    actual val general_label_ok: StringResource = StringResource(R.string.general_label_ok)
    // ...
}

// iosMain
actual object BitsStrings {
    actual val general_label_ok: StringResource = StringResource("general_label_ok")
    // ...
}
```

### Building strings in shared code

Shared code works with `StringDesc`, a description of a string that the platform resolves later:

```kotlin
import com.bitsfabrik.bitsstrings.core.desc.*
import com.bitsfabrik.bitsstrings.core.res.format

val ok: StringDesc        = BitsStrings.general_label_ok.desc()
val messages: StringDesc  = BitsStrings.general_new_messages_count.desc(count)
val emailSent: StringDesc = BitsStrings.login_label_password_reset_successful.format(email)
val pluralFmt: StringDesc = BitsStrings.general_new_messages_count.format(count, count)
val raw: StringDesc       = "Hello".desc()

// Composition
val combined = ok + " – ".desc() + messages
val list     = listOf(a, b, c).joinToStringDesc(", ")

// Numbers & currency (locale-aware)
val price  = StringDesc.Currency(19.9)
val amount = StringDesc.Number(1234.5, CommonNumberFormatter(1, 0, 2, useGrouping = true))

// Server-provided translations with fallback
val title = StringDesc.LocalizedRaw(
    strings = mapOf("de" to "Hallo".desc(), "en" to "Hello".desc()),
    fallbackLanguage = "en",
)

// Fully custom formatting
val custom = StringDesc.Custom(user) { u, locale -> "${u.firstName} (${locale.language})" }
```

> **Tip:** `PluralsResource.format(number, args…)` takes the plural quantity first and the format arguments after it. That's why `count` appears twice above.

### Resolving on Android

```kotlin
val text: String = stringDesc.toString(context)

// with an explicit locale
val german = stringDesc.toString(context, StringDesc.LocaleType.Custom(BitsLocale("de-AT")))

// or directly from a resource
BitsStrings.general_label_ok.getString(context)
```

### Resolving on iOS (Swift)

```swift
let text = stringDesc.localized()
```

`StringDesc.LocaleType.Custom(locale:)` resolves the string against a specific `.lproj` bundle, independent of the device language.

## Date & time extension (optional)

```kotlin
commonMain.dependencies {
    api(libs.bitsfabrik.bitsstrings.core)
    implementation(libs.bitsfabrik.bitsstrings.date)
}
```

Adds `StringDesc` builders for `kotlinx-datetime` types, backed by platform formatters (`LocalizedDateFormatter`, `LocalizedTimeFormatter`, `LocalizedDateTimeFormatter`, `LocalizedInstantFormatter`):

```kotlin
val date:     StringDesc = StringDesc.Date(LocalDate(2026, 9, 24), pattern = "dd. MMMM yyyy")
val time:     StringDesc = StringDesc.Time(LocalTime(14, 30), pattern = "HH:mm")
val dateTime: StringDesc = StringDesc.DateTime(localDateTime, pattern = "dd.MM.yyyy HH:mm")
val instant:  StringDesc = StringDesc.Instant(instant, pattern = "dd.MM.yyyy HH:mm")
```

Each builder also accepts a custom formatter instead of a pattern.

> **Note:** Requires Android minSdk 26. `date-extension` depends on `core` as `compileOnly`, so you have to add `core` yourself.

## Android-only projects

For plain Android apps or libraries without Kotlin Multiplatform, use the dedicated plugin:

```toml
[plugins]
bitsfabrik-bitsstrings-android-generator = { id = "com.bitsfabrik.bitsstrings.android.generator", version.ref = "bitsstrings" }
```

```kotlin
plugins {
    id("com.android.application") // or com.android.library
    alias(libs.plugins.bitsfabrik.bitsstrings.android.generator)
}

dependencies {
    implementation(libs.bitsfabrik.bitsstrings.core)
}

bitsfabrikAndroidStrings {
    resourcesPackage   = "com.example.myandroidapp"
    resourcesClassName = "Strings"        // default: "BitsStrings"
    stringsIniPath     = "../strings.ini"
    defaultLang        = "en"             // default: "de"
}
```

This generates a plain `object` (no `expect`/`actual`) backed by `R.string` / `R.plurals`, together with the Android resource files. Generation runs before `preBuild` and on Gradle sync, or manually with `./gradlew generateAndroidStrings`.

## Modules

| Module | Coordinates | Description |
|---|---|---|
| `core` | `com.bitsfabrik.bitsstrings:core` | KMP runtime: `StringResource`, `PluralsResource`, `StringDesc`, `BitsLocale` |
| `date-extension` | `com.bitsfabrik.bitsstrings:date-extension` | Optional localized date/time formatting for `kotlinx-datetime` |
| `bitsstrings-generator` | plugin `com.bitsfabrik.bitsstrings.generator` | Gradle plugin for KMP projects |
| `bitsstrings-generator` | plugin `com.bitsfabrik.bitsstrings.android.generator` | Gradle plugin for pure Android projects |

## Comparison with moko-resources

The public API of bitsstrings is modelled on [**moko-resources**](https://github.com/icerockdev/moko-resources) by IceRock Development. If you've used moko, you already know the concepts: `StringResource`, `PluralsResource`, and the `StringDesc` family (`Raw`, `Resource`, `Plural`, `Composition`, …). On top of that API, bitsstrings is a framework of its own, tailored to a different workflow:

| | moko-resources | bitsstrings |
|---|---|---|
| Source of truth | `strings.xml` per language | **One `strings.ini`** holding all languages side by side |
| Scope | Strings, images, fonts, colors, files, … | **Strings and plurals only**: small and focused |
| iOS output | `.strings` / `.stringsdict` | **String Catalog** (`.xcstrings`), or legacy `.strings` |
| Placeholders | Android style | Android (`%d`, `%s`) **and** iOS style (`%@`), normalized per platform |
| Formatting | `StringDesc` | `StringDesc` + number/currency, custom formatters, optional date/time |
| Android-only projects | — | Dedicated Android-only plugin |

If you need images, fonts, colors or other resource types, moko-resources is the more complete choice.

## Building from source

```bash
git clone https://github.com/bitsfabrik-opensource/bitsstrings.git
cd bitsstrings
./gradlew build
```

The repository contains the `core` and `date-extension` libraries and the `bitsstrings-generator` Gradle plugin. Issues and pull requests are welcome.

## Acknowledgements

- [moko-resources](https://github.com/icerockdev/moko-resources) by IceRock Development: API design for `StringResource`, `PluralsResource` and `StringDesc`, and parts of the initial implementation (see [NOTICE](NOTICE)).
- [KotlinPoet](https://github.com/square/kotlinpoet): Kotlin code generation.
- [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime): date & time types for the date extension.

## License

```
Copyright 2026 bitsfabrik

Licensed under the Apache License, Version 2.0
```

See [LICENSE](LICENSE) for the full text.
