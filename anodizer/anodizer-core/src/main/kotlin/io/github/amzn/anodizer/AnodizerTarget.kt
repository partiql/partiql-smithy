package io.github.amzn.anodizer

import io.github.amzn.anodizer.core.File

/**
 * Target for code generation: (model) –> file
 */
public interface AnodizerTarget {

    public fun getName(): String

    public fun generate(model: AnodizerModel, options: AnodizerOptions): File
}
