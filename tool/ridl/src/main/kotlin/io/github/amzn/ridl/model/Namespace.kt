package io.github.amzn.ridl.model

public data class Namespace(
    @JvmField val name: Name,
    @JvmField val definitions: List<Definition>,
) : Definition
