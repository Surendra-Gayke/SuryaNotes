import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
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

    compileSdk = 36

    defaultConfig {

        applicationId = "com.surendra.suryanotes"

        minSdk = 32

        targetSdk = 36

        versionCode = versionCodeValue

        versionName = versionNameValue

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {

        // ==========================================
        // PASTE YOUR EXISTING SIGNING CONFIG HERE
        // DO NOT MODIFY IT
        // ==========================================

    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }

        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {

        resources {

            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {

        abortOnError = false

        checkReleaseBuilds = true
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {

    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.compose.ui.text)

    androidTestImplementation(platform(libs.compose.bom))

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.ui)

    implementation(libs.compose.ui.preview)

    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.compose.material3)

    implementation(libs.compose.navigation)

    implementation(libs.lifecycle.runtime)

    implementation(libs.lifecycle.viewmodel)

    implementation(libs.coroutines)

    implementation(libs.koin.android)

    implementation(libs.koin.compose)

    implementation(libs.room.runtime)

    implementation(libs.room.ktx)

    ksp(libs.room.compiler)

    implementation(libs.coil.compose)

    implementation(libs.androidx.datastore)

    implementation(libs.kotlinx.serialization)

    implementation(libs.adaptive)

    implementation(libs.material)

    implementation(libs.androidx.compose.material.icons)

    implementation(libs.compose.rich.editor)

    implementation(libs.compose.colorpicker)
}