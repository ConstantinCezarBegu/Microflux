plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.constantin.microflux"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 5
        versionName = "1.0.4"
        multiDexEnabled = true
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
    lintOptions {
        isCheckReleaseBuilds = false
        textReport = true
        htmlReport = false
        xmlReport = false
        isExplainIssues = false
        isAbortOnError = false
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        pickFirst("META-INF/*.kotlin_module")
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.10")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Modules needed.
    api(project(":viewmodel"))
    api(project(":encryption"))
    // Android sqldelight Driver.
    implementation("com.squareup.sqldelight:android-driver:1.4.3")
    // Ktor Driver
    implementation("io.ktor:ktor-client-android:1.4.0")
    // Material
    implementation("com.google.android.material:material:1.3.0-alpha02")
    // Setting edge to edge variables
    implementation("dev.chrisbanes:insetter-ktx:0.3.0")
    // Fetching web content
    implementation("net.dankito.readability4j:readability4j:1.0.4")
    // webview
    implementation("androidx.webkit:webkit:1.3.0")
    // Kotlin std lib
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.10")
    // Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    // Appcompat
    implementation("androidx.appcompat:appcompat:1.3.0-alpha02")
    // Activity
    implementation("androidx.activity:activity-ktx:1.2.0-alpha08")
    // Fragment
    implementation("androidx.fragment:fragment-ktx:1.3.0-alpha08")
    // Core
    implementation("androidx.core:core-ktx:1.5.0-alpha02")
    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")
    // CoordinatorLayout
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
    // Viewpager2
    implementation("androidx.viewpager2:viewpager2:1.1.0-alpha01")
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.2.0-alpha05")
    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // Browser
    implementation("androidx.browser:browser:1.3.0-alpha05")
    // Process
    implementation("androidx.lifecycle:lifecycle-process:2.3.0-alpha07")
    // Work
    implementation("androidx.work:work-runtime-ktx:2.4.0")
    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.0")
    // Dagger
    implementation("com.google.dagger:dagger:2.28.3")
    implementation("com.google.dagger:dagger-android-support:2.28.1")
    kapt("com.google.dagger:dagger-compiler:2.28.1")
    kapt("com.google.dagger:dagger-android-processor:2.28.1")
    // Dagger assist inject
    implementation("com.squareup.inject:assisted-inject-annotations-dagger2:0.5.2")
    kapt("com.squareup.inject:assisted-inject-processor-dagger2:0.5.2")
    // Coil
    implementation("io.coil-kt:coil:0.11.0")
    // Test implementation
    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    // Allows to detect memory leacks.
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.4")
}