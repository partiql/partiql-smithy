@file:Suppress("NAME_SHADOWING")

package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.AnodizerOptions
import io.github.amzn.anodizer.AnodizerTarget
import io.github.amzn.anodizer.codegen.Context
import io.github.amzn.anodizer.codegen.Contextualize
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.core.File

/**
 * An [AnodizerTarget] for the Kotlin language.
 */
public class KotlinTarget : AnodizerTarget {

    private val name: String = "kotlin"

    private val templates: Templates = Templates(name)

    override fun getName(): String = name

    override fun generate(model: AnodizerModel, options: AnodizerOptions): File {
        val context = Contextualize.contextualize(model)
        val options = KotlinOptions.load(options)
        val dir = File.dir(name)
        val src = dir.mkdir("src").mkdir("main").mkdir("kotlin").mkdirp(options.pkg)
        src.add(generateDomain(context, options))
        src.add(generateReader(context, options))
        src.add(generateWriter(context, options))
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
}
