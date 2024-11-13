plugins {
    id("anodizer-conventions")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
    implementation("com.amazon.ion:ion-element:1.2.0")
}
