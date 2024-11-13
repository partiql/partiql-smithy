package io.github.amzn.anodizer.codegen

import io.github.amzn.anodizer.core.Definition
import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.core.Name
import io.github.amzn.anodizer.core.Type
import net.pearx.kasechange.CaseFormat
import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase
import net.pearx.kasechange.toSnakeCase

/**
 * Create a Context tree from a parsed RIDL document.
 */
internal object Contextualize {

    @JvmStatic
    fun contextualize(model: AnodizerModel): Context.Domain = model.toDocumentCtx()

    private fun AnodizerModel.toDocumentCtx(): Context.Domain {
        // Translate the tree.
        return Context.Domain(
            name = name(this.domain),
            definitions = definitions.toDefinitionsCtx(null),
        )
    }

    private fun List<Definition>.toDefinitionsCtx(parent: Name?): List<Context.Definition> {
        return map { it.toDefinitionCtx(parent) }
    }

    private fun List<Definition.Typedef>.toTypedefsCtx(parent: Name?): List<Context.Typedef> {
        return map { it.toTypedefCtx(parent) }
    }

    private fun Definition.toDefinitionCtx(parent: Name?): Context.Definition = when (this) {
        is Definition.Namespace -> toNamespaceCtx(parent)
        is Definition.Typedef -> toTypedefCtx(parent)
    }

    private fun Definition.Namespace.toNamespaceCtx(parent: Name?): Context.Namespace {
        val namespace = Context.Namespace(
            symbol = this.name.symbol(),
            definitions = this.definitions.toDefinitionsCtx(parent),
        )
        return namespace
    }

    private fun Definition.Typedef.toTypedefCtx(parent: Name?): Context.Typedef = when (this) {
        is Definition.Alias -> toAliasCtx(parent)
        is Definition.Enum -> toEnumCtx(parent)
        is Definition.Struct -> toStructCtx(parent)
        is Definition.Union -> toUnionCtx(parent)
    }

    private fun Definition.Alias.toAliasCtx(parent: Name?): Context.Typedef {
        val alias = Context.Alias(
            symbol = this.name.symbol(),
            type = this.type.toTypeCtx(),
            parent = parent?.symbol(),
            definition = this,
        )
        return alias
    }

    private fun Definition.Enum.toEnumCtx(parent: Name?): Context.Typedef {
        val enum = Context.Enum(
            symbol = this.name.symbol(),
            values = this.values.map { name(it) },
            parent = parent?.symbol(),
            definition = this,
        )
        return enum
    }

    private fun Definition.Struct.toStructCtx(parent: Name?): Context.Typedef {
        val struct = Context.Struct(
            symbol = this.name.symbol(),
            fields = this.fields.map {
                val n = name(it.name)
                val t = it.type.toTypeCtx()
                Context.Field(n, t)
            },
            parent = parent?.symbol(),
            definition = this,
        )
        return struct
    }

    private fun Definition.Union.toUnionCtx(parent: Name?): Context.Typedef {
        val union = Context.Union(
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

    private fun Type.Named.toTypeNamedCtx(): Context.Type {
        val named = Context.Named(
            symbol = this.name.symbol(),
            type = this,
        )
        return named
    }

    private fun Type.Array.toTypeArrayCtx(): Context.Type {
        val array = Context.Array(
            size = this.size,
            item = this.item.toTypeCtx(),
            type = this,
        )
        return array
    }

    private fun Type.Primitive.toTypePrimitiveCtx(): Context.Type {
        val primitive = Context.Primitive(
            ion = this.ion,
            type = this,
        )
        return primitive
    }

    private fun Type.Unit.toTypeUnitCtx(): Context.Type = Context.Unit

    private fun Name.symbol(): Context.Symbol = Context.Symbol(
        tag = path.joinToString("."),
        name = name(),
        path = path(),
    )

    private fun Name.name(): Context.Name = name(name)

    private fun name(text: String): Context.Name {
        return Context.Name(
            snake = text.toSnakeCase(),
            camel = text.toCamelCase(),
            pascal = text.toPascalCase(),
            upper = text.uppercase(),
            lower = text.lowercase(),
        )
    }

    private fun Name.path(): Context.Path {
        return Context.Path(
            steps = path,
            snake = path.joinToString("_"),
            camel = path.joinToString("_").toCamelCase(CaseFormat.LOWER_UNDERSCORE),
            pascal = path.joinToString("_").toPascalCase(CaseFormat.LOWER_UNDERSCORE),
        )
    }
}
