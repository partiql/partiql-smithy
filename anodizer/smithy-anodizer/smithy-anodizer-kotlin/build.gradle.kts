plugins {
    id("anodizer-conventions")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

dependencies {
    api("software.amazon.smithy.kotlin:smithy-kotlin-codegen:0.33.19")
    implementation(project(":smithy-anodizer:smithy-anodizer"))
    implementation(project(":anodizer-target:anodizer-target-kotlin"))
    implementation("net.pearx.kasechange:kasechange:1.3.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
}
