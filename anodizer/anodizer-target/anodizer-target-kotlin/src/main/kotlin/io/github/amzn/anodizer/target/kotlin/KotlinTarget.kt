@file:Suppress("NAME_SHADOWING")

package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.AnodizerTarget
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
        val features = KotlinFeatures.standard(model)

        // generate all features with defaults
        val pkg = options.getString("package")!!.split(".")
        val src = File.dir(name)
        val dir = src.mkdir("src").mkdir("main").mkdir("kotlin").mkdirp(pkg)
        dir.addAll(features.model(options))
        dir.addAll(features.serde(options))

        return src
    }
}
