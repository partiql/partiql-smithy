plugins {
    id("anodizer-conventions")
    id("org.gradle.antlr")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

dependencies {
    antlr("org.antlr:antlr4:4.10.1")
    implementation(project(":anodizer-core"))
    implementation("org.antlr:antlr4-runtime:4.10.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
    // test
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.kotlin.test.junit5)
}

sourceSets {
    main {
        java.srcDir("$buildDir/generated-src")
    }
}

kotlin.sourceSets {
    all {
        languageSettings.optIn("kotlin.RequiresOptIn")
    }
    main {
        kotlin.srcDir("$buildDir/generated-src")
    }
}

tasks.generateGrammarSource {
    val antlrPackage = "io.github.amzn.anodizer.antlr"
    val antlrSources = "$buildDir/generated-src/${antlrPackage.replace('.', '/')}"
    maxHeapSize = "64m"
    arguments = listOf("-visitor", "-long-messages", "-package", antlrPackage)
    outputDirectory = File(antlrSources)
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.compileTestKotlin {
    dependsOn(tasks.generateTestGrammarSource)
}

tasks.javadoc {
    exclude("**/antlr/**")
}
