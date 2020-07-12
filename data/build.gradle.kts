import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin")
}

repositories {
    jcenter()
}

dependencies {
    // Kotlin std lib
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.7")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
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