package io.github.amzn.anodizer.smithy

import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.core.Definition
import io.github.amzn.anodizer.core.File
import io.github.amzn.anodizer.smithy.util.Transformer
import io.github.amzn.anodizer.smithy.util.Translator
import software.amazon.smithy.build.FileManifest
import software.amazon.smithy.model.shapes.Shape
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Path
import java.util.stream.Stream

/**
 * Utility methods for interacting with the Smithy abstractions.
 */
public object AnodizerUtil {

    /**
     * Translate .smithy to .idl for base plugin.
     */
    public fun translate(shapes: Stream<Shape>): ByteArray {

        val buffer = ByteArrayOutputStream()
        val out = PrintStream(buffer)
        val translator = Translator(out)

        for (shape in shapes) {
            if (shape.isMemberShape) {
                // skip members
                continue
            }
            shape.accept(translator)
            out.println()
        }

        return buffer.toByteArray()
    }

    /**
     * Transform a smithy model into an Anodizer model.
     */
    public fun transform(domain: String, shapes: Stream<Shape>): AnodizerModel {

        val definitions = mutableListOf<Definition>()
        val transformer = Transformer()

        for (shape in shapes) {
            if (shape.isMemberShape) {
                continue
            }
            val definition = shape.accept(transformer)
            if (definition != null) {
                definitions.add(definition)
            }
        }

        return AnodizerModel(domain, definitions)
    }

    /**
     * Writes the file (tree) to the manifest at the root.
     *
     * @param file
     * @param manifest
     */
    public fun write(file: File, manifest: FileManifest) {
        write(manifest.baseDir, file, manifest)
    }

    /**
     * Writes the file (tree) to the manifest at the given path.
     *
     * @param path
     * @param file
     * @param manifest
     */
    private fun write(path: Path, file: File, manifest: FileManifest) {
        val dest = path.resolve(file.getName())
        if (file.isDir()) {
            for (child in file.getChildren()) {
                write(dest, child, manifest)
            }
        } else {
            manifest.writeFile(dest, file.toInputStream())
        }
    }
}
