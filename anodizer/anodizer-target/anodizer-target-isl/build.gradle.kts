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
    implementation("com.amazon.ion:ion-element:1.2.0")
    // implementation("net.pearx.kasechange:kasechange:1.3.0")
    // implementation("com.samskivert:jmustache:1.16")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
}
