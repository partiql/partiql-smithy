plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "anodizer"

include(
    // anodizer
    "anodizer",
    "anodizer-lang",
    "anodizer-core",
    "anodizer-target:anodizer-target-isl",
    "anodizer-target:anodizer-target-kotlin",
    "anodizer-target-codegen",
    // smithy
    "smithy-anodizer:smithy-anodizer",
    "smithy-anodizer:smithy-anodizer-isl",
    "smithy-anodizer:smithy-anodizer-kotlin",
    "smithy-anodizer:smithy-anodizer-rust",
    "smithy-anodizer-aws:smithy-anodizer-aws-kotlin",
    "smithy-anodizer-aws:smithy-anodizer-aws-rust",
    // examples (integration tests)
    // "examples:smithy-anodizer-example",
    // "examples:smithy-anodizer-example-kotlin",
    // "examples:smithy-anodizer-example-kotlin-aws",
)
