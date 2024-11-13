package io.github.amzn.anodizer.smithy

import io.github.amzn.anodizer.AnodizerOptions
import io.github.amzn.anodizer.AnodizerTarget
import io.github.amzn.anodizer.lang.AnodizerParser
import software.amazon.smithy.build.PluginContext
import software.amazon.smithy.build.SmithyBuildPlugin
import software.amazon.smithy.model.node.Node
import software.amazon.smithy.model.shapes.Shape
import java.io.ByteArrayInputStream
import java.util.ServiceLoader
import java.util.stream.Stream

/**
 * Produce an anodizer model from the smithy model, then codegen for all configured targets.
 */
public class AnodizerPlugin : SmithyBuildPlugin {

    override fun getName(): String = "anodizer"

    override fun execute(context: PluginContext) {

        // load the model
        val namespace = context.settings.getStringMember("namespace").get().value
        val shapes = load(namespace, context)
        val buffer = AnodizerUtil.translate(shapes)

        // write the model for posterity
        context.fileManifest.writeFile("$namespace.ridl", ByteArrayInputStream(buffer))

        // determine targets
        val tasks = tasks(context)
        if (tasks.isEmpty()) {
            return
        }

        // invoke codegen
        val domain = domain(context)
        val model = AnodizerParser.parse(ByteArrayInputStream(buffer), domain)
        for (task in tasks) {
            val file = task.target.generate(model, task.options)
            AnodizerUtil.write(file, context.fileManifest)
        }
    }

    /**
     * Filter all shapes to the configured namespace.
     */
    private fun load(namespace: String, context: PluginContext): Stream<Shape> {
        return context.model.shapes().filter { it.id.namespace == namespace }
    }

    /**
     * Check classpath for available targets.
     */
    private fun targets(loader: ClassLoader): Map<String, AnodizerTarget> {
        return ServiceLoader.load(AnodizerTarget::class.java, loader).associateBy { it.getName() }
    }

    /**
     * Extract target configurations from the plugin settings JSON nodes.
     */
    private fun tasks(context: PluginContext): List<Task> {
        val targets = targets(context.pluginClassLoader.get())
        val tasks = mutableListOf<Task>()
        val settings = context.settings.getObjectMember("targets").get().stringMap
        for ((t, o) in settings) {
            val target = targets[t] ?: throw IllegalArgumentException("Could not find target `$t` on the classpath")
            val options = AnodizerOptions.load(Node.printJson(o))
            tasks.add(Task(target, options))
        }
        return tasks
    }

    /**
     * Produce an anodizer domain name from the smithy-build.json.
     */
    private fun domain(context: PluginContext): String {
        return context.settings.getStringMember("domain").get().value
    }

    /**
     * Hold a resolved target and its options from the smithy-build.json
     *
     * @property target
     * @property options
     */
    private class Task(
        @JvmField val target: AnodizerTarget,
        @JvmField val options: AnodizerOptions,
    )
}
