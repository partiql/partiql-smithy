package org.partiql.tool.ridl.model

public data class Namespace(
    @JvmField val name: Name,
    @JvmField val definitions: List<Definition>,
) : Definition
