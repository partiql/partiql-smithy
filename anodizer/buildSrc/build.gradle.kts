plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    // maven {
    //     name = "kotlinRepoTools"
    //     url = java.net.URI("https://d2gys1nrxnjnyg.cloudfront.net/releases")
    //     content {
    //         includeGroupByRegex("""aws\.sdk\.kotlin.*""")
    //     }
    // }
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
}
