package org.partiql.tool.ridl.codegen.language.kotlin

internal class KotlinName(private val classname: Array<String>) {

    override fun equals(other: Any?): Boolean = (other is KotlinName && classname.contentEquals(other.classname))

    override fun hashCode(): Int = classname.hashCode()
}
