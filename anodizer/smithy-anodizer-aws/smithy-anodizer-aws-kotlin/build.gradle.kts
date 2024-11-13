plugins {
    id("anodizer-conventions")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    api(project(":smithy-anodizer:smithy-anodizer-kotlin"))
    api("software.amazon.smithy.kotlin:smithy-aws-kotlin-codegen:0.33.19")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
}
