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
    implementation(project(":anodizer-lang"))
    implementation(libs.smithy.codegen.core)
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
    implementation("net.pearx.kasechange:kasechange:1.3.0")
}
