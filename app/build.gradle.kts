plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "net.aosaka.xrarchive"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "net.aosaka.xrarchive"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.arcore)
    implementation(libs.androidx.scenecore)
    implementation(libs.androidx.compose)
    implementation(libs.kotlinx.coroutines.guava)
    compileOnly(libs.androidx.extensions.xr) //This is necessary for Proguard minification

    implementation(libs.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.concurrent.futures)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.ui.tooling)
}