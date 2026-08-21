plugins {
    alias(libs.plugins.kotlin.compiler)
    alias(libs.plugins.kotlin.serialization)
}

group = "de.visualdigits"
description = "photosite-common"
version = "1.0.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenLocal()
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springModulithVersion"] = "2.0.3"

dependencies {
    implementation(libs.bundles.kotlin)

    testImplementation(libs.bundles.kotlin.test)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
