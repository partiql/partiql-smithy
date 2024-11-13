import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}
