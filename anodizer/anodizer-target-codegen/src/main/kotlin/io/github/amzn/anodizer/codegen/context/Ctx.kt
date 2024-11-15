package io.github.amzn.anodizer.codegen.context

import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.core.Definition
import io.github.amzn.anodizer.core.Name
import io.github.amzn.anodizer.core.Type
import net.pearx.kasechange.CaseFormat
import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase
import net.pearx.kasechange.toSnakeCase

/**
 * The [Ctx] abstraction is used to make a definition tree amenable for code generation.
 *
 * Q: Why?
 * A: A ctx wraps the core anodizer model with code generation specific information/context.
 *
 * Q: Why not have the model hold the necessary context?
 * A: The model is independent of codegen, hence the separation, but that might be a welcome addition to the model.
 *
 * Q: The word "ctx" and "context" is generic and overused.
 * A: Agreed, it's a tree-node decorator pattern but the "context" keyword is familiar to the codegen space.
 */
public sealed interface Ctx {

    /**
     * Children nodes.
     */
    public fun children(): List<Ctx>

    /**
     * Visitor accept.
     *
     * @param A
     * @param R
     * @param visitor
     * @param args
     * @return
     */
    public fun <A, R> accept(visitor: CtxVisitor<A, R>, args: A): R

    public companion object {

        /**
         * Create a new [CtxModel] from the given [model].
         *
         * @param model
         * @return
         */
        @JvmStatic
        public fun build(model: AnodizerModel): CtxModel {
            return CtxModel(
                domain = name(model.domain),
                definitions = model.definitions.toDefinitionsCtx(null),
            )
        }

        private fun List<Definition>.toDefinitionsCtx(parent: Name?): List<CtxDefinition> {
            return map { it.toDefinitionCtx(parent) }
        }

        private fun List<Definition.Typedef>.toTypedefsCtx(parent: Name?): List<CtxTypedef> {
            return map { it.toTypedefCtx(parent) }
        }

        private fun Definition.toDefinitionCtx(parent: Name?): CtxDefinition = when (this) {
            is Definition.Namespace -> toNamespaceCtx(parent)
            is Definition.Typedef -> toTypedefCtx(parent)
        }

        private fun Definition.Namespace.toNamespaceCtx(parent: Name?): CtxNamespace {
            val namespace = CtxNamespace(
                symbol = this.name.symbol(),
                definitions = this.definitions.toDefinitionsCtx(parent),
            )
            return namespace
        }

        private fun Definition.Typedef.toTypedefCtx(parent: Name?): CtxTypedef = when (this) {
            is Definition.Alias -> toAliasCtx(parent)
            is Definition.Enum -> toEnumCtx(parent)
            is Definition.Struct -> toStructCtx(parent)
            is Definition.Union -> toUnionCtx(parent)
        }

        private fun Definition.Alias.toAliasCtx(parent: Name?): CtxTypedef {
            val alias = CtxAlias(
                symbol = this.name.symbol(),
                type = this.type.toTypeCtx(),
                parent = parent?.symbol(),
                definition = this,
            )
            return alias
        }

        private fun Definition.Enum.toEnumCtx(parent: Name?): CtxTypedef {
            val enum = CtxEnum(
                symbol = this.name.symbol(),
                values = this.values.map { name(it) },
                parent = parent?.symbol(),
                definition = this,
            )
            return enum
        }

        private fun Definition.Struct.toStructCtx(parent: Name?): CtxTypedef {
            val struct = CtxStruct(
                symbol = this.name.symbol(),
                fields = this.fields.map {
                    val n = name(it.name)
                    val t = it.type.toTypeCtx()
                    CtxField(n, t)
                },
                parent = parent?.symbol(),
                definition = this,
            )
            return struct
        }

        private fun Definition.Union.toUnionCtx(parent: Name?): CtxTypedef {
            val union = CtxUnion(
                symbol = this.name.symbol(),
                variants = this.variants.toTypedefsCtx(this.name),
                parent = parent?.symbol(),
                definition = this,
            )
            return union
        }

        private fun Type.toTypeCtx() = when (this) {
            is Type.Named -> toTypeNamedCtx()
            is Type.Array -> toTypeArrayCtx()
            is Type.Primitive -> toTypePrimitiveCtx()
            is Type.Unit -> toTypeUnitCtx()
        }

        private fun Type.Named.toTypeNamedCtx(): CtxType {
            val named = CtxNamed(
                symbol = this.name.symbol(),
                type = this,
            )
            return named
        }

        private fun Type.Array.toTypeArrayCtx(): CtxType {
            val array = CtxArray(
                size = this.size,
                item = this.item.toTypeCtx(),
                type = this,
            )
            return array
        }

        private fun Type.Primitive.toTypePrimitiveCtx(): CtxType {
            val primitive = CtxPrimitive(
                ion = this.ion,
                type = this,
            )
            return primitive
        }

        private fun toTypeUnitCtx(): CtxType = CtxUnit

        private fun Name.symbol(): CtxSymbol = CtxSymbol(
            tag = path.joinToString("."),
            name = name(),
            path = path(),
        )

        private fun Name.name(): CtxName = name(name)

        private fun name(text: String): CtxName {
            return CtxName(
                snake = text.toSnakeCase(),
                camel = text.toCamelCase(),
                pascal = text.toPascalCase(),
                upper = text.uppercase(),
                lower = text.lowercase(),
            )
        }

        private fun Name.path(): CtxPath {
            return CtxPath(
                steps = path,
                snake = path.joinToString("_"),
                camel = path.joinToString("_").toCamelCase(CaseFormat.LOWER_UNDERSCORE),
                pascal = path.joinToString("_").toPascalCase(CaseFormat.LOWER_UNDERSCORE),
            )
        }
    }
}
