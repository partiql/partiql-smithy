package org.partiql.tool.ridl.codegen.language.kotlin

import java.io.File

/**
 * Builder class for a kotlin package.
 */
internal class KotlinPackage(
    private val name: String,
    private val files: MutableList<KotlinFile>,
    private val packages: MutableList<KotlinPackage>,
) {

    fun write(): File {
        TODO()
    }
}
