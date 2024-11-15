package io.github.amzn.anodizer.cli.commands

import io.github.amzn.anodizer.AnodizerOptions
import io.github.amzn.anodizer.cli.util.dump
import io.github.amzn.anodizer.core.Encoding
import io.github.amzn.anodizer.lang.AnodizerParser
import io.github.amzn.anodizer.target.isl.IslTarget
import picocli.CommandLine
import java.io.File
import java.util.concurrent.Callable

@CommandLine.Command(
    name = "isl",
    mixinStandardHelpOptions = true,
    description = ["Generates IonSchema sources"]
)
internal class IslCommand : Callable<Int> {

    @CommandLine.Parameters(
        index = "0",
        description = ["Type definition file"]
    )
    lateinit var file: File

    @CommandLine.Option(
        names = ["--domain"],
        description = ["Type domain name // default is file name"],
    )
    var domain: String? = null

    @CommandLine.Option(
        names = ["--encoding"],
        description = ["Encoding determines ISL"],
        required = false,
    )
    var encoding: Encoding = Encoding.TEXT

    override fun call(): Int {

        // arguments
        val input = file.readText()
        val domain = domain ?: file.nameWithoutExtension

        // prepare
        val model = AnodizerParser.parse(input, domain)
        val target = IslTarget()
        val options = options()

        // generate
        val dir = target.generate(model, options)

        // dump to stdout
        dump(dir)

        return 0
    }

    private fun options(): AnodizerOptions = AnodizerOptions.empty()
}
