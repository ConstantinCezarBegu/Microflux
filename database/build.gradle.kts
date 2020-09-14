plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("com.squareup.sqldelight")
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

repositories {
    jcenter()
}

dependencies {
    // Modules
    implementation(project(":data"))
    implementation(project(":encryption"))
    // Sqldelight
    implementation("com.squareup.sqldelight:android-driver:1.4.3")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:1.4.3")
    // Tink for encryption
    implementation ("com.google.crypto.tink:tink-android:1.4.0-rc2")
}

sqldelight {
    database("Database") {
        packageName = "com.constantin.microflux.database"
    }
}