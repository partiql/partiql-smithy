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
 *
 */

plugins {
    kotlin("jvm") version "1.6.20"
}

object Versions {
    // Language
    const val kotlin = "1.6.20"
    const val kotlinLanguage = "1.6"
    const val kotlinApi = "1.6"
    const val jvmTarget = "1.8"
    // Deps
    const val partiql = "0.14.3"
}

object Deps {
    const val partiql = "org.partiql:partiql-lang-kotlin:${Versions.partiql}"
}

repositories {
    mavenCentral()
}

dependencies {
    api(Deps.partiql)
}

java {
    sourceCompatibility = JavaVersion.toVersion(Versions.jvmTarget)
    targetCompatibility = JavaVersion.toVersion(Versions.jvmTarget)
    withJavadocJar()
    withSourcesJar()
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = Versions.jvmTarget
    kotlinOptions.apiVersion = Versions.kotlinApi
    kotlinOptions.languageVersion = Versions.kotlinLanguage
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

kotlin {
    explicitApi = null
}
