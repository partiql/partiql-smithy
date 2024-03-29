package io.github.amzn.ridl.model.load

import io.github.amzn.ridl.model.Definition
import io.github.amzn.ridl.model.Document
import io.github.amzn.ridl.model.Namespace
import io.github.amzn.ridl.model.RType
import io.github.amzn.ridl.model.RTypeArray
import io.github.amzn.ridl.model.RTypeEnum
import io.github.amzn.ridl.model.RTypeNamed
import io.github.amzn.ridl.model.RTypePrimitive
import io.github.amzn.ridl.model.RTypeRef
import io.github.amzn.ridl.model.RTypeStruct
import io.github.amzn.ridl.model.RTypeUnion
import io.github.amzn.ridl.model.RTypeUnit
import io.github.amzn.ridl.model.Type

internal abstract class Rewriter {

    open fun rewrite(document: Document): Document = Document(definitions = document.definitions.map { rewrite(it) })

    open fun rewrite(definition: Definition): Definition = when (definition) {
        is Namespace -> rewrite(definition)
        is Type -> rewrite(definition)
    }

    open fun rewrite(namespace: Namespace): Namespace = Namespace(
        name = namespace.name,
        definitions = namespace.definitions.map { rewrite(it) },
    )

    open fun rewrite(type: Type): Type = Type(
        name = type.name, type = rewrite(type.type)
    )

    open fun rewrite(type: RType): RType = when (type) {
        is RTypeArray -> rewrite(type)
        is RTypeEnum -> rewrite(type)
        is RTypeNamed -> rewrite(type)
        is RTypePrimitive -> rewrite(type)
        is RTypeRef -> rewrite(type)
        is RTypeStruct -> rewrite(type)
        is RTypeUnion -> rewrite(type)
        is RTypeUnit -> rewrite(type)
    }

    open fun rewrite(type: RTypeArray): RTypeArray {
        val size = type.size
        val item = rewrite(type.item)
        return RTypeArray(item, size)
    }

    open fun rewrite(type: RTypeEnum): RType = type

    open fun rewrite(type: RTypeRef): RTypeRef = when (type) {
        is RTypeNamed -> rewrite(type)
        is RTypePrimitive -> rewrite(type)
    }

    open fun rewrite(type: RTypeNamed): RTypeRef = type

    open fun rewrite(type: RTypePrimitive): RTypeRef = type

    open fun rewrite(type: RTypeStruct): RTypeStruct {
        val fields = type.fields.map {
            val fName = it.name
            val fType = rewrite(it.type)
            RTypeStruct.Field(fName, fType)
        }
        return RTypeStruct(fields)
    }

    open fun rewrite(type: RTypeUnion): RTypeUnion {
        val variants = type.variants.map { rewrite(it) }
        return RTypeUnion(variants)
    }

    open fun rewrite(type: RTypeUnit): RType = type
}
