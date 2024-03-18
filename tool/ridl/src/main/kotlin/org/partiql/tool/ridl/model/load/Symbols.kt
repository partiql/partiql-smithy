package org.partiql.tool.ridl.model.load

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.partiql.tool.ridl.antlr.RIDLBaseVisitor
import org.partiql.tool.ridl.antlr.RIDLParser

/**
 * Helper function to turn the ANTLR AST to a Symbol tree for name resolution.
 */
internal object Symbols {

    @JvmStatic
    fun build(tree: RIDLParser.DocumentContext): Symbol {
        val root = Symbol.root()
        Visitor(root).visit(tree)
        return root
    }

    private class Visitor(val parent: Symbol) : RIDLBaseVisitor<Unit>() {

        override fun visitDefinitionType(ctx: RIDLParser.DefinitionTypeContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.TYPE)
        }

        override fun visitDefinitionNamespace(ctx: RIDLParser.DefinitionNamespaceContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.NAMESPACE)
        }

        override fun visitTypeStructField(ctx: RIDLParser.TypeStructFieldContext) {
            val type = ctx.type()
            val inline = type.typeStruct() ?: type.typeUnion() ?: type.typeEnum()
            if (inline != null) {
                define(inline, ctx.NAME(), Symbol.Kind.TYPE)
            }
        }

        override fun visitTypeUnionVariant(ctx: RIDLParser.TypeUnionVariantContext) {
            define(ctx, ctx.NAME(), Symbol.Kind.TYPE)
        }

        private fun define(ctx: ParserRuleContext, name: TerminalNode, kind: Symbol.Kind) {
            val location = Location.of(ctx)
            // check if definition already exists
            val existing = parent.children.find { it.name == name.text }
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
