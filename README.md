# bitsstrings

**Type-safe, native string resources for Kotlin Multiplatform (Android & iOS), generated from a single `strings.ini`.**

bitsstrings is a Kotlin Multiplatform library plus a Gradle plugin. You keep all translations in one `strings.ini` file. The plugin turns that file into:

- native platform resource files: Android `strings.xml` / `plurals.xml`, iOS `Localizable.strings` / `Localizable.stringsdict`
- a typed Kotlin object (`expect` in `commonMain`, `actual` in `androidMain` / `iosMain`) with one property per string key

At runtime, strings are resolved through each platform's own localization system (`Context.getString` on Android, `NSBundle` on iOS). No custom lookup tables and no runtime parsing.

| Platform | Supported |
|----------|-----------|
| Android  | ✅ (minSdk 24, date-extension: 26) |
| iOS      | ✅ (`iosX64`, `iosArm64`, `iosSimulatorArm64`) |
| Android-only (no KMP) | ✅ via a dedicated plugin |

---

## Inspired by moko-resources

The public API of bitsstrings is modelled on [**moko-resources**](https://github.com/icerockdev/moko-resources) by IceRock Development. If you have used moko before, you'll recognise the concepts: `StringResource`, `PluralsResource`, and the `StringDesc` abstraction with `Raw`, `Resource`, `Plural`, `Composition`, and so on.

Around that API design we built our own framework, tailored to our workflow:

| | moko-resources | bitsstrings |
|---|---|---|
| Source of truth | `strings.xml` per language in `commonMain/moko-resources` | **One `strings.ini`** holding all languages side by side |
| Scope | Strings, images, fonts, colors, files, … | **Strings and plurals only**: small and focused |
| Placeholders | Android style | Android (`%d`, `%s`) **and** iOS style (`%@`), normalized per platform |
| Formatting | `StringDesc` | `StringDesc` + number/currency formatting, custom formatters, optional date/time extension |
| Android-only projects | — | Dedicated Android-only plugin |

Many thanks to the moko-resources authors for the API design. If you need images, fonts, colors or other resource types, moko-resources is the more complete choice.

---

## Modules

| Module | Artifact | Description |
|---|---|---|
| `core` | `com.bitsfabrik.bitsstrings:core` | KMP runtime: `StringResource`, `PluralsResource`, `StringDesc`, `BitsLocale` |
| `date-extension` | `com.bitsfabrik.bitsstrings:date-extension` | Optional: localized date/time formatting based on `kotlinx-datetime` |
| `bitsstrings-generator` | plugin `com.bitsfabrik.bitsstrings.generator` | Gradle plugin for KMP projects |
| `bitsstrings-generator` | plugin `com.bitsfabrik.bitsstrings.android.generator` | Gradle plugin for pure Android projects |

---

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

- `[[Section]]` groups keys (for readability only).
- `[key_name]` defines a string. Each line below it is `<language> = <value>`.
- Plurals use `<zero>`, `<one>`, `<two>`, `<few>`, `<many>`, `<other>` blocks.
- Placeholders: `%d`, `%s`, `%f` and iOS-style `%@` are all accepted. The generator rewrites them per platform (`%1$s` on Android, `%1$@` on iOS). It also escapes XML on Android, keeping the allowed HTML tags, and escapes quotes on iOS.
- When a translation is missing, the value from `defaultLang` is used.

---

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
            export(libs.bitsfabrik.bitsstrings.core)   // expose StringDesc to Swift
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

```kotlin
bitsstrings {
    resourcesPackage   = "com.example.myapp"   // default: "${project.group}.${project.name}"
    resourcesClassName = "Strings"                  // default: "Strings"
    stringsIniPath     = "../strings.ini"      // default, relative to the module
    iosResourcesDir    = "../iosApp"           // default, where .strings/.stringsdict are written
    defaultLang        = "en"                  // default: "de"
    legacyGeneration   = false                 // true = .strings only, no .stringsdict
}
```

### 4. Wire up iOS

The generated `<lang>.lproj/Localizable.strings` and `.stringsdict` files are written into `iosResourcesDir`. Add that folder to your Xcode project as a **folder reference**, so new languages are picked up automatically.


---

## Usage

### Generated mapping

For the ini above, the plugin generates something like this:

```kotlin
// commonMain
expect object Strings {
    val general_label_ok: StringResource
    val general_new_messages_count: PluralsResource
    val login_label_password_reset_successful: StringResource
}

// androidMain
actual object Strings {
    actual val general_label_ok: StringResource = StringResource(R.string.general_label_ok)
    // ...
}

// iosMain
actual object Strings {
    actual val general_label_ok: StringResource = StringResource("general_label_ok")
    // ...
}
```

### Building strings in shared code

Shared code works with `StringDesc`, a description of a string that is resolved later on the platform:

```kotlin
import com.bitsfabrik.bitsstrings.core.desc.*
import com.bitsfabrik.bitsstrings.core.res.format

val ok: StringDesc          = Strings.general_label_ok.desc()
val messages: StringDesc    = Strings.general_new_messages_count.desc(count)
val emailSent: StringDesc   = Strings.login_label_password_reset_successful.format(email)
val pluralFmt: StringDesc   = Strings.general_new_messages_count.format(count, count)
val raw: StringDesc         = "Hello".desc()

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

### Resolving on Android

```kotlin
val text: String = stringDesc.toString(context)

// with an explicit locale
val german = stringDesc.toString(context, StringDesc.LocaleType.Custom(BitsLocale("de-AT")))

// or directly from a resource
Strings.general_label_ok.getString(context)
```

### Resolving on iOS (Swift)

```swift
let text = stringDesc.localized()
```

`StringDesc.LocaleType.Custom(locale:)` resolves the string against a specific `.lproj` bundle, independent of the device language.

---

## Date & time extension (optional)

```kotlin
commonMain.dependencies {
    api(libs.bitsfabrik.bitsstrings.core)
    implementation(libs.bitsfabrik.bitsstrings.date)
}
```

The extension adds `StringDesc` builders and platform-backed formatters (`LocalizedDateFormatter`, `LocalizedTimeFormatter`, `LocalizedDateTimeFormatter`, `LocalizedInstantFormatter`) for `kotlinx-datetime` types:

```kotlin
val date: StringDesc = StringDesc.Date(LocalDate(2026, 9, 24), pattern = "dd. MMMM yyyy")
```

Requires Android minSdk 26. `date-extension` depends on `core` as `compileOnly`, so you have to add `core` yourself.

---

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
    resourcesClassName = "Strings"
    stringsIniPath     = "../strings.ini"
    defaultLang        = "en"
}
```

This generates a plain `object Strings` with no `expect`/`actual`, backed by `R.string` / `R.plurals`, together with the Android resource files. Run it manually with `./gradlew generateAndroidStrings`.

---

## Acknowledgements

- [moko-resources](https://github.com/icerockdev/moko-resources) by IceRock Development: API design for `StringResource`, `PluralsResource` and `StringDesc`, and parts of the initial implementation (see [NOTICE](NOTICE)).
- [KotlinPoet](https://github.com/square/kotlinpoet): Kotlin code generation.
- [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime): date & time types for the date extension.

---

## License

```
Copyright 2026 bitsfabrik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
