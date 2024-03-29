package io.github.amzn.ridl.codegen.language.kotlin

/**
 * Builder class for a kotlin package.
 */
internal class KotlinPackage(@JvmField val path: List<String>) {

    private val files: MutableList<KotlinFile> = mutableListOf()
    private val packages: MutableList<KotlinPackage> = mutableListOf()

    fun mkfile(name: String, template: String, ctx: Any): KotlinFile {
        val child = KotlinFile(name, template, ctx)
        files.add(child)
        return child
    }

    fun mkdir(name: String): KotlinPackage {
        val child = KotlinPackage(path + name)
        packages.add(child)
        return child
    }

    fun write(templates: io.github.amzn.ridl.codegen.Templates) {
        files.forEach { it.write(templates) }
        packages.forEach { it.write(templates) }
    }
}
