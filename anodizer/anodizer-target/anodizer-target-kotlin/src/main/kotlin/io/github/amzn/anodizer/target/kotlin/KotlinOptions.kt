package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.AnodizerOptions

/**
 * TODO
 *
 * @property pkg
 */
public class KotlinOptions(@JvmField public val pkg: List<String>) {

    internal companion object {

        @JvmStatic
        internal fun load(options: AnodizerOptions): KotlinOptions = try {
            val pkg = options.getString("package")?.split(".")
                ?: throw IllegalArgumentException("kotlin target requires `package` configuration in smithy-build.json")
            KotlinOptions(pkg)
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("invalid smithy-build.json for kotlin: $options", ex)
        }
    }
}
