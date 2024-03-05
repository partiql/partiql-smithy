package org.partiql.tool.ridl.codegen.language.kotlin

import org.partiql.tool.ridl.codegen.Generator
import org.partiql.tool.ridl.codegen.templating.TemplateManager
import org.partiql.tool.ridl.model.Document
import java.io.File

internal class KotlinGenerator(
    private val options: KotlinOptions,
) : Generator {

    private val templates = TemplateManager("kotlin")

    override fun generate(document: Document): List<File> {
        document.definitions.forEach {
            val model = KotlinModel(
                packageRoot = options.packageRoot,
                classname = it.name,
            )
            templates.compile("type_struct", model)
        }
        return emptyList()
    }
}
