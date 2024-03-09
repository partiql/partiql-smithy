package org.partiql.tool.ridl.codegen.templating

import com.samskivert.mustache.Mustache

internal class TemplateManager(private val root: String) {

    private val templates = TemplateLoader(root)
    private val compiler: Mustache.Compiler = Mustache.compiler()

    fun apply(key: String, context: Any): String {
        val source = templates.load(key)
        val template = compiler.compile(source)
        return template.execute(context)
    }
}
