package io.github.amzn.anodizer.target.isl

import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.AnodizerOptions
import io.github.amzn.anodizer.AnodizerTarget
import io.github.amzn.anodizer.AnodizerModel

public class IslTarget : AnodizerTarget {

    private val name = "isl"

    override fun getName(): String = name

    override fun generate(model: AnodizerModel, options: AnodizerOptions): File {
        val dir = File.dir(name)
        val ft = generateText(model)
        val fp = generatePacked(model)
        dir.add(ft, fp)
        return dir
    }

    private fun generateText(model: AnodizerModel): File {
        val file = File.file("${model.domain}.text.isl")
        val writer = IslWriter.text(file)
        writer.write(model)
        return file
    }

    private fun generatePacked(model: AnodizerModel): File {
        val file = File.file("${model.domain}.packed.isl")
        val writer = IslWriter.packed(file)
        writer.write(model)
        return file
    }
}
