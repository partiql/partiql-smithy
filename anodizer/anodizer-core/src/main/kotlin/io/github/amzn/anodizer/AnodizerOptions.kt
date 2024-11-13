package io.github.amzn.anodizer

import com.amazon.ionelement.api.AnyElement
import com.amazon.ionelement.api.StructElement
import com.amazon.ionelement.api.emptyIonStruct
import com.amazon.ionelement.api.loadSingleElement

/**
 * Options helper backed by an IonElement.
 */
public class AnodizerOptions private constructor(private val element: StructElement) {

    public companion object {

        /**
         * Load an options class from an Ion string; must be a struct.
         *
         * @param str
         * @return
         */
        @JvmStatic
        public fun load(str: String): AnodizerOptions {
            return AnodizerOptions(loadSingleElement(str).asStruct())
        }

        /**
         * Create an empty options class.
         *
         * @return
         */
        @JvmStatic
        public fun empty(): AnodizerOptions = AnodizerOptions(emptyIonStruct())
    }

    public fun getString(path: String): String? {
        return get(path)?.asStringOrNull()?.textValue
    }

    public fun getLong(path: String): Long? {
        return get(path)?.asIntOrNull()?.longValue
    }

    public fun getBoolean(path: String): Boolean? {
        return get(path)?.asBooleanOrNull()?.booleanValue
    }

    public fun <T> getList(path: String, map: (AnyElement) -> T): List<T>? {
        return get(path)?.asListOrNull()?.values?.map(map)
    }

    /**
     * Check if path exists.
     */
    public fun exists(path: String): Boolean = (get(path) != null)

    /**
     * Get value at the dot-delimited path, null if not exists.
     */
    public fun get(path: String): AnyElement? {
        var curr: StructElement = element
        val parts = path.split(".").toTypedArray()
        if (parts.isEmpty()) {
            throw IllegalArgumentException("Empty path: $path")
        }
        val n = parts.size - 1
        var i = 0
        while (i < n) {
            val p = parts[i]
            curr = curr.getOptional(p)?.asStructOrNull() ?: return null
            i += 1
        }
        return curr.getOptional(parts[n])
    }

    override fun toString(): String {
        return element.toString()
    }
}
