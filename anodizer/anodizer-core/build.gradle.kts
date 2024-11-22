plugins {
    id("anodizer-conventions")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

dependencies {
    api("com.amazon.ion:ion-element:1.2.0") // api for Options, but could be removed.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlin.test.junit5)
}

tasks.test {
    useJUnitPlatform() // Enable JUnit5
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
}
