package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.core.Options

/**
 * !! WARNING: USE THIS DIRECTLY AT YOUR OWN RISK !!
 *
 * The [KotlinFeatures] class enables controlling the generation of the various features of a target.
 */
public class KotlinFeatures internal constructor(
    private val symbols: KotlinSymbols,
    private val templates: Templates,
) {

    public companion object {

        /**
         * Create the standard feature generator from a model.
         *
         * @param model
         * @return
         */
        @JvmStatic
        public fun standard(model: AnodizerModel): KotlinFeatures {
            val symbols = KotlinSymbols.standard(model)
            val templates = Templates.loader(this::class.java) // standard templates in this JAR.
            return KotlinFeatures(symbols, templates)
        }

        /**
         * Create a custom features backed by custom symbols and templates.
         *
         * @param symbols
         * @param templates
         * @return
         */
        @JvmStatic
        public fun override(symbols: KotlinSymbols, templates: Templates): KotlinFeatures {
            val t = Templates.loader(this::class.java).override(templates)
            return KotlinFeatures(symbols, t)
        }
    }

    /**
     * Generate the model files.
     * @return
     */
    public fun model(options: Options): List<File> {
        val files = mutableListOf<File>()
        files.add(KotlinModel(symbols, options, templates).generate())
        files.add(primitives(options))
        return files
    }

    /**
     * Generate the serde files.
     */
    public fun serde(options: Options): List<File> {
        val files = mutableListOf<File>()
        // files.add(KotlinReader(symbols, options, templates).generate())
        files.add(KotlinWriter(symbols, options, templates).generate())
        return files
    }

    /**
     * Generate the primitive classes for type aliases.
     */
    private fun primitives(options: Options): File {
        val file = File.file("IonPrimitive.kt")
        val content = templates.apply("ion_primitives", object {
            val `package` = options.getString("package")!!
        })
        file.write(content)
        return file
    }
}
