package io.github.amzn.anodizer.target.rust

import io.github.amzn.anodizer.codegen.Context
import io.github.amzn.anodizer.codegen.Index
import io.github.amzn.anodizer.core.Type

/**
 * A tag is added to the lifetime set if a dependency contains a reference; we propagate this up the dependency path.
 */
internal class Lifetimes(private val set: Set<String>) {

    fun contains(symbol: Context.Symbol): Boolean = set.contains(symbol.tag)

    companion object {

        @JvmStatic
        fun of(domain: Context.Domain, index: Index): Lifetimes {
            val set = mutableSetOf<String>()
            val visitor = Visitor(set, index)
            visitor.visitDomain(domain, Unit)
            return Lifetimes(set)
        }
    }

    private class Visitor(
        private val lifetimes: MutableSet<String>,
        private val index: Index,
    ) : Context.Visitor<Unit, Boolean>() {

        private val seen = mutableSetOf<String>()

        override fun defaultReturn(node: Context.Node, ctx: Unit): Boolean = false

        // avoid accept so we can wrap all calls to typedef
        override fun visit(node: Context.Node, ctx: Unit): Boolean = when (node) {
            is Context.Domain -> super.visitDomain(node, ctx)
            is Context.Namespace -> super.visitNamespace(node, ctx)
            is Context.Typedef -> visitTypedef(node, ctx)
        }

        /**
         * Wrap all calls for typedef.
         */
        override fun visitTypedef(typedef: Context.Typedef, ctx: Unit): Boolean {
            val tag = typedef.symbol().tag
            var needsLifetime = lifetimes.contains(tag)
            if (needsLifetime || seen.contains(tag)) {
                return needsLifetime
            }
            seen.add(tag)
            needsLifetime = super.visitTypedef(typedef, ctx)
            if (needsLifetime) {
                lifetimes.add(tag)
            }
            return needsLifetime
        }

        override fun visitAlias(alias: Context.Alias, ctx: Unit): Boolean {
            if (alias.type !is Context.Named) return false
            val definition = index.get(alias.type.symbol.tag)
            return visitTypedef(definition, ctx)
        }

        override fun visitEnum(enum: Context.Enum, ctx: Unit): Boolean = false

        override fun visitStruct(struct: Context.Struct, ctx: Unit): Boolean {
            for (field in struct.fields) {
                if (field.type.isRef()) {
                    return true
                }
            }
            return false
        }

        override fun visitUnion(union: Context.Union, ctx: Unit): Boolean {
            var needsLifetime = false
            for (variant in union.variants) {
                needsLifetime = visitTypedef(variant, ctx) || needsLifetime
            }
            return needsLifetime
        }

        fun Context.Type.isRef(): Boolean = when (this.type()) {
            is Type.Named,
            is Type.Primitive.Blob,
            is Type.Primitive.Clob,
            is Type.Primitive.Decimal,
            -> true
            else -> false
        }
    }
}
