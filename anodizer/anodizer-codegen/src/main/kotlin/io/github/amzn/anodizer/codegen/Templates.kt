package io.github.amzn.anodizer.codegen

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Mustache.TemplateLoader
import com.samskivert.mustache.MustacheException
import com.samskivert.mustache.Template
import java.io.InputStream
import java.io.Reader

/**
 * Caching template provider over a `resources/templates` directory.
 */
public class Templates private constructor(
    private val clazz: Class<*>,
    private val root: String? = null,
) {

    private val loader = Loader(clazz)
    private val compiler: Mustache.Compiler = Mustache.compiler().withLoader(loader)
    private val cache: MutableMap<String, Template> = mutableMapOf()

    /**
     * Apply overrides to self, returning the new templates view.
     *
     * TODO determine how the template cache should behave if this were to be a linked-list.
     */
    public fun override(templates: Templates): Templates {
        // set the fallback of the loader classpath to this classpath
        templates.loader.next = this.loader.clazz
        return templates
    }

    public fun apply(key: String, context: Any? = null): String {
        val template = cache.getOrPut(key) {
            val reader = loader.getTemplate(key)
            compiler.escapeHTML(false).compile(reader)
        }
        return try {
            template.execute(context)
        } catch (ex: MustacheException) {
            throw RuntimeException("Exception while applying template `$key` on hash `$context`", ex)
        }
    }

    /**
     * Caching [TemplateLoader] implementation backed by the configured resources class loader.
     *
     * @property clazz
     */
    private inner class Loader(val clazz: Class<*>, var next: Class<*>? = null) : TemplateLoader {

        override fun getTemplate(name: String): Reader {
            val filename = resolve(name)
            val stream = getResource(filename) ?: error("Template `$filename` not found")
            return stream.bufferedReader()
        }

        private fun getResource(filename: String): InputStream? {
            return (clazz.getResourceAsStream(filename) ?: next?.getResourceAsStream(filename))
        }

        private fun resolve(name: String): String {
            val filename = "$name.mustache"
            return if (root != null) "/templates/$root/$filename" else "/templates/$filename"
        }
    }

    public companion object {

        /**
         * Create a [Template] provider from resources.
         *
         * @param clazz
         * @return
         */
        @JvmStatic
        @JvmOverloads
        public fun loader(clazz: Class<*>, root: String? = null): Templates = Templates(clazz, root)
    }
}
