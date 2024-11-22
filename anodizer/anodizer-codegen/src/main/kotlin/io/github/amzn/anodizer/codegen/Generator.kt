package io.github.amzn.anodizer.codegen

import io.github.amzn.anodizer.codegen.context.CtxAlias
import io.github.amzn.anodizer.codegen.context.CtxArray
import io.github.amzn.anodizer.codegen.context.CtxDefinition
import io.github.amzn.anodizer.codegen.context.CtxEnum
import io.github.amzn.anodizer.codegen.context.CtxNamed
import io.github.amzn.anodizer.codegen.context.CtxNamespace
import io.github.amzn.anodizer.codegen.context.CtxPrimitive
import io.github.amzn.anodizer.codegen.context.CtxStruct
import io.github.amzn.anodizer.codegen.context.CtxUnion
import io.github.amzn.anodizer.codegen.context.CtxUnit

/**
 * Shared logic for template based generators.
 *
 * @property symbols
 * @property templates
 */
public abstract class Generator(
    @JvmField public val symbols: Symbols,
    @JvmField public val templates: Templates,
) {

    /**
     * Return the rendered template with the given context.
     */
    public fun Buffer.appendTemplate(template: String, context: Any) {
        append(context.toString(template))
    }

    /**
     * Render the template.
     */
    public fun Any.toString(template: String): String {
        return templates.apply(template, this)
    }

    public open fun generateDefinition(definition: CtxDefinition, buffer: Buffer) {
        when (definition) {
            is CtxNamespace -> generateNamespace(definition, buffer)
            is CtxAlias -> generateAlias(definition, buffer)
            is CtxEnum -> generateEnum(definition, buffer)
            is CtxStruct -> generateStruct(definition, buffer)
            is CtxUnion -> generateUnion(definition, buffer)
        }
    }

    public open fun generateNamespace(namespace: CtxNamespace, buffer: Buffer) {
        for (definition in namespace.definitions) {
            generateDefinition(definition, buffer)
        }
    }

    public open fun generateAlias(alias: CtxAlias, buffer: Buffer) {
        when (alias.type) {
            is CtxPrimitive -> generateAliasPrimitive(alias, alias.type, buffer)
            is CtxArray -> generateAliasArray(alias, alias.type, buffer)
            is CtxUnit -> generateAliasUnit(alias, alias.type, buffer)
            is CtxNamed -> generateAliasNamed(alias, alias.type, buffer)
        }
    }

    public abstract fun generateAliasPrimitive(alias: CtxAlias, primitive: CtxPrimitive, buffer: Buffer)

    public abstract fun generateAliasArray(alias: CtxAlias, array: CtxArray, buffer: Buffer)

    public abstract fun generateAliasUnit(alias: CtxAlias, unit: CtxUnit, buffer: Buffer)

    public open fun generateAliasNamed(alias: CtxAlias, named: CtxNamed, buffer: Buffer) {
        val definition = when (val base = symbols.definitionOf(named.symbol)) {
            is CtxAlias -> alias.alias(base.type)
            is CtxEnum -> base.alias(alias)
            is CtxStruct -> base.alias(alias)
            is CtxUnion -> base.alias(alias)
        }
        generateDefinition(definition, buffer)
    }

    public abstract fun generateEnum(enum: CtxEnum, buffer: Buffer)

    public abstract fun generateStruct(struct: CtxStruct, buffer: Buffer)

    public abstract fun generateUnion(union: CtxUnion, buffer: Buffer)
}
