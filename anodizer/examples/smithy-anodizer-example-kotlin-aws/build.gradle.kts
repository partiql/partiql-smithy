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
    // smithy cli classpath
    smithyBuild("software.amazon.smithy.kotlin:smithy-kotlin-codegen:0.33.1")
    smithyBuild("software.amazon.smithy.kotlin:smithy-aws-kotlin-codegen:0.33.1")

    smithyBuild(project(":smithy-anodizer:smithy-anodizer")) // TODO replace with smithy-anodizer-kotlin
    smithyBuild(project(":anodizer-target:anodizer-target-isl"))
    smithyBuild(project(":anodizer-target:anodizer-target-kotlin"))
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
            from(src.dir("anodizer/kotlin/src/main/kotlin"))
            into(generatedSrc)
        }
    }
}

dependencies {

    // model classpath
    implementation("software.amazon.smithy:smithy-aws-traits:1.52.1")
    implementation(project(":smithy-anodizer-aws:smithy-anodizer-aws-kotlin"))

    // generated-src (smithy-kotlin)
    implementation(kotlin("stdlib"))
    implementation("aws.smithy.kotlin:aws-json-protocols:1.3.1")
    implementation("aws.smithy.kotlin:aws-protocol-core:1.3.1")
    implementation("aws.smithy.kotlin:http:1.3.1")
    implementation("aws.smithy.kotlin:http-auth:1.3.1")
    implementation("aws.smithy.kotlin:http-client-engine-default:1.3.1")
    implementation("aws.smithy.kotlin:identity-api:1.3.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
    implementation("aws.smithy.kotlin:serde:1.3.1")
    implementation("aws.smithy.kotlin:serde-json:1.3.1")
    implementation("aws.smithy.kotlin:telemetry-defaults:1.3.1")
    api("aws.smithy.kotlin:http-client:1.3.1")
    api("aws.smithy.kotlin:runtime-core:1.3.1")
    api("aws.smithy.kotlin:smithy-client:1.3.1")
    api("aws.smithy.kotlin:telemetry-api:1.3.1")

    // generated-src (anodizer-kotlin)
    // implementation(":anodizer-runtime:anodizer-runtime-kotlin")
    implementation("com.amazon.ion:ion-element:1.2.0")

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
