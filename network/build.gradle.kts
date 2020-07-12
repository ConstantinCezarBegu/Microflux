plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
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
            "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
            "-Xopt-in=kotlin.RequiresOptIn"
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
    // Ktor
    implementation("io.ktor:ktor-client-android:1.3.2")
    implementation("io.ktor:ktor-client-auth-jvm:1.3.2")
    implementation("io.ktor:ktor-client-serialization-jvm:1.3.2")
}