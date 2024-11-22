package io.github.amzn.anodizer.smithy.kotlin

import io.github.amzn.anodizer.AnodizerModel
import io.github.amzn.anodizer.codegen.context.CtxTypedef
import io.github.amzn.anodizer.target.kotlin.KotlinSymbols

public class SmithySymbols(model: AnodizerModel) : KotlinSymbols(model) {

    /**
     * For now, assume all Smithy symbols are in the current namespace (they're imported from .model)
     */
    override fun typeOf(typedef: CtxTypedef): String {
        val symbol = typedef.symbol()
        return symbol.name.pascal
    }
}
