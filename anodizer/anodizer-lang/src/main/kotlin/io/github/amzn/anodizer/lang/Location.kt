package io.github.amzn.anodizer.lang

import org.antlr.v4.runtime.ParserRuleContext

internal class Location(
    @JvmField val line: Int,
    @JvmField val offset: Int,
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
