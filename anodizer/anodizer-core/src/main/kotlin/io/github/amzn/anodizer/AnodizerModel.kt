package io.github.amzn.anodizer

import io.github.amzn.anodizer.core.Definition

/**
 * Top-level model of an anodizer type domain.
 *
 * @property domain         The top-level namespace name.
 * @property definitions    The top-level definitions.
 */
public class AnodizerModel(
    @JvmField public val domain: String,
    @JvmField public val definitions: List<Definition>,
) {

    override fun toString(): String = buildString {
        appendLine("Domain: $domain")
        appendLine("Types:")
        for (d in definitions) {
            append("‣ ")
            append(d)
            appendLine()
        }
    }

    /**‣
     * Remove all aliases and replace references to the alias with its base.
     */
    public fun normalize(): AnodizerModel {
        // TODO
        //  1. Remove all aliases.
        //  2. Move all inlines to top-level of current namespace.
        return this
    }
}
