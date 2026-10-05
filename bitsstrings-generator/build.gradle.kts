import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.gradle.plugin.publish)
    alias(libs.plugins.kotlinxSerialization)
}

group = "com.bitsfabrik.bitsstrings"
version = libs.versions.bitsstrings.get()

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(gradleKotlinDsl())
    compileOnly(libs.kotlinGradlePlugin)
    compileOnly(libs.androidGradlePlugin)
    compileOnly(libs.androidSdkCommon)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test-junit"))
}

gradlePlugin {
    plugins {
        create("bitsstrings") {
            id = "com.bitsfabrik.bitsstrings.generator"
            implementationClass = "com.bitsfabrik.bitsstrings.generator.plugins.BitsStringsGeneratorPlugin"

            displayName = "bitsfabrik bitsstrings generator plugin"
            description = "Plugin to provide access to the string resources on iOS & Android"
        }
        create("androidStrings") {
            id = "com.bitsfabrik.bitsstrings.android.generator"
            implementationClass = "com.bitsfabrik.bitsstrings.generator.plugins.AndroidStringsGeneratorPlugin"

            displayName = "bitsfabrik Android-only String generator"
            description = "Plugin to provide access to the string resources on Android-only projects"
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set("bitsstrings Generator")
        description.set("Gradle plugin generating Android and iOS string resources plus typed Kotlin accessors from a strings.ini File")
    }
}
