package io.github.amzn.anodizer.core

import org.junit.jupiter.api.Test

class FileTest {

    /**
     * Sanity check, no content assertions.
     */
    @Test
    fun loadResources() {
        val d = File.resource(FileTest::class.java, "/test")
        assert(d.isDir())
        assert(d.isNotEmpty())
    }

    /**
     * Testing loading a file from resources.
     */
    @Test
    fun loadResourceFile() {
        val f1 = File.resource(FileTest::class.java, "/test/file1.txt")
        assert(f1.isFile())
        assert(f1.toByteArray().toString(Charsets.UTF_8) == "Hello, world!")

        val f2 = File.resource(FileTest::class.java, "/test/file2.txt")
        assert(f2.isFile())
        assert(f2.toByteArray().toString(Charsets.UTF_8) == "Goodbye, moon.")
    }

    /**
     * Testing loading a directory from resources.
     */
    @Test
    fun loadResourceDir() {
        val dir = File.resource(FileTest::class.java, "/test/dir1")
        assert(dir.isDir())
        val files = dir.getChildren()
        assert(files.size == 2)
        for (f in files) {
            assert(f.isFile())
        }
    }
}
