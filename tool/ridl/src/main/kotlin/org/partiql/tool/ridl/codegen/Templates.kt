package org.partiql.tool.ridl.codegen

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Mustache.TemplateLoader
import com.samskivert.mustache.Template
import java.io.Reader

internal class Templates(private val root: String) {

    private val loader: Loader = Loader()
    private val compiler: Mustache.Compiler = Mustache.compiler().withLoader(loader)
    private val cache: MutableMap<String, Template> = mutableMapOf()

    fun apply(key: String, context: Any): String {
        val template = cache.getOrPut(key) {
            val reader = loader.getTemplate(key)
            compiler.compile(reader)
        }
        return template.execute(context)
    }

    private inner class Loader : TemplateLoader {

        override fun getTemplate(name: String): Reader {
            val filename = "$name.mustache"
            val stream = this::class.java.getResourceAsStream("/$root/$filename")!!
            return stream.bufferedReader()
        }
    }
}
