package org.partiql.tool.ridl.codegen.language.kotlin

import java.io.File

/**
 * TODO
 *
 * @property packageName
 * @property imports
 */
internal class KotlinFile(
    private val `package`: String,
    private val name: String,
    private val imports: MutableSet<KotlinName>,
) {

    private val content: MutableList<Pair<String, Any>> = mutableListOf()

    public fun append(ctx: Any) {

    }

    private fun write(): File {
        TODO()
    }
}
