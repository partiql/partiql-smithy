package io.github.amzn.ridl.model.load

import io.github.amzn.ridl.model.*

/**
 * Helper function to remove all aliases and replace references with the base type.
 */
internal object Lower : Rewriter() {

    override fun rewrite(document: Document) = super.rewrite(Document(
        definitions = document.definitions.filterNot { it.isAlias() },
    ))

    override fun rewrite(namespace: Namespace) = super.rewrite(Namespace(
        name = namespace.name,
        definitions = namespace.definitions.filterNot { it.isAlias() },
    ))

    override fun rewrite(type: RTypeNamed): RTypeRef = type.base ?: type

    private fun Definition.isAlias(): Boolean {
        if (this !is Type) return false
        return (type is RTypeNamed && type.base != null) || (type is RTypePrimitive)
    }
}
