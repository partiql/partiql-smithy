import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

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
    application
}

object Versions {
    // Language
    const val kotlin = "1.6.20"
    const val kotlinLanguage = "1.6"
    const val kotlinApi = "1.6"
    const val jvmTarget = "1.8"

    // Dependencies
    const val dotlin = "1.0.2"
    const val ionElement = "1.0.0"
    const val kasechange = "1.3.0"
    const val kotlinPoet = "1.11.0"
    const val picoCli = "4.7.0"

    // Testing
    const val junit5 = "5.9.3"
}

object Deps {
    // Language
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"

    // Dependencies
    const val dotlin = "io.github.rchowell:dotlin:${Versions.dotlin}"
    const val ionElement = "com.amazon.ion:ion-element:${Versions.ionElement}"
    const val kasechange = "net.pearx.kasechange:kasechange:${Versions.kasechange}"
    const val kotlinPoet = "com.squareup:kotlinpoet:${Versions.kotlinPoet}"
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
    implementation(Deps.dotlin)
    implementation(Deps.ionElement)
    implementation(Deps.kasechange)
    implementation(Deps.kotlinPoet)
    implementation(Deps.picoCli)
}

java {
    sourceCompatibility = JavaVersion.toVersion(Versions.jvmTarget)
    targetCompatibility = JavaVersion.toVersion(Versions.jvmTarget)
}

kotlin {
    explicitApi = null
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
    useJUnitPlatform() // Enable JUnit5
    jvmArgs!!.addAll(listOf("-Duser.language=en", "-Duser.country=US"))
    maxHeapSize = "4g"
    testLogging {
        events.add(TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
    }
}

application {
    applicationName = "sprout"
    mainClass.set("org.partiql.sprout.SproutKt")
}

distributions {
    main {
        distributionBaseName.set("sprout")
    }
}

tasks.register<GradleBuild>("install") {
    tasks = listOf("assembleDist", "distZip", "installDist")
}
