package io.github.amzn.anodizer.cli.commands

import io.github.amzn.anodizer.core.Options
import io.github.amzn.anodizer.cli.util.dump
import io.github.amzn.anodizer.lang.AnodizerParser
import io.github.amzn.anodizer.target.kotlin.KotlinTarget
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

        // arguments
        val input = file.readText()
        val domain = domain ?: file.nameWithoutExtension

        // prepare
        val model = AnodizerParser.parse(input, domain, include)
        val target = KotlinTarget()
        val options = options()

        // generate
        val dir = target.generate(model, options)

        // dump to stdout
        dump(dir)

        return 0
    }

    // kind of weird shoving the strings through but ehh works fine.
    private fun options(): Options {
        val options = buildString {
            appendLine("{")
            appendLine("  package: \"$pkg\",")
            appendLine("}")
        }
        return Options.load(options)
    }
}
