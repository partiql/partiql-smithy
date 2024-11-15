package io.github.amzn.anodizer.core

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream
import java.util.jar.JarFile

/**
 * Rudimentary filesystem backed by ByteArrayInputStream and ByteArrayOutputStream.
 *
 * Q: Why?
 * A: A generator produces a file tree which callers consume.
 *
 * Q: More why?
 * A: We can re-create a file hierarchy from the resources in a JAR.
 *
 * Q: What is wrong with this?
 * A: For starters, there are no assertions on duplicate entities.
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

        /**
         * Create a file.
         *
         * @param name
         * @return
         */
        @JvmStatic
        public fun file(name: String): File = File(name, false)

        /**
         * Create a directory.
         *
         * @param name
         * @return
         */
        @JvmStatic
        public fun dir(name: String): File = File(name, true)

        /**
         * Create a file (or tree) from a resource; the path is always taken from the root.
         *
         * @param clazz
         * @param path
         */
        @JvmStatic
        public fun resource(clazz: Class<*>, path: String): File {

            // load resource
            val url = clazz.getResource(path) ?: throw IllegalArgumentException("Resource not found: $path")

            // convert a java.io.File tree
            if (url.protocol == "file") {
                return java.io.File(url.toURI()).toFile()
            }

            // create a tree from the jar (making this recursive would be nice but not necessary now and tricky)
            if (url.protocol == "jar") {

                // extract <file> from url.path with from "file:" <file> "!/<path>"
                val file = url.path.substring(5, url.path.indexOf("!"))
                val jar = JarFile(file)

                // a trie would be nice, but perf not important.
                val relative = if (path.startsWith("/")) path.drop(1) else path
                val entries = jar.entries().asSequence()
                    .filter { it.name.startsWith(relative) }
                    .map { it.name.substring(relative.length) to it}
                    .toList()

                // top-level only unzip
                val dir = dir(relative)
                for ((p, e) in entries) {
                    if (p.endsWith("/")) {
                        continue // skip directories
                    }
                    val n = p.split("/").last()
                    val f = File(n, false)
                    val b = jar.getInputStream(e).readBytes()
                    f.buffer!!.write(b)
                    dir.add(f)
                }
                return dir
            }

            throw IllegalArgumentException("Unrecognized protocol, expected `file` or `jar`, found ${url.protocol}")
        }

        /**
         * Convert java.io.File to this abstraction.
         */
        private fun java.io.File.toFile(): File = if (isFile) {
            val bytes = readBytes()
            val file = File(name, false)
            file.buffer!!.write(bytes) // write directly to the buffer.
            file
        } else {
            val dir = File(name, true)
            val files = listFiles()
            if (files != null) {
                for (file in files) {
                    dir.add(file.toFile())
                }
            }
            dir
        }
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
     * Return true if file has children.
     */
    public fun isNotEmpty(): Boolean = children.isNotEmpty()

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
     * Include a resource from the classpath.
     *
     * @param clazz
     * @param resource
     */
    public fun include(clazz: Class<*>, resource: String) {
        if (!isDir) {
            throw IllegalStateException("Cannot add a resource to a file")
        }
        children.add(File.resource(clazz, resource))
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
