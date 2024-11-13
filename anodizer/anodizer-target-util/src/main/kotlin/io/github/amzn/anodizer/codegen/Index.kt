package io.github.amzn.anodizer.codegen

/**
 * Index definitions by their tags to lookup aliases.
 */
internal class Index private constructor(private val definitions: Map<String, Context.Typedef>) {

    fun get(tag: String): Context.Typedef =
        definitions[tag] ?: error("Definition $tag was missing from the definitions map.")

    companion object {

        fun of(domain: Context.Domain): Index {
            // 1. Collect all aliases
            val aliases = mutableMapOf<String, Context.Type>()
            Collector(aliases).visit(domain, Unit)
            // 2. Add all types to definitions map; using base definitions rather than aliases.
            val definitions = mutableMapOf<String, Context.Typedef>()
            Rebaser(aliases, definitions).visit(domain, Unit)
            return Index(definitions)
        }
    }

    /**
     * Walk definition tree collecting all aliases.
     */
    private class Collector(private val aliases: MutableMap<String, Context.Type>) : Context.Visitor<Unit, Unit>() {

        override fun defaultReturn(node: Context.Node, ctx: Unit) = Unit

        override fun visitAlias(alias: Context.Alias, context: Unit) {
            aliases[alias.symbol.tag] = alias.type
        }
    }

    private class Rebaser(
        private val aliases: Map<String, Context.Type>,
        private val definitions: MutableMap<String, Context.Typedef>,
    ) : Context.Visitor<Unit, Unit>() {

        override fun defaultReturn(node: Context.Node, ctx: Unit) = Unit

        override fun visitStruct(struct: Context.Struct, ctx: Unit) {
            definitions[struct.symbol.tag] = struct
        }

        override fun visitEnum(enum: Context.Enum, ctx: Unit) {
            definitions[enum.symbol.tag] = enum
        }

        override fun visitUnion(union: Context.Union, ctx: Unit) {
            definitions[union.symbol.tag] = union
        }

        override fun visitAlias(alias: Context.Alias, context: Unit) {
            val type = alias.type
            if (type is Context.Named) {
                var next = aliases[type.symbol.tag]
                while (next != null) {
                    if (next !is Context.Named) break // hit a non-named type
                    next = aliases[next.symbol.tag]
                }
            }
            definitions[alias.symbol.tag] = alias
        }
    }
}
