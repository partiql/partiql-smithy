package io.github.amzn.anodizer.codegen

import io.github.amzn.anodizer.codegen.context.CtxAlias
import io.github.amzn.anodizer.codegen.context.CtxArray
import io.github.amzn.anodizer.codegen.context.CtxDefinition
import io.github.amzn.anodizer.codegen.context.CtxEnum
import io.github.amzn.anodizer.codegen.context.CtxModel
import io.github.amzn.anodizer.codegen.context.CtxNamed
import io.github.amzn.anodizer.codegen.context.CtxNamespace
import io.github.amzn.anodizer.codegen.context.CtxPrimitive
import io.github.amzn.anodizer.codegen.context.CtxStruct
import io.github.amzn.anodizer.codegen.context.CtxSymbol
import io.github.amzn.anodizer.codegen.context.CtxType
import io.github.amzn.anodizer.codegen.context.CtxTypedef
import io.github.amzn.anodizer.codegen.context.CtxUnion
import io.github.amzn.anodizer.codegen.context.CtxUnit

/**
 * TODO this needs a better factoring and name (Symbols?) but I don't want to deal with it right now.
 *
 * Shared logic for template based generators.
 *
 * @property model
 * @property templates
 */
public abstract class Generator(
    @JvmField public val model: CtxModel,
    @JvmField public val templates: Templates,
) {

    /**
     * For definition lookups.
     */
    private val index: Index = Index.build(model)

    /**
     * Return the type definition for this symbol.
     */
    public fun definitionOf(symbol: CtxSymbol): CtxTypedef = index.get(symbol.tag)

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

    /**
     * Return a method name for the symbol with a prefix (if any) and suffix (if any).
     */
    public abstract fun method(symbol: CtxSymbol, prefix: String? = null, suffix: String? = null): String

    /**
     * Return a reference (or null) given a symbol.
     */
    public open fun pathToOrNull(symbol: CtxSymbol?): String? {
        return if (symbol != null) pathTo(symbol) else null
    }

    /**
     * Return a reference given a symbol.
     */
    public abstract fun pathTo(symbol: CtxSymbol): String

    /**
     * Return a type reference given a type definition.
     */
    public open fun typeOf(typedef: CtxTypedef): String {
        val symbol = typedef.symbol()
        return pathTo(symbol)
    }

    /**
     * Return a type reference given a type argument.
     */
    public open fun typeOf(type: CtxType): String = when (type) {
        is CtxArray -> typeOfArray(type)
        is CtxNamed -> typeOfNamed(type)
        is CtxPrimitive -> typeOfPrimitive(type)
        is CtxUnit -> typeOf(type)
    }

    public abstract fun typeOfArray(array: CtxArray): String

    public open fun typeOfNamed(named: CtxNamed): String = pathTo(named.symbol)

    public abstract fun typeOfPrimitive(primitive: CtxPrimitive): String

    public abstract fun typeOfUnit(unit: CtxUnit): String

    /**
     * TODO unnest this and probably make it a facet.
     *
     * Base generator that has (likely) all the methods you need, otherwise use the Generator directly.
     */
    public abstract class Base(model: CtxModel, templates: Templates) : Generator(model, templates) {

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
            val definition = when (val base = definitionOf(named.symbol)) {
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
}
