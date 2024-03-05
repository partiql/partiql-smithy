package org.partiql.tool.ridl.model

import org.partiql.tool.ridl.parser.Parser
import java.nio.file.Path

/**
 * Top-level model of a grammar
 */
public class Document(
    public val definitions: List<Definition>,
) {

    public companion object {

        @JvmStatic
        @JvmOverloads
        public fun load(input: String, include: Path? = null): Document = Parser.load(input, include)
    }
}
