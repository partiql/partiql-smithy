package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.AnodizerOptions

internal class KotlinOptions(@JvmField val pkg: List<String>) {

    companion object {

        @JvmStatic
        fun load(options: AnodizerOptions): KotlinOptions = try {
            val pkg = options.getString("package")?.split(".")
                ?: throw IllegalArgumentException("kotlin target requires `package` configuration in smithy-build.json")
            KotlinOptions(pkg)
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("invalid smithy-build.json for kotlin: $options", ex)
        }
    }
}
