package org.partiql.tool.ridl.codegen

import org.partiql.tool.ridl.model.Document
import java.io.File

public interface Generator {

    public fun generate(document: Document): List<File>
}
