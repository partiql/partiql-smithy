package io.github.amzn.anodizer.lang

import io.github.amzn.anodizer.antlr.RIDLBaseVisitor
import io.github.amzn.anodizer.antlr.RIDLParser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import kotlin.collections.find

/**
 * Helper function to turn the ANTLR AST to a Symbol tree for name resolution.
 */
internal object Symbols {

    @JvmStatic
    fun load(tree: RIDLParser.DocumentContext, domain: String): Symbol {
        val root = Symbol.root(domain)
        Visitor(root).visit(tree)
        return root
    }

    private class Visitor(val parent: Symbol) : RIDLBaseVisitor<Unit>() {

        override fun visitDefinitionNamespace(ctx: RIDLParser.DefinitionNamespaceContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.NAMESPACE)
        }

        override fun visitDefinitionAlias(ctx: RIDLParser.DefinitionAliasContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.TYPE)
        }

        override fun visitDefinitionEnum(ctx: RIDLParser.DefinitionEnumContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.TYPE)
        }

        override fun visitDefinitionStruct(ctx: RIDLParser.DefinitionStructContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.TYPE)
        }

        override fun visitDefinitionUnion(ctx: RIDLParser.DefinitionUnionContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.TYPE)
        }

        override fun visitVariantUnit(ctx: RIDLParser.VariantUnitContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.TYPE)
        }

        override fun visitVariantType(ctx: RIDLParser.VariantTypeContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.TYPE)
        }

        override fun visitVariantEnum(ctx: RIDLParser.VariantEnumContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.TYPE)
        }

        override fun visitVariantStruct(ctx: RIDLParser.VariantStructContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.TYPE)
        }

        override fun visitVariantUnion(ctx: RIDLParser.VariantUnionContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.TYPE)
        }

        private fun define(ctx: ParserRuleContext, name: TerminalNode, kind: Symbol.Kind) {
            val location = Location.of(ctx)
            // check if definition already exists
            val existing = parent.children.find { it.text == name.text }
            if (existing != null) {
                error("Duplicate name `${name.text}` found at $location — previously defined at ${existing.location}")
            }
            // link child to parent
            val child = Symbol(name.text, kind, location, parent)
            // link parent to child
            parent.children.add(child)
            // descend
            Visitor(child).visitChildren(ctx)
        }
    }
}
