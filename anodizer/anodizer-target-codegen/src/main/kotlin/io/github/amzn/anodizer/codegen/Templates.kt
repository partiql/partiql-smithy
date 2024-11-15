package io.github.amzn.anodizer.codegen

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Mustache.TemplateLoader
import com.samskivert.mustache.MustacheException
import com.samskivert.mustache.Template
import java.io.Reader

/**
 * Caching template provider over the resources/templates directory.
 */
public class Templates(private val root: String? = null) {

    private val loader: Loader = Loader()
    private val compiler: Mustache.Compiler = Mustache.compiler().withLoader(loader)
    private val cache: MutableMap<String, Template> = mutableMapOf()

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

    private inner class Loader : TemplateLoader {

        override fun getTemplate(name: String): Reader {
            val filename = resolve(name)
            val stream = this::class.java.getResourceAsStream(filename)!!
            return stream.bufferedReader()
        }

        private fun resolve(name: String): String {
            val filename = "$name.mustache"
            return if (root != null) "/templates/$root/$filename" else "/templates/$filename"
        }
    }
}
