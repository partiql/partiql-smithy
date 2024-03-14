import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

plugins {
    kotlin("jvm") version "1.6.20"
    id("org.gradle.antlr")
    application

    // TODO SANDBOX
    // id("com.google.protobuf") version "0.9.4"
}

val platform = "osx-x86_64"

object Versions {
    // Language
    const val kotlin = "1.6.20"
    const val kotlinLanguage = "1.6"
    const val kotlinApi = "1.6"
    const val jvmTarget = "1.8"

    // Dependencies
    const val antlr = "4.10.1"
    const val ionElement = "1.0.0"
    const val kasechange = "1.3.0"
    const val kotlinPoet = "1.11.0"
    const val mustache = "1.16"
    const val picoCli = "4.7.0"

    // Testing
    const val junit5 = "5.9.3"
}

object Deps {
    // Language
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"

    // Dependencies
    const val antlr = "org.antlr:antlr4:${Versions.antlr}"
    const val antlrRuntime = "org.antlr:antlr4-runtime:${Versions.antlr}"
    const val ionElement = "com.amazon.ion:ion-element:${Versions.ionElement}"
    const val kasechange = "net.pearx.kasechange:kasechange:${Versions.kasechange}"
    const val kotlinPoet = "com.squareup:kotlinpoet:${Versions.kotlinPoet}"
    const val mustache = "com.samskivert:jmustache:${Versions.mustache}"
    const val picoCli = "info.picocli:picocli:${Versions.picoCli}"

    // Testing
    const val junitApi = "org.junit.jupiter:junit-jupiter-api:${Versions.junit5}"
    const val junitParams = "org.junit.jupiter:junit-jupiter-params:${Versions.junit5}"
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
    const val kotlinTestJunit = "org.jetbrains.kotlin:kotlin-test-junit5:${Versions.kotlin}"
}

repositories {
    mavenCentral()
}

dependencies {
    antlr(Deps.antlr)
    implementation(Deps.antlrRuntime)
    implementation(Deps.ionElement)
    implementation(Deps.kasechange)
    implementation(Deps.mustache)
    implementation(Deps.picoCli)
    // Test
    testImplementation(Deps.kotlinTest)
    testImplementation(Deps.kotlinTestJunit)
    testImplementation(Deps.junitParams)

    // TODO SANDBOX
    // implementation("com.google.protobuf:protobuf-java:3.6.1")
}

java {
    sourceCompatibility = JavaVersion.toVersion(Versions.jvmTarget)
    targetCompatibility = JavaVersion.toVersion(Versions.jvmTarget)
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}

sourceSets {
    main {
        java.srcDir("$buildDir/generated-src")
    }
}

kotlin.sourceSets {
    all {
        languageSettings.optIn("kotlin.RequiresOptIn")
    }
    main {
        kotlin.srcDir("$buildDir/generated-src")
    }
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = Versions.jvmTarget
    kotlinOptions.apiVersion = Versions.kotlinApi
    kotlinOptions.languageVersion = Versions.kotlinLanguage
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = Versions.jvmTarget
    kotlinOptions.apiVersion = Versions.kotlinApi
    kotlinOptions.languageVersion = Versions.kotlinLanguage
}

tasks.test {
//    useJUnitPlatform() // Enable JUnit5
//    jvmArgs!!.addAll(listOf("-Duser.language=en", "-Duser.country=US"))
//    maxHeapSize = "4g"
//    testLogging {
//        events.add(TestLogEvent.FAILED)
//        exceptionFormat = TestExceptionFormat.FULL
//    }
}

tasks.generateGrammarSource {
    val antlrPackage = "org.partiql.tool.ridl.antlr"
    val antlrSources = "$buildDir/generated-src/${antlrPackage.replace('.', '/')}"
    maxHeapSize = "64m"
    arguments = listOf("-visitor", "-long-messages", "-package", antlrPackage)
    outputDirectory = File(antlrSources)
}

tasks.javadoc {
    exclude("**/antlr/**")
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.findByName("sourcesJar")?.apply {
    dependsOn(tasks.generateGrammarSource)
}

distributions {
    main {
        distributionBaseName.set("ridl")
    }
}

tasks.register<GradleBuild>("install") {
    tasks = listOf("assembleDist", "distZip", "installDist")
}

application {
    applicationName = "ridl"
    mainClass.set("org.partiql.tool.ridl.MainKt")
}

//protobuf {
//    protoc {
//        artifact = "com.google.protobuf:protoc:3.2.0:$platform"
//    }
//}
