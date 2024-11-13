package io.github.amzn.anodizer.core

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream

/**
 * Rudimentary filesystem backed by ByteArrayInputStream and ByteArrayOutputStream.
 *
 * Q: Why?
 * A: A generator produces a file tree which callers consume.
 *
 * TODO tree pretty-print helper.
 */
public class File private constructor(
    private val name: String,
    private val isDir: Boolean,
) {

    private val buffer: ByteArrayOutputStream?
    private val writer: PrintStream?
    private val children: MutableList<File>

    init {
        if (isDir) {
            buffer = null
            writer = null
        } else {
            buffer = ByteArrayOutputStream()
            writer = PrintStream(buffer)
        }
        children = mutableListOf()
    }

    public companion object {

        @JvmStatic
        public fun file(name: String): File = File(name, false)

        @JvmStatic
        public fun dir(name: String): File = File(name, true)
    }

    /**
     * File name.
     */
    public fun getName(): String = name

    /**
     * If true, then can have children.
     */
    public fun isDir(): Boolean = isDir

    /**
     * If true, then can be written to.
     */
    public fun isFile(): Boolean = !isDir

    /**
     * Return all file children.
     */
    public fun getChildren(): List<File> = children

    /**
     * Add a file to this directory, returning it.
     */
    public fun touch(name: String): File {
        if (!isDir) {
            throw IllegalStateException("Cannot call touch() on a file")
        }
        val file = touch(name)
        children.add(file)
        return file
    }

    /**
     * Add a directory to this directory, returning it.
     */
    public fun mkdir(name: String): File {
        if (!isDir) {
            throw IllegalStateException("Cannot call mkdir() on a file")
        }
        val dir = dir(name)
        children.add(dir)
        return dir
    }

    /**
     * Add a directory to this directory, creating intermediate directories as required (mkdir -p)
     */
    public fun mkdirp(names: List<String>): File {
        var dir = this
        for (name in names) {
            dir = dir.mkdir(name)
        }
        return dir
    }

    /**
     * Write the string; from PrintStream API.
     */
    public fun write(str: String) {
        if (isDir) {
            throw IllegalStateException("Cannot write to a directory")
        }
        writer!!.print(str)
    }

    /**
     * Write the string with a newline; from PrintStream API.
     */
    public fun writeln(str: String) {
        if (isDir) {
            throw IllegalStateException("Cannot write to a directory")
        }
        writer!!.println(str)
    }

    /**
     * Write a blank newline; from PrintStream API.
     */
    public fun writeln() {
        if (isDir) {
            throw IllegalStateException("Cannot write to a directory")
        }
        writer!!.println()
    }

    /**
     * Add files to this directory.
     *
     * @param files
     */
    public fun add(vararg files: File) {
        if (!isDir) {
            throw IllegalStateException("Cannot add a file to a file")
        }
        children.addAll(files)
    }

    /**
     * Return the file buffer as a byte[].
     */
    public fun toByteArray(): ByteArray {
        if (isDir) {
            throw IllegalStateException("Cannot call `toByteArray()` on a directory")
        }
        return buffer!!.toByteArray()
    }

    /**
     * Rather than consuming the input, produce a copy of it.
     */
    public fun toInputStream(): InputStream {
        if (buffer == null) {
            throw IllegalStateException("Cannot call `toInputStream()` on a directory")
        }
        return ByteArrayInputStream(toByteArray())
    }
}
