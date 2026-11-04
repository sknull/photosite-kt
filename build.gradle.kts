plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.gradle.pdf) apply false
    alias(libs.plugins.sqlDelight) apply false
    alias(libs.plugins.kotlin.compiler) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.boot.dependency.management) apply false
    alias(libs.plugins.google.devtools) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}


configurations.all {
    resolutionStrategy {
        // Erzwingt eine neuere Version, die die gesuchte Methode enthält
        force("org.apache.commons:commons-compress:1.27.1")
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // Zwingt das gesamte Gradle-Buildsystem (inkl. AGP), eine neuere Version zu nutzen
        classpath("org.apache.commons:commons-compress:1.27.1")
    }
}
