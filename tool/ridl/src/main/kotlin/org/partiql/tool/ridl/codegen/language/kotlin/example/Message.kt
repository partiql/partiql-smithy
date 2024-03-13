package org.partiql.tool.ridl.codegen.language.kotlin.example

import com.amazon.ion.IonReader
import com.amazon.ion.IonType
import com.amazon.ion.IonWriter

public class Namespace private constructor() {

    public data class Message(
        @JvmField val foo: Int,
        @JvmField val bar: Int,
    ) {

        public fun write(writer: IonWriter) {
            writer.addTypeAnnotation(tag)
            writer.stepIn(IonType.SEXP)
            writer.writeInt(foo.toLong())
            writer.writeInt(bar.toLong())
            writer.stepOut()
        }

        public companion object {

            @JvmStatic
            private val tag: String = "message"

            @JvmStatic
            public fun read(reader: IonReader) {
                val tags = reader.typeAnnotations
                assert(tags.size == 1)
                assert(tags[0] == tag)
                reader.stepIn()
                val foo: Int = reader.intValue()
                val bar: Int = reader.intValue()
                reader.stepOut()
                Message(foo, bar)
            }
        }
    }
}
