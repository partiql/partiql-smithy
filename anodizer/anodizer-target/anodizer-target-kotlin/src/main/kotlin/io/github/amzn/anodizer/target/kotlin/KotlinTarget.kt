@file:Suppress("NAME_SHADOWING")

package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.AnodizerOptions
import io.github.amzn.anodizer.AnodizerTarget
import io.github.amzn.anodizer.codegen.Context
import io.github.amzn.anodizer.codegen.context.Contextualize
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.core.File

/**
 * An [AnodizerTarget] for the Kotlin language.
 */
public class KotlinTarget : AnodizerTarget {

    private val name: String = "kotlin"

    private val templates: Templates = Templates()

    override fun getName(): String = name

    override fun generate(model: AnodizerModel, options: AnodizerOptions): File {
        val context = Contextualize.contextualize(model)
        val options = KotlinOptions.load(options)
        val dir = File.dir(name)
        val src = dir.mkdir("src").mkdir("main").mkdir("kotlin").mkdirp(options.pkg)
        src.add(generateDomain(context, options))
        src.add(generateReader(context, options))
        src.add(generateWriter(context, options))
        src.add(primitives(context, options))
        return dir
    }

    private fun generateDomain(context: Context.Domain, options: KotlinOptions): File {
        return KotlinDomain(context, options, templates).generate()
    }

    private fun generateReader(context: Context.Domain, options: KotlinOptions): File {
        return KotlinReader(context, options, templates).generate()
    }

    private fun generateWriter(context: Context.Domain, options: KotlinOptions): File {
        return KotlinWriter(context, options, templates).generate()
    }

    private fun primitives(context: Context.Domain, options: KotlinOptions): File {
        val file = File.file("IonPrimitive.kt")
        val content = templates.apply("ion_primitives", object {
            val `package` = options.pkg.joinToString(".")
        })
        file.write(content)
        return file
    }

    /**
     * TODO this should be factored such to reduce duplication.
     *
     * The primary difference between instance and static methods here
     * is where the models are coming from - ie anodizer or smithy.
     *
     * I think the model should distinguish if this is an internal or external model
     * so that codegen can be simplified and we don't need the duplication.
     *
     * This will also be necessary when "anodizing" the smithy generated structures.
     */
    public companion object {

        @JvmStatic
        public fun reader(model: AnodizerModel, options: KotlinOptions): File {
            val context = Contextualize.contextualize(model)
            val templates = Templates()
            return KotlinReader(context, options, templates).generate()
        }

        @JvmStatic
        public fun writer(model: AnodizerModel, options: KotlinOptions): File {
            val context = Contextualize.contextualize(model)
            val templates = Templates()
            return KotlinWriter(context, options, templates).generate()
        }

        @JvmStatic
        public fun primitives(model: AnodizerModel, options: KotlinOptions): File {
            val templates = Templates()
            val file = File.file("IonPrimitive.kt")
            val content = templates.apply("ion_primitives", object {
                val `package` = options.pkg.joinToString(".")
            })
            file.write(content)
            return file
        }
    }
}
