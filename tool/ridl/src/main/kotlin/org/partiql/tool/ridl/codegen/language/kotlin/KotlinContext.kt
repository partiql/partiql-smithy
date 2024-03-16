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
    @JvmField val array: KArray? = null,
    @JvmField val enum: KEnum? = null,
    @JvmField val scalar: KScalar? = null,
    @JvmField val struct: KStruct? = null,
    @JvmField val union: KUnion? = null,
    @JvmField val unit: KUnit? = null,
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
    @JvmField val parent: String,
    @JvmField val fields: List<KField>,
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
    @JvmField val parent: String,
    @JvmField val variants: List<KVariant>,
    @JvmField val builder: String? = null,
    @JvmField val visit: String? = null,
)

internal class KVariant(
    @JvmField val tag: Int,
    @JvmField val name: String,
    @JvmField val type: KType,
)

internal class KUnit(
    @JvmField val path: String,
    @JvmField val name: String,
    @JvmField val parent: String,
    @JvmField val tag: String,
)

internal class KScalar(
    @JvmField val path: String,
    @JvmField val name: String,
    @JvmField val parent: String,
    @JvmField val type: String,
    @JvmField val write: String,
    @JvmField val read: String,
)
