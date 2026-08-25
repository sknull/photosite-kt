import de.visualdigits.translation.util.TranslationUtil
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.gradle.pdf)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.google.devtools)
}

val version = "1.0.0-SNAPSHOT"
val buildNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "9999"
val installerVersion = if (buildNumber == "9999") {
    version
} else {
    "$version.$buildNumber"
}

buildscript {
    dependencies {
        classpath(libs.proguardGradle)
    }
}

abstract class GenerateVersionTask : DefaultTask() {
    @get:Input
    abstract val appVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val outputFile = outputDirectory.file("AppVersion.kt").get().asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText("""package de.visualdigits.generated

data class AppVersion(
    val version: String = "${appVersion.get()}",
) : Comparable<AppVersion> {

    val numericParts: List<Int> = version
        .substringBefore("-")
        .split(".")
        .map { v -> v.toInt() }

    override fun compareTo(other: AppVersion): Int {
        var c = numericParts[0].compareTo(other.numericParts[0])
        var index = 1
        while (c == 0 && index < 3) {
            c = numericParts[index].compareTo(other.numericParts[index])
            index++
        }
        if (c == 0 && version.contains("-")) {
            c = -1
        }
        
        return c
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppVersion

        return numericParts == other.numericParts
    }

    override fun hashCode(): Int {
        return numericParts.hashCode()
    }
}""")
    }
}

val generateVersionClass = tasks.register<GenerateVersionTask>("generateVersionClass") {
    notCompatibleWithConfigurationCache("No caching supported.")
    appVersion.set(installerVersion)
    outputDirectory.set(layout.buildDirectory.dir("generated/version"))
}

kotlin {
    jvm()
    jvmToolchain(21)
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sqldelight {
        databases {
            create("SettingsDatabase") {
                packageName = "de.visualdigits.photosite"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(generateVersionClass)
        }

        val androidMain by getting {
            dependencies {
                // android
                implementation(libs.compose.components)
                implementation(libs.androidx.activity.compose)

                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.sqldelight.android)

                // android tv
                implementation(project.dependencies.platform("androidx.compose:compose-bom:2026.03.00"))
                implementation(libs.androidx.tv.material)
                implementation(libs.androidx.ui.tooling)
                implementation(libs.androidx.ui.tooling.preview)
            }
        }

        commonMain.dependencies {
            implementation(libs.bundles.compose)
            implementation(libs.bundles.coil)
            implementation(libs.bundles.ktor)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.kotlin)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.jetbrains.compose.navigation)

            implementation(libs.kermit)
            implementation(libs.deskit)

            implementation(libs.html.converter)

            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqlite.bundled)

            implementation(libs.stephans.kmp.components)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit.jupiter.api)
            implementation(libs.junit.jupiter.engine)
            implementation(libs.junit.platform.launcher)
            implementation(libs.koin.test)
        }

        jvmMain.dependencies {
            implementation(libs.flatlaf)
            implementation(compose.desktop.currentOs)
            implementation(libs.skiko.awt.runtime.windows.x64)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.sqldelight.jvm)
            implementation(libs.kotlinx.io.core.jvm)
        }

        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit.jupiter.api)
            implementation(libs.junit.jupiter.engine)
            implementation(libs.junit.platform.launcher)
            implementation(libs.koin.test)
        }
    }
}

configurations.all {
    exclude(group = "ch.qos.logback", module = "logback-classic")
    exclude(group = "ch.qos.logback", module = "logback-core")
}

base {
    archivesName.set("photosite")
}

android {
    namespace = "de.visualdigits.photosite"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "de.visualdigits.photosite"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"

            // Schließt ALLE plattformspezifischen Metadaten aus (Native, JS, Wasm)
            excludes += "**/default/linkdata/**"
            excludes += "**/default/manifest"
            excludes += "**/default/module"
//            excludes += "**/*.knm"
//            excludes += "**/*.kotlin_metadata"

            // Speziell für deinen neuen Fehler (JS/Wasm Pfade)
            excludes += "jsAndWasmJsMain/**"
            excludes += "wasmJsMain/**"
            excludes += "jsMain/**"

            pickFirsts.add("META-INF/kotlin-project-structure-metadata.json")
            pickFirsts.add("META-INF/kotlinx-serialization-json.kotlin_module")
            pickFirsts.add("META-INF/resource_loader.kotlin_module")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

configurations.all {
    exclude(group = "org.jetbrains.compose.material", module = "material-desktop")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Tar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Zip> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

compose.desktop {
    application {
        mainClass = "de.visualdigits.photosite.MainKt"

        nativeDistributions {
            packageName = "de.visualdigits.photosite"
            packageVersion = "1.0.$buildNumber"
            includeAllModules = false
            modules(
                "java.instrument",
                "jdk.unsupported",
                "java.desktop",
                "java.xml",
                "java.naming",
                "java.prefs",
                "java.sql",
                "java.net.http"
            )
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            windows {
//                iconFile.set(project.file("src/commonMain/composeResources/drawable/Msfs2024Tools.ico"))
            }

//            buildTypes {
//                release {
//                    proguard {
//                        configurationFiles.from(project.file("proguard-rules.pro"))
//                        isEnabled.set(false)
//                        optimize.set(false)
//                    }
//                }
//            }
        }
    }
}

tasks.register("showDependencies") {
    doLast {
        configurations.kotlinCompilerClasspath.get()
            .forEach { println("#### ${it.canonicalPath}") }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "de.visualdigits.compose.resources"
}

tasks.asciidoctorPdf {
    notCompatibleWithConfigurationCache("No caching supported.")
    baseDirFollowsSourceFile()
    setSourceDir(rootDir)
    sources {
        include("README.adoc")
    }
    setOutputDir(file(layout.buildDirectory.dir("asciidoc")))
    asciidoctorj {
        attributes(
            mapOf(
                "imagesdir" to rootDir.absolutePath,
                "source-highlighter" to "rouge",
                "icons" to "font"
            )
        )
    }
}

val copyPdfToDistribution = tasks.register<Copy>("copyPdfToDistribution",) {
    group = "documentation"
    description = "Copies the asciidoc pdf into the distribution"

    val pdfTask = tasks.asciidoctorPdf.get()
    dependsOn(tasks.asciidoctorPdf)
    from(pdfTask.outputDir)
    into(layout.buildDirectory.dir("compose/binaries/main/app/de.visualdigits.photosite"))
    include("**/*.pdf")
    eachFile { path = name }
}

val copyPdfToDocs = tasks.register<Copy>("copyPdfToDocs") {
    group = "documentation"
    description = "Copies the asciidoc pdf into the docs directory"

    val pdfTask = tasks.asciidoctorPdf.get()
    dependsOn(tasks.asciidoctorPdf)
    from(pdfTask.outputDir)
    into(file("$rootDir/docs"))
    include("**/*.pdf")
    eachFile { path = name }
}

tasks.matching { it.name == "createDistributable" }.all {
    finalizedBy(copyPdfToDistribution, copyPdfToDocs)
}

tasks.register<Zip>("zip") {
    group = "compose desktop"
    description = "Writes the artifact created by createDistributable to a zip file"

    dependsOn("createDistributable", copyPdfToDistribution, copyPdfToDocs)

    from(layout.buildDirectory.dir("compose/binaries/main/app"))
    from(tasks.asciidoctorPdf.map { it.outputDir }) {
        include("README.pdf") // oder "**/*.pdf"
        into("de.visualdigits.photosite")
    }

    archiveFileName.set("photosite_${project.version}.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
}

tasks.register("extractTranslations") {
    group = "localization"
    description = "Converts the string resources to a csv file under projectroot/translation/stringresources.csv."

    val projectRootDir = project.rootDir
    doLast {
        TranslationUtil.extractTranslation(projectRootDir)
    }
}

tasks.register("updateTranslations") {
    group = "localization"
    description = "Converts the translation csv back to string resources."

    val projectRootDir = project.rootDir
    doLast {
        TranslationUtil.updateTranslation(projectRootDir)
    }
}

tasks.register("joinUpdateTranslations") {
    group = "localization"
    description = "Converts the translation csv back to string resources."

    val projectRootDir = project.rootDir
    doLast {
        TranslationUtil.joinUpdateTranslation(projectRootDir)
    }
}
