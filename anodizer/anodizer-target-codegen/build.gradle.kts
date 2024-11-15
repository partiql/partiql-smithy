plugins {
    id("anodizer-conventions")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

dependencies {
    api(project(":anodizer-core"))
    api("net.pearx.kasechange:kasechange:1.3.0")
    api("com.samskivert:jmustache:1.16")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
    // test
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.kotlin.test.junit5)
}
