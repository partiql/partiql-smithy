package org.partiql.tool.ridl.model.load

import org.antlr.v4.runtime.ParserRuleContext
import org.partiql.tool.ridl.antlr.RIDLBaseVisitor
import org.partiql.tool.ridl.antlr.RIDLParser
import org.partiql.tool.ridl.model.Definition
import org.partiql.tool.ridl.model.Name
import org.partiql.tool.ridl.model.Namespace
import org.partiql.tool.ridl.model.Primitive
import org.partiql.tool.ridl.model.RType
import org.partiql.tool.ridl.model.RTypeArray
import org.partiql.tool.ridl.model.RTypeEnum
import org.partiql.tool.ridl.model.RTypeNamed
import org.partiql.tool.ridl.model.RTypePrimitive
import org.partiql.tool.ridl.model.RTypeRef
import org.partiql.tool.ridl.model.RTypeStruct
import org.partiql.tool.ridl.model.RTypeUnion
import org.partiql.tool.ridl.model.RTypeUnit
import org.partiql.tool.ridl.model.Type

/**
 * Helper function to turn the ANTLR tree to a definition tree.
 */
internal object Definitions {

    fun build(tree: RIDLParser.DocumentContext, root: Symbol): List<Definition> {
        // unpack top-level namespace
        return DVisitor(root, root).visitDocument(tree).definitions
    }

    /**
     * Walk through the symbol tree for name resolution.
     *
     * @property root       Root namespace symbol
     * @property namespace  Current namespace
     */
    private class DVisitor(
        private val root: Symbol,
        private val namespace: Symbol,
    ) : RIDLBaseVisitor<Definition>() {

        override fun visitDocument(ctx: RIDLParser.DocumentContext) = visitBody(ctx.body())

        override fun visitDefinitionType(ctx: RIDLParser.DefinitionTypeContext): Type {
            val name = namespace.get(ctx, ctx.NAME().text).toName()
            val definition = TVisitor(root, name, namespace).visitType(ctx.type())
            return Type(name, definition)
        }

        override fun visitDefinitionNamespace(ctx: RIDLParser.DefinitionNamespaceContext): Namespace {
            val curr = namespace.get(ctx, ctx.NAME().text)
            return DVisitor(root, curr).visitBody(ctx.body())
        }

        override fun visitBody(ctx: RIDLParser.BodyContext): Namespace {
            val name = namespace.toName()
            val definitions = ctx.definition().map { visit(it) }
            return Namespace(name, definitions)
        }
    }

    /**
     * Produce an RType.
     *
     * @property root       Root namespace symbol
     * @property name       Current symbol name
     * @property namespace  Current namespace
     */
    private class TVisitor(
        private val root: Symbol,
        private val name: Name,
        private val namespace: Symbol,
    ) : RIDLBaseVisitor<RType>() {

        override fun visitTypeNamed(ctx: RIDLParser.TypeNamedContext): RTypeNamed {
            val path = ctx.NAME().map { it.text!! }.toTypedArray()
            val curr = if (ctx.root != null) root else namespace
            val symbol = curr.find(path)
            if (symbol == null) {
                val fullname = path.joinToString("::")
                val namespace = namespace.toString()
                error("Could not resolve name `$fullname` in namespace `${namespace}` at ${Location.of(ctx)}")
            }
            return RTypeNamed(symbol.toName())
        }

        override fun visitTypePrimitive(ctx: RIDLParser.TypePrimitiveContext): RTypePrimitive {
            val kind = try {
                Primitive.valueOf(ctx.text.uppercase())
            } catch (ex: IllegalArgumentException) {
                error("Unknown primitive type `${ctx.text}` at ${Location.of(ctx)}")
            }
            return RTypePrimitive(kind)
        }

        override fun visitTypeArrayNamed(ctx: RIDLParser.TypeArrayNamedContext): RType {
            val type = visitTypeNamed(ctx.typeNamed())
            val size = ctx.INTEGER()?.text?.toInt()
            return RTypeArray(type, size)
        }

        override fun visitTypeArrayPrimitive(ctx: RIDLParser.TypeArrayPrimitiveContext): RType {
            val type = visitTypePrimitive(ctx.typePrimitive())
            val size = ctx.INTEGER()?.text?.toInt()
            return RTypeArray(type, size)
        }

        override fun visitTypeStruct(ctx: RIDLParser.TypeStructContext): RTypeStruct {
            val fields = ctx.typeStructField().map {
                val name = it.NAME().text
                val type = visitType(it.type())
                if (type !is RTypeRef) {
                    error("Inline definitions are currently not supported: ${Location.of(ctx)}")
                }
                RTypeStruct.Field(name, type)
            }
            return RTypeStruct(fields)
        }

        override fun visitTypeUnion(ctx: RIDLParser.TypeUnionContext): RTypeUnion {
            // define variants in the namespace created by the union
            val curr = namespace.get(ctx, name.name)
            val visitor = TVisitor(root, name, curr)
            val variants = ctx.typeUnionVariant().map {
                val name = curr.get(it, it.NAME().text).toName()
                val type = visitor.visitType(it.type())
                Type(name, type)
            }
            return RTypeUnion(variants)
        }

        override fun visitTypeEnum(ctx: RIDLParser.TypeEnumContext) = RTypeEnum(ctx.ENUMERATOR().map { it.text })

        override fun visitTypeUnit(ctx: RIDLParser.TypeUnitContext) = RTypeUnit
    }

    private fun Symbol.get(ctx: ParserRuleContext, name: String): Symbol {
        val location = Location.of(ctx)
        val symbol = this.find(arrayOf(name))
        if (symbol == null) {
            error("Name `$name` was not found; appeared at $location")
        }
        return symbol
    }
}

