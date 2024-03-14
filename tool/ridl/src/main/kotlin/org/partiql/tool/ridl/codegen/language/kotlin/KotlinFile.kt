package org.partiql.tool.ridl.codegen.language.kotlin

import org.partiql.tool.ridl.codegen.Templates

internal class KotlinFile(
    private val name: String,
    private val template: String,
    private val ctx: Any,
) {

    fun write(templates: Templates) {
        try {
            val output = templates.apply(template, ctx)
            println(output)
        } catch (ex: Exception) {
            val message = "Error in file $name for template $template"
            val cause = ex
            throw RuntimeException(message, cause)
        }
    }
}
