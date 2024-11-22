package io.github.amzn.anodizer.smithy.kotlin

import com.amazon.ionelement.api.ionListOf
import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.core.Options
import io.github.amzn.anodizer.smithy.AnodizerUtil
import io.github.amzn.anodizer.target.kotlin.KotlinFeatures
import software.amazon.smithy.kotlin.codegen.core.CodegenContext
import software.amazon.smithy.kotlin.codegen.core.KotlinDelegator
import software.amazon.smithy.kotlin.codegen.integration.KotlinIntegration
import software.amazon.smithy.model.shapes.Shape
import java.util.stream.Stream

/**
 * The Anodizer Kotlin Integration which produces additional serde sources for the Smithy Kotlin codegen.
 */
public class SmithyIntegration : KotlinIntegration {

    override val order: Byte = 0

    override fun writeAdditionalFiles(ctx: CodegenContext, delegator: KotlinDelegator) {

        // load shapes
        val namespace = ctx.settings.service.namespace
        val shapes = load(namespace, ctx)

        // transform to anodizer model
        val domain = domain(namespace)
        val model = AnodizerUtil.transform(domain, shapes)
        val pkg = ctx.settings.pkg.name.split(".")

        // generate serde using the smithy overrides and smithy templates
        val symbols = SmithySymbols(model)
        val templates = Templates.loader(this::class.java)
        val features = KotlinFeatures.override(symbols, templates)
        val options = options(ctx)

        // generate serde feature with overrides
        val src = File.dir("src")
        val dir = src.mkdir("main").mkdir("kotlin").mkdirp(pkg).mkdir("serde")
        dir.addAll(features.serde(options))

        // write via smithy manifest
        AnodizerUtil.write(src, delegator.fileManifest)
    }

    /**
     * Filter all shapes to the configured service's namespace.
     */
    private fun load(namespace: String, context: CodegenContext): Stream<Shape> {
        return context.model.shapes().filter { it.id.namespace == namespace }
    }

    /**
     * Derive a domain name from the service namespace.
     */
    private fun domain(namespace: String): String = namespace.split(".").last()

    private fun options(ctx: CodegenContext): Options {
        val pkg = ctx.settings.pkg.name
        val options = ionStructOf(
            "package" to ionString("$pkg.serde"),
            "imports" to ionListOf(ionString("$pkg.model.*"))
        )
        return Options.load(options)
    }
}
