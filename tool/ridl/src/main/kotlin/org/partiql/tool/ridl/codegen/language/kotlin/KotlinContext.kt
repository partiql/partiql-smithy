package org.partiql.tool.ridl.codegen.language.kotlin

internal class KTypes(
    @JvmField val packageName: String,
    @JvmField val imports: List<String>,
    @JvmField val types: List<KType>,
)

internal class KType(
    @JvmField val alias: KAlias? = null,
    @JvmField val struct: KStruct? = null,
    @JvmField val union: KUnion? = null,
    @JvmField val unit: KUnit? = null,
    @JvmField val enum: KEnum? = null,
)

internal class KStruct(
    @JvmField val classname: String,
    @JvmField val simplename: String,
    @JvmField val fields: List<KField>,
    @JvmField val parent: String? = null,
)

internal class KField(
    @JvmField val name: String,
    @JvmField val type: String,
)

internal class KUnion(
    @JvmField val classname: String,
    @JvmField val simplename: String,
    @JvmField val variants: List<KVariant>,
    @JvmField val parent: String? = null,
)

internal class KVariant(
    @JvmField val simplename: String,
    @JvmField val struct: KStruct? = null,
    @JvmField val union: KUnion? = null,
    @JvmField val unit: KUnit? = null,
)

internal class KEnum(
    @JvmField val classname: String,
    @JvmField val simplename: String,
    @JvmField val enumerators: List<String>,
)

internal class KUnit(
    @JvmField val classname: String,
    @JvmField val simplename: String,
    @JvmField val parent: String? = null,
)

internal class KAlias(
    @JvmField val simplename: String,
    @JvmField val type: String,
)
