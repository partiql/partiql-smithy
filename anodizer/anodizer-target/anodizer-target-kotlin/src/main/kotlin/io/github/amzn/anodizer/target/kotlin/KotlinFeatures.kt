package io.github.amzn.anodizer.target.kotlin

import io.github.amzn.anodizer.core.Options
import io.github.amzn.anodizer.codegen.Templates
import io.github.amzn.anodizer.codegen.context.CtxModel
import io.github.amzn.anodizer.core.File

/**
 * !! WARNING: USE THIS DIRECTLY AT YOUR OWN RISK !!
 *
 * The [KotlinFeatures] class enables controlling the generation of the various features of a target.
 */
public class KotlinFeatures internal constructor(
    private val model: CtxModel,
    private val options: Options,
) {

    /**
     * The default Kotlin templates are in this JAR's resources.
     */
    private val templates = Templates.loader(this::class.java)

    /**
     * Generate the model files.
     *
     * @param options    (OPTIONAL) option overrides.
     * @param templates  (OPTIONAL) template overrides.
     * @return
     */
    public fun model(options: Options? = null, templates: Templates? = null): List<File> {
        val files = mutableListOf<File>()
        val o = if (options == null) this.options else this.options.override(options)
        val t = if (templates == null) this.templates else this.templates.override(templates)
        files.add(KotlinModel(model, o, t).generate())
        files.add(primitives(t))
        return files
    }

    /**
     * Generate the serde files.
     *
     * @param options    (OPTIONAL) option overrides.
     * @param templates  (OPTIONAL) template overrides.
     * @return
     */
    public fun serde(options: Options? = null, templates: Templates? = null): List<File> {
        val files = mutableListOf<File>()
        val o = if (options == null) this.options else this.options.override(options)
        val t = if (templates == null) this.templates else this.templates.override(templates)
        files.add(KotlinReader(model, o, t).generate())
        files.add(KotlinWriter(model, o, t).generate())
        return files
    }

    private fun primitives(templates: Templates): File {
        val file = File.file("IonPrimitive.kt")
        val content = templates.apply("ion_primitives", object {
            val `package` = options.getString("package")!!.split(".").joinToString(".")
        })
        file.write(content)
        return file
    }
}
