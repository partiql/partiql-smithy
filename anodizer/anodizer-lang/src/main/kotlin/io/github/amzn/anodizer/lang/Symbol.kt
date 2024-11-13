package io.github.amzn.anodizer.lang

import io.github.amzn.anodizer.core.Name
import kotlin.collections.find
import kotlin.collections.forEach
import kotlin.collections.reversed
import kotlin.collections.toTypedArray
import kotlin.text.appendLine

/**
 * Symbol Tree (namespaces and types)
 *
 * @property text       Symbol text.
 * @property kind       Symbol kind: TYPE or NAMESPACE
 * @property location   Source location for error messaging.
 * @property parent     Used to search up the tree.
 * @property children   Used to search down the tree.
 */
internal class Symbol(
    @JvmField val text: String,
    @JvmField val kind: Kind,
    @JvmField val location: Location,
    @JvmField val parent: Symbol?,
    @JvmField val children: LinkedHashSet<Symbol> = java.util.LinkedHashSet(),
) {

    internal enum class Kind {
        TYPE,
        NAMESPACE,
    }

    companion object {

        @JvmStatic
        fun root(namespace: String) = Symbol(namespace, Kind.NAMESPACE, Location(0, 0), null)
    }

    /**
     * Produce a name from this symbol.
     */
    val name: Name by lazy {
        val path = mutableListOf<String>()
        var curr: Symbol? = this
        while (curr != null) {
            path.add(curr.text)
            curr = curr.parent
        }
        Name(text, path.reversed().subList(1, path.size).toTypedArray())
    }

    override fun toString(): String = text

    fun toStringDebug(lead: String): String = buildString {
        append(lead)
        append(text)
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
            var curr = ns.children.find { it.text == path[0] }
            if (curr != null) {
                // matched the root, now match the rest or fail
                var i = 1
                while (i < path.size && curr != null) {
                    curr = curr.children.find { it.text == path[i] }
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
