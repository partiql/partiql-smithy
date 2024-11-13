package io.github.amzn.anodizer.smithy.kotlin

import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.smithy.AnodizerUtil
import io.github.amzn.anodizer.target.kotlin.KotlinOptions
import io.github.amzn.anodizer.target.kotlin.KotlinTarget
import software.amazon.smithy.kotlin.codegen.core.CodegenContext
import software.amazon.smithy.kotlin.codegen.core.KotlinDelegator
import software.amazon.smithy.kotlin.codegen.integration.KotlinIntegration
import software.amazon.smithy.model.shapes.Shape
import java.util.stream.Stream

/**
 * TODO
 */
public class AnodizerIntegration : KotlinIntegration {

    override val order: Byte = 0

    override fun writeAdditionalFiles(ctx: CodegenContext, delegator: KotlinDelegator) {

        // load shapes
        val namespace = ctx.settings.service.namespace
        val pkg = ctx.settings.pkg.name.split(".")
        val shapes = load(namespace, ctx)

        // transform to anodizer model
        val domain = domain(namespace)
        val options = KotlinOptions(pkg)
        val model = AnodizerUtil.transform(domain, shapes)

        // generate reader/writer
        val dir = File.dir("serde")
        dir.add(KotlinTarget.reader(model, options))
        dir.add(KotlinTarget.writer(model, options))
        AnodizerUtil.write(dir, delegator.fileManifest)
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
