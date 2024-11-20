@file:Suppress("NAME_SHADOWING")

package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.AnodizerTarget
import io.github.amzn.anodizer.codegen.context.Ctx
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.core.Options

/**
 * An [AnodizerTarget] for the Kotlin language, templates can be overridden using [KotlinFeatures].
 */
public class KotlinTarget : AnodizerTarget {

    private val name: String = "kotlin"

    /**
     * A key for this service.
     */
    override fun getName(): String = name

    /**
     * Generate all sources for this target.
     *
     * @param model
     * @param options
     * @return
     */
    override fun generate(model: AnodizerModel, options: Options): File {

        // prepare model
        val model = Ctx.build(model)
        val features = KotlinFeatures(model, options)

        // generate all features with defaults
        val pkg = options.getString("package")!!.split(".")
        val src = File.dir(name)
        val dir = src.mkdir("src").mkdir("main").mkdir("kotlin").mkdirp(pkg)
        dir.addAll(features.model())
        dir.addAll(features.serde())

        return src
    }

    public companion object {

        /**
         * Create a [KotlinFeatures] instance for the given [model].
         *
         * @param model
         * @return
         */
        @JvmStatic
        public fun features(model: AnodizerModel, options: Options): KotlinFeatures {
            val model = Ctx.build(model)
            return KotlinFeatures(model, options)
        }
    }
}
