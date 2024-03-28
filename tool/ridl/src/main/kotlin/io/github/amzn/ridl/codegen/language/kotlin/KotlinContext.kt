package io.github.amzn.ridl.codegen.language.kotlin

import com.amazon.ion.IonType

internal class KModel(
    @JvmField var `package`: String,
    @JvmField var namespace: KNamespace,
)

internal class KNamespace(
    @JvmField var name: String,
    @JvmField var definitions: List<KDefinition>,
)

internal class KDefinition(
    @JvmField var type: KType? = null,
    @JvmField var namespace: KNamespace? = null,
)

internal class KType(
    @JvmField var array: KArray? = null,
    @JvmField var enum: KEnum? = null,
    @JvmField var struct: KStruct? = null,
    @JvmField var union: KUnion? = null,
    @JvmField var unit: KUnit? = null,
    @JvmField var box: KBox? = null,
)

internal class KArray(
    @JvmField var path: String,
    @JvmField var name: String,
    @JvmField var parent: String,
    @JvmField var item: String,
    @JvmField var itemIon: IonType,
    @JvmField var size: Int?,
    @JvmField var write: String?,
    @JvmField var read: String?,
    @JvmField var tag: Int? = null,
)

internal class KEnum(
    @JvmField var path: String,
    @JvmField var name: String,
    @JvmField var parent: String,
    @JvmField var values: List<String>,
    @JvmField var tag: Int? = null,
)

internal class KStruct(
    @JvmField var path: String,
    @JvmField var name: String,
    @JvmField var parent: String,
    @JvmField var fields: List<KField>,
    @JvmField var tag: Int? = null,
)

internal class KBox(
    @JvmField var path: String,
    @JvmField var name: String,
    @JvmField var parent: String,
    @JvmField var type: String,
    @JvmField var ion: IonType,
    @JvmField var write: String?,
    @JvmField var read: String?,
    @JvmField var tag: Int? = null,
)

internal class KField(
    @JvmField var name: String,
    @JvmField var type: String,
    @JvmField var ion: IonType,
    @JvmField var write: String?,
    @JvmField var read: String?,
    @JvmField var tag: Int? = null,
)

internal class KUnion(
    @JvmField var path: String,
    @JvmField var name: String,
    @JvmField var parent: String,
    @JvmField var variants: List<KVariant>,
    @JvmField var tag: Int? = null,
)

internal class KVariant(
    @JvmField var tag: Int,
    @JvmField var name: String,
    @JvmField var type: KType,
)

internal class KUnit(
    @JvmField var path: String,
    @JvmField var name: String,
    @JvmField var parent: String,
    @JvmField var tag: String,
)
