package org.partiql.tool.ridl.codegen.language.kotlin

internal class KModel(
    @JvmField val `package`: String,
    @JvmField val namespace: KNamespace,
)

internal class KNamespace(
    @JvmField val name: String,
    @JvmField val types: List<KType>,
)

internal class KType(
    @JvmField val alias: KAlias? = null,
    @JvmField val array: KArray? = null,
    @JvmField val struct: KStruct? = null,
    @JvmField val union: KUnion? = null,
    @JvmField val unit: KUnit? = null,
    @JvmField val enum: KEnum? = null,
)

internal class KArray(
    @JvmField val path: String,
    @JvmField val name: String,
    @JvmField val item: String,
    @JvmField val size: Int?,
    @JvmField val write: String?,
)

internal class KEnum(
    @JvmField val path: String,
    @JvmField val name: String,
    @JvmField val values: List<String>,
)

internal class KStruct(
    @JvmField val path: String,
    @JvmField val name: String,
    @JvmField val tag: String,
    @JvmField val fields: List<KField>,
    @JvmField val parent: String? = null,
    @JvmField val builder: String? = null,
    @JvmField val visit: String? = null,
)

internal class KField(
    @JvmField val name: String,
    @JvmField val type: String,
    @JvmField val write: String?,
    @JvmField val read: String?,
)

internal class KUnion(
    @JvmField val path: String,
    @JvmField val name: String,
    @JvmField val variants: List<KType>,
    @JvmField val parent: String? = null,
    @JvmField val builder: String? = null,
    @JvmField val visit: String? = null,
)

internal class KUnit(
    @JvmField val path: String,
    @JvmField val name: String,
    @JvmField val tag: String,
    @JvmField val parent: String? = null,
    @JvmField val builder: String? = null,
    @JvmField val visit: String? = null,
)

internal class KAlias(
    @JvmField val name: String,
    @JvmField val type: String,
)
