package io.github.amzn.ridl.model

import io.github.amzn.ridl.model.load.Loader
import io.github.amzn.ridl.model.load.Lower
import java.nio.file.Path

/**
 * Top-level model of a grammar
 */
public class Document(
    @JvmField public val definitions: List<Definition>,
) {

    public companion object {

        @JvmStatic
        @JvmOverloads
        public fun load(input: String, include: Path? = null): Document = Loader.load(input, include)
    }

    /**
     * Remove all aliases and replace references to the alias with its base.
     */
    public fun lower(): Document = Lower.rewrite(this)
}
