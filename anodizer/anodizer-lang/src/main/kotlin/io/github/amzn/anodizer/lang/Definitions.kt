package io.github.amzn.anodizer.lang

import io.github.amzn.anodizer.antlr.RIDLBaseVisitor
import io.github.amzn.anodizer.antlr.RIDLParser
import io.github.amzn.anodizer.core.Definition
import io.github.amzn.anodizer.core.Definition.Typedef
import io.github.amzn.anodizer.core.Type
import org.antlr.v4.runtime.ParserRuleContext
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.collections.toTypedArray
import kotlin.text.toInt

/**
 * Helper function to turn the ANTLR AST to a RIDL definition graph.
 */
internal object Definitions {

    @JvmStatic
    fun load(tree: RIDLParser.DocumentContext, root: Symbol): List<Definition> {
        val visitor = DVisitor(root, root)
        val domain = visitor.visit(tree) as Definition.Namespace
        return domain.definitions
    }

    internal fun Symbol.get(ctx: ParserRuleContext, name: String): Symbol {
        val location = Location.of(ctx)
        val symbol = find(arrayOf(name))
        if (symbol == null) {
            error("Name `$name` was not found; appeared at $location")
        }
        return symbol
    }

    /**
     * Walk through the symbols adding all type definitions to the types map.
     *
     * Note, not every
     *
     * @property root           The domain root symbol.
     * @property curr           The current namespace symbol.
     */
    private class DVisitor(
        @JvmField val root: Symbol,
        @JvmField val curr: Symbol,
    ) : RIDLBaseVisitor<Definition>() {

        /**
         * Scope into the next namespace.
         */
        fun scope(next: Symbol) = DVisitor(root, next)

        override fun visitDocument(ctx: RIDLParser.DocumentContext) = visitBody(ctx.body())

        override fun visitBody(ctx: RIDLParser.BodyContext): Definition {
            val name = curr.name
            val definitions = ctx.definition().map { visit(it) }
            return Definition.Namespace(name, definitions)
        }

        override fun visitDefinitionNamespace(ctx: RIDLParser.DefinitionNamespaceContext): Definition {
            val symbol = curr.get(ctx, ctx.NAME().text)
            return scope(symbol).visitBody(ctx.body())
        }

        override fun visitDefinitionAlias(ctx: RIDLParser.DefinitionAliasContext): Definition {
            val symbol = curr.get(ctx, ctx.NAME().text)
            val type = TVisitor(root, curr).visit(ctx.type())
            return Definition.Alias(symbol.name, type)
        }

        override fun visitDefinitionEnum(ctx: RIDLParser.DefinitionEnumContext): Definition {
            val symbol = curr.get(ctx, ctx.NAME().text)
            val values = ctx.enum_().ENUMERAL().map { it.text }
            return Definition.Enum(symbol.name, values)
        }

        override fun visitDefinitionStruct(ctx: RIDLParser.DefinitionStructContext): Definition {
            val symbol = curr.get(ctx, ctx.NAME().text)
            val fields = ctx.struct().field().map {
                val fName = it.NAME().text
                val fType = TVisitor(root, curr).visit(it.type())
                Definition.Struct.Field(fName, fType)
            }
            return Definition.Struct(symbol.name, fields)
        }

        override fun visitDefinitionUnion(ctx: RIDLParser.DefinitionUnionContext): Definition {
            val symbol = curr.get(ctx, ctx.NAME().text)
            val visitor = scope(symbol)
            val variants = ctx.union().variant().map { visitor.visit(it) as Typedef }
            return Definition.Union(symbol.name, variants)
        }

        override fun visitVariantUnit(ctx: RIDLParser.VariantUnitContext): Typedef {
            val symbol = curr.get(ctx, ctx.NAME().text)
            return Definition.Alias(symbol.name, Type.Unit)
        }

        override fun visitVariantType(ctx: RIDLParser.VariantTypeContext): Typedef {
            val symbol = curr.get(ctx, ctx.NAME().text)
            val type = TVisitor(root, curr).visit(ctx.type())
            return Definition.Alias(symbol.name, type)
        }

        override fun visitVariantEnum(ctx: RIDLParser.VariantEnumContext): Typedef {
            val symbol = curr.get(ctx, ctx.NAME().text)
            val values = ctx.enum_().ENUMERAL().map { it.text }
            return Definition.Enum(symbol.name, values)
        }

        override fun visitVariantStruct(ctx: RIDLParser.VariantStructContext): Typedef {
            val symbol = curr.get(ctx, ctx.NAME().text)
            val fields = ctx.struct().field().map {
                val fName = it.NAME().text
                val fType = TVisitor(root, curr).visit(it.type())
                Definition.Struct.Field(fName, fType)
            }
            return Definition.Struct(symbol.name, fields)
        }

        override fun visitVariantUnion(ctx: RIDLParser.VariantUnionContext): Typedef {
            val symbol = curr.get(ctx, ctx.NAME().text)
            val visitor = scope(symbol)
            val variants = ctx.union().variant().map { visitor.visit(it) as Typedef }
            return Definition.Union(symbol.name, variants)
        }
    }

    /**
     * Walk through the symbols adding all type definitions to the types map.
     *
     * @property root   The domain root symbol.
     * @property curr   The current namespace symbol.
     */
    private class TVisitor(
        private val root: Symbol,
        private val curr: Symbol,
    ) : RIDLBaseVisitor<Type>() {

        override fun visitTypeNamed(ctx: RIDLParser.TypeNamedContext): Type.Named {
            val path = ctx.NAME().map { it.text!! }.toTypedArray()
            val namespace = if (ctx.root != null) root else curr
            val symbol = namespace.find(path)
            if (symbol == null) {
                val name = path.joinToString("::")
                error("Could not resolve name `$name` in namespace `$namespace` at ${Location.of(ctx)}")
            }
            return Type.Named(symbol.name)
        }

        override fun visitTypeBool(ctx: RIDLParser.TypeBoolContext) = Type.Primitive.Bool

        override fun visitTypeInt(ctx: RIDLParser.TypeIntContext) = Type.Primitive.Int

        override fun visitTypeFloat(ctx: RIDLParser.TypeFloatContext) = Type.Primitive.Float

        override fun visitTypeDecimal(ctx: RIDLParser.TypeDecimalContext): Type.Primitive {
            val precision = ctx.p?.text?.toIntOrNull()
            val exponent = ctx.e?.text?.toIntOrNull()
            return Type.Primitive.Decimal(precision, exponent)
        }

        override fun visitTypeString(ctx: RIDLParser.TypeStringContext) = Type.Primitive.String

        override fun visitTypeBlob(ctx: RIDLParser.TypeBlobContext): Type.Primitive {
            val size: Int? = ctx.INT()?.text?.toInt()
            return Type.Primitive.Blob(size)
        }

        override fun visitTypeClob(ctx: RIDLParser.TypeClobContext): Type.Primitive {
            val size: Int? = ctx.INT()?.text?.toInt()
            return Type.Primitive.Clob(size)
        }

        override fun visitTypeArrayNamed(ctx: RIDLParser.TypeArrayNamedContext): Type.Array {
            val type = visit(ctx.typeNamed())
            val size = ctx.INT()?.text?.toInt()
            return Type.Array(type, size)
        }

        override fun visitTypeArrayPrimitive(ctx: RIDLParser.TypeArrayPrimitiveContext): Type.Array {
            val type = visit(ctx.typePrimitive())
            val size = ctx.INT()?.text?.toInt()
            return Type.Array(type, size)
        }

        override fun visitTypeArrayNested(ctx: RIDLParser.TypeArrayNestedContext): Type.Array {
            val type = visit(ctx.typeArray())
            val size = ctx.INT()?.text?.toInt()
            return Type.Array(type, size)
        }

        override fun visitTypeUnit(ctx: RIDLParser.TypeUnitContext) = Type.Unit

        private fun String.toIntOrNull(): Int? = try {
            toInt()
        } catch (_: NumberFormatException) {
            null
        }
    }
}
