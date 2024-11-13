package io.github.amzn.anodizer.commands

import io.github.amzn.anodizer.AnodizerOptions
import io.github.amzn.anodizer.core.Encoding
import io.github.amzn.anodizer.core.File as AnodizerFile
import io.github.amzn.anodizer.lang.AnodizerParser
import io.github.amzn.anodizer.target.isl.IslTarget
import picocli.CommandLine
import java.io.File
import java.util.Stack
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

    /**
     * TODO perhaps add as a utility
     */
    private fun dump(file: AnodizerFile) {
        val stack = Stack<AnodizerFile>().apply { push(file) }
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
}
