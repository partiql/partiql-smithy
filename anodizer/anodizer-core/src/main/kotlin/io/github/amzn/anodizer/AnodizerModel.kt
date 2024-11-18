package io.github.amzn.anodizer

import io.github.amzn.anodizer.core.Definition

/**
 * Top-level model of an anodizer domain (collection of types).
 *
 * @property domain         The top-level namespace name.
 * @property definitions    The top-level definitions.
 */
public class AnodizerModel(
    @JvmField public val domain: String,
    @JvmField public val definitions: List<Definition>,
) {

    /**
     * Return a debug string.
     */
    override fun toString(): String = buildString {
        appendLine(domain)
        appendLine("-----------------------------")
        for (d in definitions) {
            appendLine(d)
        }
    }
}
