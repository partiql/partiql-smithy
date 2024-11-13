package io.github.amzn.anodizer.commands

import io.github.amzn.anodizer.lang.AnodizerParser
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
        names = ["--domain"],
        description = ["Type domain name"],
    )
    var domain: String? = null

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
        val domain = domain ?: file.nameWithoutExtension
        val input = file.readText()
        val model = AnodizerParser.parse(input, domain, include)
        TODO("kotlin command")
        return 0
    }
}
