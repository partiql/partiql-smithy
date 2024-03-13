package org.partiql.tool.ridl.codegen.language.kotlin

import org.partiql.tool.ridl.model.Document
import picocli.CommandLine
import java.io.File
import java.nio.file.Path
import java.util.concurrent.Callable

@CommandLine.Command(
    name = "kotlin",
    mixinStandardHelpOptions = true,
    description = ["Generates Kotlin sources"]
)
internal class KotlinCommand : Callable<Int> {

    @CommandLine.Parameters(
        index = "0",
        description = ["Type definition file"]
    )
    lateinit var file: File

    @CommandLine.Option(
        names = ["-p", "--package"],
        description = ["Package root"]
    )
    lateinit var pkg: String

    @CommandLine.Option(
        names = ["-I"],
        description = ["Include directory"]
    )
    var include: Path? = null

    override fun call(): Int {
        // Configure
        val options = KotlinOptions(
            namespace = file.nameWithoutExtension,
            pkg = pkg.split("."),
        )
        // Parse
        val input = file.readText()
        val document = Document.load(input, include)
        // Generate
        val files = KotlinGenerator.generate(options, document)
        return 0
    }
}
