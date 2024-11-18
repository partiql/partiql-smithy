@file:Suppress("NAME_SHADOWING")

package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.AnodizerOptions
import io.github.amzn.anodizer.AnodizerTarget
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.codegen.context.Ctx
import io.github.amzn.anodizer.codegen.context.CtxModel
import io.github.amzn.anodizer.core.File

/**
 * An [AnodizerTarget] for the Kotlin language.
 */
public class KotlinTarget : AnodizerTarget {

    private val name: String = "kotlin"

    public companion object {

        @JvmStatic
        public fun faceted(): Facets {
            TODO()
        }
    }

    override fun getName(): String = name

    override fun generate(model: AnodizerModel, options: AnodizerOptions): File {
        val context = Ctx.build(model)
        val facets = Facets(context, KotlinOptions.load(options))
        val options = KotlinOptions.load(options)
        val dir = File.dir(name)
        val src = dir.mkdir("src").mkdir("main").mkdir("kotlin").mkdirp(options.pkg)
        for (file in facets.all()) {
            src.add(file)
        }
        return dir
    }

    /**
     * The [KotlinTarget] requires a zero-argument constructor, the facets provide access to the individual
     *
     * Q:
     */
    public class Facets internal constructor(
        private val context: CtxModel,
        private val options: KotlinOptions,
    ) {

        private val templates: Templates = Templates()

        public fun all(): List<File> = listOf(
            types(),
            reader(),
            writer(),
            primitives(),
        )

        public fun types(): File {
            return KotlinDomain(context, options, templates).generate()
        }

        public fun reader(): File {
            return KotlinReader(context, options, templates).generate()
        }

        public fun writer(): File {
            return KotlinWriter(context, options, templates).generate()
        }

        private fun primitives(): File {
            val file = File.file("IonPrimitive.kt")
            val content = templates.apply("ion_primitives", object {
                val `package` = options.pkg.joinToString(".")
            })
            file.write(content)
            return file
        }
    }
}
