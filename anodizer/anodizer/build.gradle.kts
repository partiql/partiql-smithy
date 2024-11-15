plugins {
    id("anodizer-conventions")
    application
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation(project(":anodizer-core"))
    implementation(project(":anodizer-lang"))
    implementation(project(":anodizer-target:anodizer-target-isl"))
    implementation(project(":anodizer-target:anodizer-target-kotlin"))

    implementation("info.picocli:picocli:4.7.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
    // test
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.kotlin.test.junit5)
}

distributions {
    main {
        distributionBaseName.set("anodizer")
    }
}

tasks.register<GradleBuild>("install") {
    tasks = listOf("assembleDist", "distZip", "installDist")
}

application {
    applicationName = "anodizer"
    mainClass.set("io.github.amzn.anodizer.cli.MainKt")
}
