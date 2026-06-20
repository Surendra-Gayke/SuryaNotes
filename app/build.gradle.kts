import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
}
val versionProperties = Properties()
val versionPropertiesFile = rootProject.file("version.properties")

versionProperties.load(versionPropertiesFile.inputStream())

val versionCodeValue =
    versionProperties["VERSION_CODE"].toString().toInt()

val versionNameValue =
    versionProperties["VERSION_NAME"].toString()

android {
    namespace = "com.surendra.suryanotes"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.surendra.suryanotes"
        minSdk = 32
        targetSdk = 34
        versionCode = versionCodeValue
        versionName = versionNameValue

        setProperty(
            "archivesBaseName",
            "NoteCraft-v$versionNameValue"
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

}
kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // Gson for JSON serialization
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}