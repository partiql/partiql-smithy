package org.partiql.tool.ridl.codegen.language.kotlin

import java.io.File

/**
 * Builder class for a kotlin package.
 */
internal class KotlinPackage(
    @JvmField val path: Array<String>,
    @JvmField val files: MutableList<KotlinFile>,
    @JvmField val packages: MutableList<KotlinPackage>,
) {



    fun write(): List<File> {
        TODO()
    }
}
