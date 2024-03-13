package org.partiql.tool.ridl.codegen.language.kotlin

import org.partiql.tool.ridl.model.Document
import picocli.CommandLine
import java.io.File
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
    lateinit var packageRoot: String

    override fun call(): Int {
        val input = file.readText()
        val document = Document.load(input)
        val generator = KotlinGenerator(packageRoot)
        generator.generate(document)
        return 0
    }
}
