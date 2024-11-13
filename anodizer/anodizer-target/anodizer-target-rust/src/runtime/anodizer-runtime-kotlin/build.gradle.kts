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
    application
}

object Versions {
    // Language
    const val kotlin = "1.6.20"
    const val kotlinLanguage = "1.6"
    const val kotlinApi = "1.6"
    const val jvmTarget = "1.8"
    // Dependencies
    const val ionElement = "1.0.0"
    // Testing
    const val junit5 = "5.9.3"
}

object Deps {
    // Language
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    // Dependencies
    const val ionElement = "com.amazon.ion:ion-element:${Versions.ionElement}"
    // Test
    const val junitApi = "org.junit.jupiter:junit-jupiter-api:${Versions.junit5}"
    const val junitParams = "org.junit.jupiter:junit-jupiter-params:${Versions.junit5}"
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
    const val kotlinTestJunit = "org.jetbrains.kotlin:kotlin-test-junit5:${Versions.kotlin}"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Deps.ionElement)
    // Test
    testImplementation(Deps.kotlinTest)
    testImplementation(Deps.kotlinTestJunit)
    testImplementation(Deps.junitParams)
}

java {
    sourceCompatibility = JavaVersion.toVersion(Versions.jvmTarget)
    targetCompatibility = JavaVersion.toVersion(Versions.jvmTarget)
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
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
}
