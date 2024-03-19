package org.partiql.tool.ridl.model.load

import org.partiql.tool.ridl.model.*

/**
 * For lack of a better name, I'm calling this "rebase" because it replaces the base type for each RTypeRef.
 */
internal object Rebase {

    fun apply(definitions: List<Definition>, aliases: Map<Name, RTypeRef>) = with(rewriter(aliases)) {
        definitions.map { this.rewrite(it) }
    }

    private fun rewriter(aliases: Map<Name, RTypeRef>) = object : Rewriter() {

        override fun rewrite(type: RTypeNamed): RTypeNamed {
            var next = aliases[type.name]
            var base = next
            while (next != null) {
                base = next
                if (next !is RTypeNamed) break // hit a primitive type
                next = aliases[next.name]
            }
            return type.copy(base = base)
        }
    }
}