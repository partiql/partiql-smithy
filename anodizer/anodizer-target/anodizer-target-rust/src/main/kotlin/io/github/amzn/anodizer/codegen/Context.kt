package io.github.amzn.anodizer.codegen

import io.github.amzn.anodizer.core.Ion
import net.pearx.kasechange.CaseFormat
import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase
import net.pearx.kasechange.toSnakeCase

/**
 * A context tree is just like the model, but oriented for codegen.
 *
 * TODO converge context and model — no real need for them to be different.
 */
internal class Context private constructor() {

    /**
     * Node of definition tree; rooted at a domain.
     */
    sealed interface Node {
        fun children(): List<Node>
        fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R
    }

    data class Domain(
        @JvmField val name: Name,
        @JvmField val definitions: List<Definition>,
    ) : Node {
        override fun children() = definitions
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitDomain(this, ctx)
    }

    data class Symbol(
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
    data class Name(
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
    data class Path(
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
        fun snake(delimiter: String = ""): String = steps.joinToString(delimiter) { it.toSnakeCase(case) }

        /**
         * pascal("")   -> PathToMyName
         * pascal(".")  -> Path.To.MyName
         * pascal("_")  -> Path_To_My_Name
         * pascal("::") -> Path::To::My::Name
         */
        fun pascal(delimiter: String = ""): String = steps.joinToString(delimiter) { it.toPascalCase(case) }

        /**
         * camel("")    -> pathtomyName
         * camel(".")   -> path.to.myName
         * camel("_")   -> path_to_myName
         * camel("::")  -> path::to::myName
         */
        fun camel(delimiter: String = ""): String = steps.joinToString(delimiter) { it.toCamelCase(case) }

        /**
         * upper("")    -> PATHTOMY_NAME
         * upper(".")   -> PATH.TO.MY_NAME
         * upper("_")   -> PATH_TO_MY_NAME
         * upper("::")  -> PATH::TO::MY_NAME
         */
        fun upper(delimiter: String = ""): String = steps.joinToString(delimiter) { it.uppercase() }

        /**
         * lower("")    -> pathtomy_name
         * lower(".")   -> path.to.my_name
         * lower("_")   -> path_to_my_name
         * lower("::")  -> path::to::my_name
         */
        fun lower(delimiter: String = ""): String = steps.joinToString(delimiter) { it.lowercase() }
    }

    sealed interface Definition : Node {
        fun symbol(): Symbol
    }

    data class Namespace(
        @JvmField val symbol: Symbol,
        @JvmField val definitions: List<Definition>,
    ) : Definition {
        override fun symbol() = symbol
        override fun children() = definitions
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitNamespace(this, ctx)
    }

    sealed interface Typedef : Definition {
        fun definition(): io.github.amzn.anodizer.core.Definition.Typedef
    }

    data class Alias(
        @JvmField val symbol: Symbol,
        @JvmField val type: Type,
        @JvmField val parent: Symbol?,
        @JvmField val definition: io.github.amzn.anodizer.core.Definition.Alias,
    ) : Typedef {
        override fun definition() = definition
        override fun symbol() = symbol
        override fun children() = emptyList<Definition>()
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitAlias(this, ctx)
    }

    data class Enum(
        @JvmField val symbol: Symbol,
        @JvmField val values: List<Name>,
        @JvmField val parent: Symbol?,
        @JvmField val definition: io.github.amzn.anodizer.core.Definition.Enum,
    ) : Typedef {
        override fun definition() = definition
        override fun symbol() = symbol
        override fun children() = emptyList<Definition>()
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitEnum(this, ctx)
    }

    data class Struct(
        @JvmField val symbol: Symbol,
        @JvmField val fields: List<Field>,
        @JvmField val parent: Symbol?,
        @JvmField val definition: io.github.amzn.anodizer.core.Definition.Struct,
    ) : Typedef {
        override fun definition() = definition
        override fun symbol() = symbol
        override fun children() = emptyList<Definition>()
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitStruct(this, ctx)
    }

    data class Union(
        @JvmField val symbol: Symbol,
        @JvmField val variants: List<Typedef>,
        @JvmField val parent: Symbol?,
        @JvmField val definition: io.github.amzn.anodizer.core.Definition.Union,
    ) : Typedef {
        override fun definition() = definition
        override fun symbol() = symbol
        override fun children() = variants
        override fun <C, R> accept(visitor: Visitor<C, R>, ctx: C): R = visitor.visitUnion(this, ctx)
    }

    data class Field(
        @JvmField val name: Name,
        @JvmField val type: Type,
    )

    sealed interface Type {
        fun type(): io.github.amzn.anodizer.core.Type
    }

    data class Named(
        @JvmField val symbol: Symbol,
        @JvmField val type: io.github.amzn.anodizer.core.Type.Named,
    ) : Type {
        override fun type(): io.github.amzn.anodizer.core.Type = type
    }

    data class Array(
        @JvmField val item: Type,
        @JvmField val size: Int?,
        @JvmField val type: io.github.amzn.anodizer.core.Type.Array,
    ) : Type {
        override fun type(): io.github.amzn.anodizer.core.Type = type
    }

    data class Primitive(
        @JvmField val ion: Ion,
        @JvmField val type: io.github.amzn.anodizer.core.Type.Primitive,
    ) : Type {
        fun args(): List<Any?> = type.args()
        override fun type(): io.github.amzn.anodizer.core.Type = type
    }

    object Unit : Type {
        override fun type(): io.github.amzn.anodizer.core.Type = io.github.amzn.anodizer.core.Type.Unit
    }

    /**
     * For the definitions tree.
     */
    abstract class Visitor<C, R> {

        open fun defaultVisit(node: Node, ctx: C): R {
            for (child in node.children()) {
                visit(child, ctx)
            }
            return defaultReturn(node, ctx)
        }

        abstract fun defaultReturn(node: Node, ctx: C): R

        open fun visit(node: Node, ctx: C): R = node.accept(this, ctx)

        open fun visitDomain(domain: Domain, ctx: C): R = defaultVisit(domain, ctx)

        open fun visitDefinition(definition: Definition, ctx: C): R = when (definition) {
            is Namespace -> visitNamespace(definition, ctx)
            is Typedef -> visitTypedef(definition, ctx)
        }

        open fun visitNamespace(namespace: Namespace, ctx: C): R = defaultVisit(namespace, ctx)

        open fun visitTypedef(typedef: Typedef, ctx: C): R = when (typedef) {
            is Alias -> visitAlias(typedef, ctx)
            is Enum -> visitEnum(typedef, ctx)
            is Struct -> visitStruct(typedef, ctx)
            is Union -> visitUnion(typedef, ctx)
        }

        open fun visitAlias(alias: Alias, ctx: C): R = defaultVisit(alias, ctx)

        open fun visitEnum(enum: Enum, ctx: C): R = defaultVisit(enum, ctx)

        open fun visitStruct(struct: Struct, ctx: C): R = defaultVisit(struct, ctx)

        open fun visitUnion(union: Union, ctx: C): R = defaultVisit(union, ctx)
    }
}
