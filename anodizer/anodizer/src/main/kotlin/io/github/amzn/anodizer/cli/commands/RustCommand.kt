// package io.github.amzn.anodizer.commands
//
// import io.github.amzn.anodizer.codegen.Model
// import io.github.amzn.anodizer.codegen.Templates
// import io.github.amzn.anodizer.lang.AnodizerParser
// import picocli.CommandLine
// import java.io.File
// import java.nio.file.Path
// import java.util.concurrent.Callable
//
// @CommandLine.Command(
//     name = "rust",
//     mixinStandardHelpOptions = true,
//     description = ["Generates Rust sources"]
// )
// internal class RustCommand : Callable<Int> {
//
//     @CommandLine.Parameters(
//         index = "0",
//         description = ["Type definition file"]
//     )
//     lateinit var file: File
//
//     @CommandLine.Option(
//         names = ["--domain"],
//         description = ["Type domain name"],
//     )
//     var domain: String? = null
//
//     @CommandLine.Option(
//         names = ["-I"],
//         description = ["Include directory"]
//     )
//     var include: Path? = null
//
//     override fun call(): Int {
//         // Configure
//         val domain = domain ?: file.nameWithoutExtension
//         // Parse
//         val input = file.readText()
//         val document = AnodizerParser.parse(input, domain, include)
//         val templates = Templates("rust")
//         val context = Model.contextualize(document)
//         // Generate, for now just print to stdout
//         val buffer = io.github.amzn.anodizer.codegen.buffer()
//         RustDomain(context, templates).generate(buffer)
//         RustFactory(context, templates).generate(buffer)
//         RustWriter(context, templates).generate(buffer)
//         RustReader(context, templates).generate(buffer)
//         println(buffer)
//         return 0
//     }
// }
