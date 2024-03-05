package org.partiql.tool.ridl.codegen.templating

import java.io.Reader
import java.io.StringReader

internal class TemplateLoader(private val root: String) {

    private val cache: MutableMap<String, String> = mutableMapOf()

    fun load(name: String): Reader {
        val key = "$name.mustache"
        val source = cache.getOrPut(key) {
            val stream = this::class.java.getResourceAsStream("/$root/$key")!!
            stream.bufferedReader().readText()
        }
        return StringReader(source)
    }
}
