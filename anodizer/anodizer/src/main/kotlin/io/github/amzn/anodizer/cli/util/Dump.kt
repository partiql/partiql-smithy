package io.github.amzn.anodizer.cli.util

import io.github.amzn.anodizer.core.File
import java.util.Stack

/**
 * Write files to stdout
 */
internal fun dump(file: File) {
    val stack = Stack<File>().apply { push(file) }
    while (stack.isNotEmpty()) {
        val f = stack.pop()
        if (f.isFile()) {
            println()
            println("=== ${f.getName()} ===")
            println()
            System.out.write(f.toByteArray())
        }
        f.getChildren().forEach { stack.push(it) }
    }
}
