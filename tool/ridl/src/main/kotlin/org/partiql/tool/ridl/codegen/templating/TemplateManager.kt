package org.partiql.tool.ridl.codegen.templating

import com.samskivert.mustache.Mustache

internal class TemplateManager(private val root: String) {

    private val templates = TemplateLoader(root)
    private val compiler: Mustache.Compiler = Mustache.compiler()

    fun compile(name: String, context: Any) {
        val source = templates.load(name)
        val template = compiler.compile(source)
        val result = template.execute(context)
        println(result)
    }
}
