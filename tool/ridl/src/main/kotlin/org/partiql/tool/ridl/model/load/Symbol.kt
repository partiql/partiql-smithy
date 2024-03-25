package org.partiql.tool.ridl.model.load

import org.partiql.tool.ridl.model.Name

/**
 * Names (namespaces and types)
 *
 * @property name
 * @property location   Source location for error messaging.
 * @property parent     Used to search up the tree.
 * @property children   Used to search down the tree.
 */
internal class Symbol(
    @JvmField val name: String,
    @JvmField val kind: Kind,
    @JvmField val location: Location,
    @JvmField val parent: Symbol?,
    @JvmField val children: MutableSet<Symbol> = mutableSetOf(),
) {

    internal enum class Kind {
        TYPE,
        NAMESPACE,
    }

    companion object {

        @JvmStatic
        fun root() = Symbol("", Kind.NAMESPACE, Location(0, 0), null)
    }

    fun toName(): Name {
        val path = mutableListOf<String>()
        var curr: Symbol? = this
        while (curr != null) {
            path.add(curr.name)
            curr = curr.parent
        }
        return Name(name, path.reversed().subList(1, path.size).toTypedArray())
    }

    override fun toString(): String = name

    fun toStringDebug(lead: String): String = buildString {
        append(lead)
        append(name)
        children.forEach {
            appendLine()
            append(it.toStringDebug("$lead "))
        }
    }

    /**
     * Find the symbol starting in the current namespace and work outwards.
     */
    fun find(path: Array<String>): Symbol? {
        var ns: Symbol? = this
        while (ns != null) {
            var curr = ns.children.find { it.name == path[0] }
            if (curr != null) {
                // matched the root, now match the rest or fail
                var i = 1
                while (i < path.size && curr != null) {
                    curr = curr.children.find { it.name == path[i] }
                    i++
                }
                return if (i == path.size) curr else null
            }
            // did not match root; move up one namespace.
            ns = ns.parent
        }
        return null
    }
}
