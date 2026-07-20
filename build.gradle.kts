group = "de.visualdigits"
version = "1.0.0-SNAPSHOT"
description = "photosite"
java.sourceCompatibility = JavaVersion.VERSION_21

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.kotlin.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spring.boot.plugin)
    alias(libs.plugins.spring.boot.dependency.management)
    alias(libs.plugins.kotlin.spring)
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation(libs.bundles.spring.boot)
    implementation(libs.com.drewnoakes.metadata.extractor)
    implementation(libs.com.github.rjeschke.txtmark)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.xml.serialization)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ksoup.core)
    implementation(libs.net.coobird.thumbnailator)
    implementation(libs.org.apache.commons.commons.text)
    implementation(libs.org.apache.tika.tika.core)
    implementation(libs.org.bouncycastle.bcpkix.jdk15to18)
    implementation(libs.org.shredzone.acme4j.acme4j.client)
    implementation(libs.org.shredzone.acme4j.acme4j.utils)
    implementation(libs.org.webjars.bootstrap)
    implementation(libs.org.webjars.jquery)

    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val testsJar by tasks.registering(Jar::class) {
    archiveClassifier = "tests"
    from(sourceSets["test"].output)
}

java {
    withSourcesJar()
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        artifact(testsJar)
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
