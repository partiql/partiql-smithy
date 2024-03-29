package io.github.amzn.ridl.model.load

import io.github.amzn.ridl.antlr.RIDLBaseVisitor
import io.github.amzn.ridl.antlr.RIDLParser
import io.github.amzn.ridl.model.Definition
import io.github.amzn.ridl.model.Name
import io.github.amzn.ridl.model.Namespace
import io.github.amzn.ridl.model.Primitive
import io.github.amzn.ridl.model.RType
import io.github.amzn.ridl.model.RTypeArray
import io.github.amzn.ridl.model.RTypeEnum
import io.github.amzn.ridl.model.RTypeNamed
import io.github.amzn.ridl.model.RTypePrimitive
import io.github.amzn.ridl.model.RTypeRef
import io.github.amzn.ridl.model.RTypeStruct
import io.github.amzn.ridl.model.RTypeUnion
import io.github.amzn.ridl.model.RTypeUnit
import io.github.amzn.ridl.model.Type
import org.antlr.v4.runtime.ParserRuleContext

/**
 * Helper function to turn the ANTLR AST to a definition tree.
 */
internal class Definitions(
    private val definitions: List<Definition>,
    private val aliases: Map<Name, RTypeRef>,
) {

    companion object {

        @JvmStatic
        fun build(tree: RIDLParser.DocumentContext, root: Symbol): Definitions {
            // collect aliases when visiting definitions
            val aliases = mutableMapOf<Name, RTypeRef>()
            // unpack top-level namespace
            val definitions = DVisitor(root, root, aliases).visitDocument(tree).definitions
            return Definitions(definitions, aliases)
        }

        internal fun Symbol.get(ctx: ParserRuleContext, name: String): Symbol {
            val location = Location.of(ctx)
            val symbol = find(arrayOf(name))
            if (symbol == null) {
                error("Name `$name` was not found; appeared at $location")
            }
            return symbol
        }
    }

    fun rebase(): List<Definition> = Rebase.apply(definitions, aliases)

    /**
     * Walk through the symbol tree for name resolution.
     *
     * @property root       Root namespace symbol
     * @property namespace  Current namespace
     */
    private class DVisitor(
        private val root: Symbol,
        private val namespace: Symbol,
        private val aliases: MutableMap<Name, RTypeRef>,
    ) : RIDLBaseVisitor<Definition>() {

        override fun visitDocument(ctx: RIDLParser.DocumentContext) = visitBody(ctx.body())

        override fun visitDefinitionType(ctx: RIDLParser.DefinitionTypeContext): Type {
            val name = namespace.get(ctx, ctx.NAME().text).toName()
            val definition = TVisitor(root, name, namespace).visitType(ctx.type())
            if (definition is RTypeRef) {
                // this is an alias; as we are assigning a name to an existing type.
                aliases[name] = definition
            }
            return Type(name, definition)
        }

        override fun visitDefinitionNamespace(ctx: RIDLParser.DefinitionNamespaceContext): Namespace {
            val curr = namespace.get(ctx, ctx.NAME().text)
            return DVisitor(root, curr, aliases).visitBody(ctx.body())
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
            return RTypeNamed(symbol.toName(), null)
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
            val variants = ctx.typeUnionVariant().map {
                val name = curr.get(it, it.NAME().text).toName()
                val type = TVisitor(root, name, curr).visitType(it.type())
                Type(name, type)
            }
            return RTypeUnion(variants)
        }

        override fun visitTypeEnum(ctx: RIDLParser.TypeEnumContext) = RTypeEnum(ctx.ENUMERATOR().map { it.text })

        override fun visitTypeUnit(ctx: RIDLParser.TypeUnitContext) = RTypeUnit
    }
}
