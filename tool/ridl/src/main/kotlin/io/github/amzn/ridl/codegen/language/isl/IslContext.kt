package io.github.amzn.ridl.codegen.language.isl

internal class IslArray(
    @JvmField val name: String?,
    @JvmField val element: String,
    @JvmField val size: Int?,
)

internal class IslBlob(
    @JvmField val name: String?,
    @JvmField val size: Int?,
)

internal class IslAlias(
    @JvmField val name: String,
    @JvmField val type: String,
)

internal class IslSexp(
    @JvmField val name: String,
    @JvmField val size: Int,
    @JvmField val operands: List<String>,
)

internal class IslOneOf(
    @JvmField val name: String,
    @JvmField val variants: List<IslVariant>,
)

internal class IslVariant(
    @JvmField val name: String,
    @JvmField val type: String,
)

internal class IslEnum(
    @JvmField val name: String,
    @JvmField val values: List<String>,
)

internal class IslUnit(
    @JvmField val name: String,
)
