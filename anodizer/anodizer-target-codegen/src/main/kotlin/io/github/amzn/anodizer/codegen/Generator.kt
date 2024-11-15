package io.github.amzn.anodizer.codegen

/**
 * Shared logic for template based generators.
 *
 * @property context
 * @property templates
 */
public abstract class Generator(
    @JvmField public val context: Context.Domain,
    @JvmField public val templates: Templates,
) {

    /**
     * For definition lookups.
     */
    private val index: Index = Index.of(context)

    /**
     * Return the type definition for this symbol.
     */
    public fun definitionOf(symbol: Context.Symbol): Context.Typedef = index.get(symbol.tag)

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
    public abstract fun method(symbol: Context.Symbol, prefix: String? = null, suffix: String? = null): String

    /**
     * Return a reference (or null) given a symbol.
     */
    public open fun pathToOrNull(symbol: Context.Symbol?): String? {
        return if (symbol != null) pathTo(symbol) else null
    }

    /**
     * Return a reference given a symbol.
     */
    public abstract fun pathTo(symbol: Context.Symbol): String

    /**
     * Return a type reference given a type definition.
     */
    public open fun typeOf(typedef: Context.Typedef): String {
        val symbol = typedef.symbol()
        return pathTo(symbol)
    }

    /**
     * Return a type reference given a type argument.
     */
    public open fun typeOf(type: Context.Type): String = when (type) {
        is Context.Array -> typeOfArray(type)
        is Context.Named -> typeOfNamed(type)
        is Context.Primitive -> typeOfPrimitive(type)
        is Context.Unit -> typeOf(type)
    }

    public abstract fun typeOfArray(array: Context.Array): String

    public open fun typeOfNamed(named: Context.Named): String = pathTo(named.symbol)

    public abstract fun typeOfPrimitive(primitive: Context.Primitive): String

    public abstract fun typeOfUnit(unit: Context.Unit): String

    /**
     * Base generator that has (likely) all the methods you need, but you can extend just the Generator too.
     */
    public abstract class Base(context: Context.Domain, templates: Templates) : Generator(context, templates) {

        public open fun generateDefinition(definition: Context.Definition, buffer: Buffer) {
            when (definition) {
                is Context.Namespace -> generateNamespace(definition, buffer)
                is Context.Alias -> generateAlias(definition, buffer)
                is Context.Enum -> generateEnum(definition, buffer)
                is Context.Struct -> generateStruct(definition, buffer)
                is Context.Union -> generateUnion(definition, buffer)
            }
        }

        public open fun generateNamespace(namespace: Context.Namespace, buffer: Buffer) {
            for (definition in namespace.definitions) {
                generateDefinition(definition, buffer)
            }
        }

        public open fun generateAlias(alias: Context.Alias, buffer: Buffer) {
            when (alias.type) {
                is Context.Primitive -> generateAliasPrimitive(alias, alias.type, buffer)
                is Context.Array -> generateAliasArray(alias, alias.type, buffer)
                is Context.Unit -> generateAliasUnit(alias, alias.type, buffer)
                is Context.Named -> generateAliasNamed(alias, alias.type, buffer)
            }
        }

        public abstract fun generateAliasPrimitive(alias: Context.Alias, primitive: Context.Primitive, buffer: Buffer)

        public abstract fun generateAliasArray(alias: Context.Alias, array: Context.Array, buffer: Buffer)

        public abstract fun generateAliasUnit(alias: Context.Alias, unit: Context.Unit, buffer: Buffer)

        public open fun generateAliasNamed(alias: Context.Alias, named: Context.Named, buffer: Buffer) {
            val definition = when (val base = definitionOf(named.symbol)) {
                is Context.Alias -> alias.copy(type = base.type)
                is Context.Enum -> base.copy(
                    symbol = alias.symbol,
                    parent = alias.parent,
                )
                is Context.Struct -> base.copy(
                    symbol = alias.symbol,
                    parent = alias.parent,
                )
                is Context.Union -> base.copy(
                    symbol = alias.symbol,
                    parent = alias.parent,
                )
            }
            generateDefinition(definition, buffer)
        }

        public abstract fun generateEnum(enum: Context.Enum, buffer: Buffer)

        public abstract fun generateStruct(struct: Context.Struct, buffer: Buffer)

        public abstract fun generateUnion(union: Context.Union, buffer: Buffer)
    }
}
