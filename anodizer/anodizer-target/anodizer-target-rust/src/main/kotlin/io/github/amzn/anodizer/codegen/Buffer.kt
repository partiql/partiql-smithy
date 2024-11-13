package io.github.amzn.anodizer.codegen

/**
 * Alias to StringBuilder is sufficient.
 */
internal typealias Buffer = StringBuilder

/**
 * Constructor.
 */
public fun buffer(): Buffer = StringBuilder()

/**
 * This is buildString {} ... logic-less templates means passing strings around.
 */
public inline fun buffer(block: StringBuilder.() -> Unit): String {
    return StringBuilder().apply(block).toString()
}
