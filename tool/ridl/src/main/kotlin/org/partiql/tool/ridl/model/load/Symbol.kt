package org.partiql.tool.ridl.model.load

import org.partiql.tool.ridl.model.Name

/**
 * Names (namespaces and types)
 *
 * @property name
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
     * Find the symbol in this namespace.
     */
    fun find(name: String): Symbol? = children.find { it.name == name }

    /**
     * Find the symbol starting in the current namespace and working outwards.
     */
    fun lookup(name: String): Symbol? {
        var namespace: Symbol? = this
        while (namespace != null) {
            val symbol = namespace.find(name)
            if (symbol != null) {
                return symbol
            }
            namespace = namespace.parent
        }
        return null
    }
}
