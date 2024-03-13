package org.partiql.tool.ridl.codegen.language.kotlin

import java.io.File

/**
 * Builder class for a kotlin package.
 */
internal class KotlinPackage(@JvmField val path: Array<String>) {

    private val files: MutableList<KotlinFile> = mutableListOf()
    private val packages: MutableList<KotlinPackage> = mutableListOf()

    fun mkfile(name: String, template: String, ctx: Any? = null): KotlinFile {
        val child = KotlinFile(name, template, ctx)
        files.add(child)
        return child
    }

    fun mkdir(name: String): KotlinPackage {
        val child = KotlinPackage(path + name)
        packages.add(child)
        return child
    }

    fun write(): List<File> {
        TODO()
    }
}
