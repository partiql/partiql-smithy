package org.partiql.tool.ridl.codegen.language.kotlin

import com.amazon.ion.IonType

internal class KModel(
    @JvmField val `package`: String,
    @JvmField val namespace: KNamespace,
)

internal class KNamespace(
    @JvmField val name: String,
    @JvmField val definitions: List<KDefinition>,
)

internal class KDefinition(
    @JvmField val type: KType? = null,
    @JvmField val namespace: KNamespace? = null,
)

internal class KType(
    @JvmField val array: KArray? = null,
    @JvmField val enum: KEnum? = null,
    @JvmField val struct: KStruct? = null,
    @JvmField val union: KUnion? = null,
    @JvmField val unit: KUnit? = null,
)

internal class KArray(
    @JvmField val path: String,
    @JvmField val name: String,
    @JvmField val item: String,
    @JvmField val itemIon: IonType,
    @JvmField val size: Int?,
    @JvmField val write: String?,
    @JvmField val read: String?,
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
    @JvmField val wrap: Boolean = true,
)

internal class KField(
    @JvmField val name: String,
    @JvmField val type: String,
    @JvmField val ion: IonType,
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
