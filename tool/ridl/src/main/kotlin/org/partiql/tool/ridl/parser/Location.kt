package org.partiql.tool.ridl.parser

import org.antlr.v4.runtime.ParserRuleContext

internal data class Location(
    val line: Int,
    val offset: Int,
) {

    override fun toString(): String = "line $line [$offset,]"

    companion object {

        fun of(ctx: ParserRuleContext): Location {
            val line = ctx.start.line
            val offset = ctx.start.charPositionInLine
            return Location(line, offset)
        }
    }
}
