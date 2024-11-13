plugins {
    id("anodizer-conventions")
    id("software.amazon.smithy.gradle.smithy-base").version("1.1.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.test {
    useJUnitPlatform() // Enable JUnit5
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
}

dependencies {
    // smithy cli classpath
    smithyBuild(project(":smithy-anodizer:smithy-anodizer"))
    smithyBuild(project(":smithy-anodizer:smithy-anodizer-isl"))
}

smithy {
    // configure smithy
}

dependencies {
    implementation(kotlin("stdlib"))
    // testing
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.kotlin.test.junit5)
}

