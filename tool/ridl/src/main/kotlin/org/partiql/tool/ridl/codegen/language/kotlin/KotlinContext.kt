package org.partiql.tool.ridl.codegen.language.kotlin

internal class KStruct(
    @JvmField val classname: String,
    @JvmField val simplename: String,
    @JvmField val fields: List<KField>,
)

internal class KField(
    @JvmField val name: String,
    @JvmField val type: String,
)

internal class KEnum(
    @JvmField val classname: String,
    @JvmField val simplename: String,
    @JvmField val enumerators: List<KEnumerator>,
)

internal class KEnumerator(@JvmField val value: String)
