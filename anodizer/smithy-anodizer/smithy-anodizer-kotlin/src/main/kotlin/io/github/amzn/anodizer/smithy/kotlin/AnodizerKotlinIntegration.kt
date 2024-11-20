package io.github.amzn.anodizer.smithy.kotlin

import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.core.Options
import io.github.amzn.anodizer.smithy.AnodizerUtil
import io.github.amzn.anodizer.target.kotlin.KotlinTarget
import software.amazon.smithy.kotlin.codegen.core.CodegenContext
import software.amazon.smithy.kotlin.codegen.core.KotlinDelegator
import software.amazon.smithy.kotlin.codegen.integration.KotlinIntegration
import software.amazon.smithy.model.shapes.Shape
import java.util.stream.Stream

/**
 * TODO
 */
public class AnodizerKotlinIntegration : KotlinIntegration {

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
        val options = Options.load("{ package: \"${ctx.settings.pkg.name}.serde\" }".trimIndent())
        val templates = Templates.loader(this::class.java)
        val features = KotlinTarget.features(model, options)

        // generate serde feature with overrides
        val src = File.dir("src")
        val dir = src.mkdir("main").mkdir("kotlin").mkdirp(pkg).mkdir("serde")
        dir.addAll(features.serde(templates = templates))

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
}
