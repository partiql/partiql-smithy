package io.github.amzn.anodizer.codegen

import io.github.amzn.anodizer.codegen.context.Ctx
import io.github.amzn.anodizer.codegen.context.CtxAlias
import io.github.amzn.anodizer.codegen.context.CtxEnum
import io.github.amzn.anodizer.codegen.context.CtxModel
import io.github.amzn.anodizer.codegen.context.CtxNamed
import io.github.amzn.anodizer.codegen.context.CtxStruct
import io.github.amzn.anodizer.codegen.context.CtxType
import io.github.amzn.anodizer.codegen.context.CtxTypedef
import io.github.amzn.anodizer.codegen.context.CtxUnion
import io.github.amzn.anodizer.codegen.context.CtxVisitor

/**
 * Index definitions by their tags to lookup aliases.
 */
internal class Index private constructor(private val definitions: Map<String, CtxTypedef>) {

    fun get(tag: String): CtxTypedef =
        definitions[tag] ?: error("Definition $tag was missing from the definitions map.")

    companion object {

        fun build(model: CtxModel): Index {
            // 1. Collect all aliases
            val aliases = mutableMapOf<String, CtxType>()
            Collector(aliases).visit(model, Unit)
            // 2. Add all types to definitions map; using base definitions rather than aliases.
            val definitions = mutableMapOf<String, CtxTypedef>()
            Rebaser(aliases, definitions).visit(model, Unit)
            return Index(definitions)
        }
    }

    /**
     * Walk definition tree collecting all aliases.
     */
    private class Collector(private val aliases: MutableMap<String, CtxType>) : CtxVisitor<Unit, Unit> {

        override fun defaultReturn(ctx: Ctx, args: Unit) = Unit

        override fun visitAlias(ctx: CtxAlias, args: Unit) {
            aliases[ctx.symbol.tag] = ctx.type
        }
    }

    private class Rebaser(
        private val aliases: Map<String, CtxType>,
        private val definitions: MutableMap<String, CtxTypedef>,
    ) : CtxVisitor<Unit, Unit> {

        override fun defaultReturn(ctx: Ctx, args: Unit) = Unit

        override fun visitStruct(ctx: CtxStruct, args: Unit) {
            definitions[ctx.symbol.tag] = ctx
        }

        override fun visitEnum(ctx: CtxEnum, args: Unit) {
            definitions[ctx.symbol.tag] = ctx
        }

        override fun visitUnion(ctx: CtxUnion, args: Unit) {
            definitions[ctx.symbol.tag] = ctx
        }

        override fun visitAlias(ctx: CtxAlias, args: Unit) {
            val type = ctx.type
            if (type is CtxNamed) {
                var next = aliases[type.symbol.tag]
                while (next != null) {
                    if (next !is CtxNamed) break // hit a non-named type
                    next = aliases[next.symbol.tag]
                }
            }
            definitions[ctx.symbol.tag] = ctx
        }
    }
}
