package io.github.amzn.anodizer.codegen

import io.github.amzn.anodizer.core.Ion
import net.pearx.kasechange.CaseFormat
import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase
import net.pearx.kasechange.toSnakeCase

/**
 * A context tree is just like the model, but oriented for codegen.
 *
 * TODO unnest node and visitor, make context=domain.
 */
public class Context private constructor() {

    /**
     * Node of definition tree; rooted at a domain.
     */
    public sealed interface Node {
        public fun children(): List<Node>
        public fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R
    }

    public data class Domain(
        @JvmField val name: Name,
        @JvmField val definitions: List<Definition>,
    ) : Node {
        override fun children(): List<Definition> = definitions
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitDomain(this, ctx)
    }

    public data class Symbol(
        @JvmField val tag: String,
        @JvmField val name: Name,
        @JvmField val path: Path,
    )

    /**
     * Name
     * -----------------------
     * snake -> "my_type"
     * upper -> "MY_TYPE"
     * lower -> "my_type"
     * camel -> "myType"
     * pascal -> "MyType"
     */
    public data class Name(
        @JvmField val snake: String,
        @JvmField val camel: String,
        @JvmField val pascal: String,
        @JvmField val upper: String,
        @JvmField val lower: String,
    )

    /**
     * Path
     * -----------------------
     * steps    -> ["path", "to", "my_name"]
     * snake    -> path_to_my_name
     * camel    -> pathToMyName
     * pascal   -> PathToMyName
     */
    public data class Path(
        @JvmField val steps: kotlin.Array<String>,
        @JvmField val snake: String,
        @JvmField val camel: String,
        @JvmField val pascal: String,
    ) {

        /**
         * RIDL.g4 // NAME: [a-z][a-z0-9_]*;
         */
        private val case = CaseFormat.LOWER_UNDERSCORE

        /**
         * snake("")    -> pathtomy_name
         * snake(".")   -> path.to.my_name
         * snake("_")   -> path_to_my_name
         * snake("::")  -> path::to::my_name
         */
        public fun snake(delimiter: String = ""): String = steps.joinToString(delimiter) { it.toSnakeCase(case) }

        /**
         * pascal("")   -> PathToMyName
         * pascal(".")  -> Path.To.MyName
         * pascal("_")  -> Path_To_My_Name
         * pascal("::") -> Path::To::My::Name
         */
        public fun pascal(delimiter: String = ""): String = steps.joinToString(delimiter) { it.toPascalCase(case) }

        /**
         * camel("")    -> pathtomyName
         * camel(".")   -> path.to.myName
         * camel("_")   -> path_to_myName
         * camel("::")  -> path::to::myName
         */
        public fun camel(delimiter: String = ""): String = steps.joinToString(delimiter) { it.toCamelCase(case) }

        /**
         * upper("")    -> PATHTOMY_NAME
         * upper(".")   -> PATH.TO.MY_NAME
         * upper("_")   -> PATH_TO_MY_NAME
         * upper("::")  -> PATH::TO::MY_NAME
         */
        public fun upper(delimiter: String = ""): String = steps.joinToString(delimiter) { it.uppercase() }

        /**
         * lower("")    -> pathtomy_name
         * lower(".")   -> path.to.my_name
         * lower("_")   -> path_to_my_name
         * lower("::")  -> path::to::my_name
         */
        public fun lower(delimiter: String = ""): String = steps.joinToString(delimiter) { it.lowercase() }
    }

    public sealed interface Definition : Node {
        public fun symbol(): Symbol
    }

    public data class Namespace(
        @JvmField val symbol: Symbol,
        @JvmField val definitions: List<Definition>,
    ) : Definition {
        override fun symbol(): Symbol = symbol
        override fun children(): List<Definition> = definitions
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitNamespace(this, ctx)
    }

    public sealed interface Typedef : Definition {
        public fun definition(): io.github.amzn.anodizer.core.Definition.Typedef
    }

    public data class Alias(
        @JvmField val symbol: Symbol,
        @JvmField val type: Type,
        @JvmField val parent: Symbol?,
        @JvmField val definition: io.github.amzn.anodizer.core.Definition.Alias,
    ) : Typedef {
        override fun definition(): io.github.amzn.anodizer.core.Definition.Alias = definition
        override fun symbol(): Symbol = symbol
        override fun children(): List<Definition> = emptyList<Definition>()
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitAlias(this, ctx)
    }

    public data class Enum(
        @JvmField val symbol: Symbol,
        @JvmField val values: List<Name>,
        @JvmField val parent: Symbol?,
        @JvmField val definition: io.github.amzn.anodizer.core.Definition.Enum,
    ) : Typedef {
        override fun definition(): io.github.amzn.anodizer.core.Definition.Enum = definition
        override fun symbol(): Symbol = symbol
        override fun children(): List<Definition> = emptyList()
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitEnum(this, ctx)
    }

    public data class Struct(
        @JvmField val symbol: Symbol,
        @JvmField val fields: List<Field>,
        @JvmField val parent: Symbol?,
        @JvmField val definition: io.github.amzn.anodizer.core.Definition.Struct,
    ) : Typedef {
        override fun definition(): io.github.amzn.anodizer.core.Definition.Struct = definition
        override fun symbol(): Symbol = symbol
        override fun children(): List<Definition> = emptyList()
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitStruct(this, ctx)
    }

    public data class Union(
        @JvmField val symbol: Symbol,
        @JvmField val variants: List<Typedef>,
        @JvmField val parent: Symbol?,
        @JvmField val definition: io.github.amzn.anodizer.core.Definition.Union,
    ) : Typedef {
        override fun definition(): io.github.amzn.anodizer.core.Definition.Union = definition
        override fun symbol(): Symbol = symbol
        override fun children(): List<Typedef> = variants
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitUnion(this, ctx)
    }

    public data class Field(
        @JvmField val name: Name,
        @JvmField val type: Type,
    )

    public sealed interface Type {
        public fun type(): io.github.amzn.anodizer.core.Type
    }

    public data class Named(
        @JvmField val symbol: Symbol,
        @JvmField val type: io.github.amzn.anodizer.core.Type.Named,
    ) : Type {
        override fun type(): io.github.amzn.anodizer.core.Type = type
    }

    public data class Array(
        @JvmField val item: Type,
        @JvmField val size: Int?,
        @JvmField val type: io.github.amzn.anodizer.core.Type.Array,
    ) : Type {
        override fun type(): io.github.amzn.anodizer.core.Type = type
    }

    public data class Primitive(
        @JvmField val ion: Ion,
        @JvmField val type: io.github.amzn.anodizer.core.Type.Primitive,
    ) : Type {
        public fun args(): List<Any?> = type.args()
        override fun type(): io.github.amzn.anodizer.core.Type = type
    }

    public data object Unit : Type {
        override fun type(): io.github.amzn.anodizer.core.Type = io.github.amzn.anodizer.core.Type.Unit
    }

    /**
     * For the definitions tree.
     */
    public abstract class Visitor<C, R> {

        public open fun defaultVisit(node: Node, ctx: C): R {
            for (child in node.children()) {
                visit(child, ctx)
            }
            return defaultReturn(node, ctx)
        }

        public abstract fun defaultReturn(node: Node, ctx: C): R

        public open fun visit(node: Node, ctx: C): R = node.accept(this, ctx)

        public open fun visitDomain(domain: Domain, ctx: C): R = defaultVisit(domain, ctx)

        public open fun visitDefinition(definition: Definition, ctx: C): R = when (definition) {
            is Namespace -> visitNamespace(definition, ctx)
            is Typedef -> visitTypedef(definition, ctx)
        }

        public open fun visitNamespace(namespace: Namespace, ctx: C): R = defaultVisit(namespace, ctx)

        public open fun visitTypedef(typedef: Typedef, ctx: C): R = when (typedef) {
            is Alias -> visitAlias(typedef, ctx)
            is Enum -> visitEnum(typedef, ctx)
            is Struct -> visitStruct(typedef, ctx)
            is Union -> visitUnion(typedef, ctx)
        }

        public open fun visitAlias(alias: Alias, ctx: C): R = defaultVisit(alias, ctx)

        public open fun visitEnum(enum: Enum, ctx: C): R = defaultVisit(enum, ctx)

        public open fun visitStruct(struct: Struct, ctx: C): R = defaultVisit(struct, ctx)

        public open fun visitUnion(union: Union, ctx: C): R = defaultVisit(union, ctx)
    }
}
