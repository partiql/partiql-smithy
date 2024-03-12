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
    `maven-publish`
}

val group: String by project
val version: String by project

object Versions {
    // Language
    const val kotlin = "1.6.20"
    const val kotlinLanguage = "1.6"
    const val kotlinApi = "1.6"
    const val jvmTarget = "1.8"
    // Deps
    const val partiql = "1.0.0-perf.1"
}

object Deps {
    const val partiql = "org.partiql:partiql-types:${Versions.partiql}"
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

publishing {

    publications {
        create<MavenPublication>("main") {
            artifactId = "partiql-plan"
            from(components["java"])
            pom {
                name.set("PartiQL Plan")
                description.set("PartiQL Plan experimental data structures")
                url.set("https://partiql.org")
                packaging = "jar"
                groupId = group
                version = version
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("PartiQL Team")
                        email.set("partiql-dev@amazon.com")
                        organization.set("PartiQL")
                        organizationUrl.set("https://github.com/partiql")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            url = uri("https://aws.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                val ossrhUsername: String by project
                val ossrhPassword: String by project
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}
