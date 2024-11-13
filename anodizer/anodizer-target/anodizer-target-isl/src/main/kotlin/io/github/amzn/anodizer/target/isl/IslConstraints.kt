package io.github.amzn.anodizer.target.isl

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.StructField
import com.amazon.ionelement.api.SymbolElement
import com.amazon.ionelement.api.field
import com.amazon.ionelement.api.ionInt
import com.amazon.ionelement.api.ionListOf
import com.amazon.ionelement.api.ionStructOf
import com.amazon.ionelement.api.ionSymbol

/**
 * Constraint helper functions. Not a complete list; only what I need for now.
 */
internal class IslConstraints : Iterable<StructField> {

    private val constraints: MutableList<StructField> = mutableListOf()

    override fun iterator(): Iterator<StructField> = constraints.iterator()

    fun isEmpty() = constraints.isEmpty()

    fun build(): List<StructField> = constraints

    fun add(constraint: StructField) = this.apply {
        this.constraints.add(constraint)
    }

    fun addAll(constraints: Collection<StructField>) = this.apply {
        this.constraints.addAll(constraints)
    }

    fun addAll(constraints: IslConstraints) = this.apply {
        this.constraints.addAll(constraints.constraints)
    }

    fun name(name: String) = this.apply {
        constraints.add(field("name", ionSymbol(name)))
    }

    fun annotation(annotation: String, required: Boolean) = this.apply {
        var value = ionListOf(ionSymbol(annotation))
        if (required) {
            value = value.withAnnotations("required")
        }
        constraints.add(field("annotations", value))
    }

    fun annotations(annotations: List<String>, required: Boolean = true) = this.apply {
        var value = ionListOf(annotations.map { ionSymbol(it) })
        if (required) {
            value = value.withAnnotations("required")
        }
        constraints.add(field("annotations", value))
    }

    fun fields(fields: Collection<StructField>, closed: Boolean = true) = this.apply {
        var value = ionStructOf(fields)
        if (closed) {
            value = value.withAnnotations("closed")
        }
        constraints.add(field("fields", value))
    }

    fun orderedElements(elements: Collection<IonElement>) = this.apply {
        constraints.add(field("ordered_elements", ionListOf(elements)))
    }

    fun oneOf(types: Collection<SymbolElement>) = this.apply {
        constraints.add(field("one_of", ionListOf(types)))
    }

    fun type(type: IonElement) = this.apply {
        constraints.add(field("type", type))
    }

    fun element(element: IonElement) = this.apply {
        constraints.add(field("element", element))
    }

    fun containerLength(length: Int) = this.apply {
        constraints.add(field("container_length", ionInt(length.toLong())))
    }

    fun byteLength(length: Int) = this.apply {
        constraints.add(field("byte_length", ionInt(length.toLong())))
    }

    fun precision(precision: Int) = this.apply {
        constraints.add(field("precision", ionInt(precision.toLong())))
    }

    fun exponent(exponent: Int) = this.apply {
        constraints.add(field("exponent", ionInt(exponent.toLong())))
    }

    fun validValues(values: List<IonElement>) = this.apply {
        constraints.add(field("valid_values", ionListOf(values)))
    }
}
