package io.github.amzn.anodizer.core

import com.amazon.ionelement.api.AnyElement
import com.amazon.ionelement.api.StructElement
import com.amazon.ionelement.api.emptyIonStruct
import com.amazon.ionelement.api.loadSingleElement

/**
 * Options "view" over an Ion struct; all methods return null if the option does not exist.
 */
public class Options private constructor(
    private val element: StructElement,
    private var next: Options? = null,
) {

    /**
     * Check if path exists.
     */
    public fun exists(path: String): Boolean = (search(path) != null)

    /**
     * Get option as String.
     */
    public fun getString(path: String): String? {
        return search(path)?.asStringOrNull()?.textValue
    }

    /**
     * Get option as Long.
     */
    public fun getLong(path: String): Long? {
        return search(path)?.asIntOrNull()?.longValue
    }

    /**
     * Get option as Boolean.
     */
    public fun getBoolean(path: String): Boolean? {
        return search(path)?.asBooleanOrNull()?.booleanValue
    }

    /**
     * Get option as List<T>.
     */
    public fun <T> getList(path: String, map: (AnyElement) -> T): List<T>? {
        return search(path)?.asListOrNull()?.values?.map(map)
    }

    /**
     * Get option as Options (for nested options).
     */
    public fun get(path: String): Options? {
        return search(path)?.asStructOrNull()?.let { Options(it) }
    }

    /**
     * Apply overrides to self, returning the new options view.
     */
    public fun override(options: Options): Options {
        if (options === this) {
            throw IllegalArgumentException("Cannot override options with itself")
        }
        // leetcode hackz #TC
        var curr = options
        while (curr.next != null) {
            curr = curr.next!!
        }
        curr.next = this
        return options
    }

    /**
     * Debug string.
     */
    override fun toString(): String {
        return element.toString()
    }

    /**
     * Search for value at the dot-delimited path, null if not exists.
     */
    private fun search(path: String): AnyElement? {
        val parts = path.split(".").toTypedArray()
        return search(parts)
    }

    /**
     * Search for value at the path, null if not exists.
     */
    private fun search(path: Array<String>): AnyElement? {
        if (path.isEmpty()) {
            throw IllegalArgumentException("Empty path: $path")
        }
        var curr: StructElement = element
        val n = path.size - 1
        var i = 0
        while (i < n) {
            val p = path[i]
            curr = curr.getOptional(p)?.asStructOrNull() ?: return null
            i += 1
        }
        return curr.getOptional(path[n]) ?: next?.search(path)
    }

    /**
     * TODO BUILDING OPTIONS FROM A STRING IS NOT IDEAL, ADD A BUILDER SOME OTHER DAY.
     */
    public companion object {

        /**
         * Load an options class from an Ion string; must be a struct.
         *
         *
         * @param str
         * @return
         */
        @JvmStatic
        public fun load(str: String): Options {
            return Options(loadSingleElement(str).asStruct())
        }

        /**
         * Create an empty options class.
         *
         * @return
         */
        @JvmStatic
        public fun empty(): Options = Options(emptyIonStruct())
    }
}
