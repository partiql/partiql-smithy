import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    // smithy-kotlin
    smithyBuild("software.amazon.smithy.kotlin:smithy-kotlin-codegen:0.33.1")
    // smithy-anodizer-kotlin
    smithyBuild(project(":smithy-anodizer:smithy-anodizer-kotlin"))
}

smithy {
    // configure smithy
}

val generatedSrc: Directory = project.layout.buildDirectory.dir("generated-src").get()

val copyGeneratedSources = tasks.register("copyGenerateSources") {
    dependsOn(tasks.smithyBuild)
    outputs.dir(generatedSrc)
    outputs.upToDateWhen { false }
    doLast {
        val src = smithy.outputDirectory.dir("source").get()
        copy {
            from(src.dir("kotlin-codegen/src/main/kotlin"))
            into(generatedSrc)
        }
    }
}

dependencies {
    // generated-src
    implementation("aws.smithy.kotlin:telemetry-defaults:1.3.1")
    implementation("com.amazon.ion:ion-element:1.2.0")
    api("aws.smithy.kotlin:runtime-core:1.3.1")
    api("aws.smithy.kotlin:smithy-client:1.3.1")
    api("aws.smithy.kotlin:telemetry-api:1.3.1")
    // testing
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.kotlin.test.junit5)
}

sourceSets {
    main {
        java.srcDir(generatedSrc)
    }
}

kotlin.sourceSets {
    all {
        languageSettings.optIn("kotlin.RequiresOptIn")
        languageSettings.optIn("aws.smithy.kotlin.runtime.InternalApi")
    }
    main {
        kotlin.srcDir(generatedSrc)
    }
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn(copyGeneratedSources)
}

tasks.clean.configure {
    delete(project.layout.projectDirectory.dir("generated-src"))
}
