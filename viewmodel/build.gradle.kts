plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        @Suppress("SuspiciousCollectionReassignment")
        freeCompilerArgs += listOf(
            "-progressive",
            "-XXLanguage:+NewInference",
            "-XXLanguage:+InlineClasses",
            "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xuse-experimental=kotlinx.coroutines.FlowPreview"
        )
    }
    packagingOptions {
        pickFirst("META-INF/*.kotlin_module")
    }
}

dependencies {
    // modules
    api(project(":repository"))
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0-alpha06")
    // viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0-alpha06")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.7")
}