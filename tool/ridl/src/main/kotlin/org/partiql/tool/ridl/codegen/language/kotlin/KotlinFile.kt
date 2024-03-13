package org.partiql.tool.ridl.codegen.language.kotlin

import java.io.File

internal class KotlinFile(
    private val name: String,
    private val template: String,
    private val ctx: Any? = null,
) {

    private fun write(): File {
        TODO()
    }
}
