package io.github.amzn.paste.serde

import com.amazon.ion.IonType
import com.amazon.ion.IonWriter
import com.amazon.ion.system.IonBinaryWriterBuilder
import com.amazon.ion.system.IonTextWriterBuilder
import io.github.amzn.paste.model.GetPasteRequest
import io.github.amzn.paste.model.GetPasteResponse
import io.github.amzn.paste.model.PutPasteRequest
import io.github.amzn.paste.model.PutPasteResponse
import io.github.amzn.paste.runtime.AnodizerWriter
import java.io.OutputStream

/**
 * Modifications
 *  - changed input/output to request/response
 *  - non-null assertions on the nullable fields.
 */
public abstract class PasteWriter(writer: IonWriter) : AnodizerWriter(writer) {

    public abstract fun writePutPasteRequest(value: PutPasteRequest)

    public abstract fun writePutPasteResponse(value: PutPasteResponse)

    public abstract fun writeGetPasteResponse(value: GetPasteResponse)

    public abstract fun writeGetPasteRequest(value: GetPasteRequest)

    public companion object {

        @JvmStatic
        public fun text(output: Appendable): PasteWriter = Text(IonTextWriterBuilder.standard().build(output))

        @JvmStatic
        public fun text(writer: IonWriter): PasteWriter = Text(writer)

        @JvmStatic
        public fun packed(output: OutputStream): PasteWriter = Packed(IonBinaryWriterBuilder.standard().build(output))

        @JvmStatic
        public fun packed(writer: IonWriter): PasteWriter = Packed(writer)
    }

    private class Text(writer: IonWriter) : PasteWriter(writer) {

        override fun writePutPasteRequest(value: PutPasteRequest) {
            writer.setTypeAnnotations("put_paste_input")
            writer.stepIn(IonType.STRUCT)
            writer.setFieldName("content_type"); writer.writeString(value.contentType)
            writer.setFieldName("content"); writer.writeBlob(value.content)
            writer.stepOut()
        }

        override fun writePutPasteResponse(value: PutPasteResponse) {
            writer.setTypeAnnotations("put_paste_output")
            writer.stepIn(IonType.STRUCT)
            writer.setFieldName("id"); writer.writeInt(value.id!!)
            writer.stepOut()
        }

        override fun writeGetPasteResponse(value: GetPasteResponse) {
            writer.setTypeAnnotations("get_paste_output")
            writer.stepIn(IonType.STRUCT)
            writer.setFieldName("content_type"); writer.writeString(value.contentType)
            writer.setFieldName("content"); writer.writeBlob(value.content)
            writer.stepOut()
        }

        override fun writeGetPasteRequest(value: GetPasteRequest) {
            writer.setTypeAnnotations("get_paste_input")
            writer.stepIn(IonType.STRUCT)
            writer.setFieldName("id"); writer.writeInt(value.id!!)
            writer.stepOut()
        }
    }

    private class Packed(writer: IonWriter) : PasteWriter(writer) {

        override fun writePutPasteRequest(value: PutPasteRequest) {
            writer.stepIn(IonType.SEXP)
            writer.writeString(value.contentType)
            writer.writeBlob(value.content)
            writer.stepOut()
        }

        override fun writePutPasteResponse(value: PutPasteResponse) {
            writer.stepIn(IonType.SEXP)
            writer.writeInt(value.id!!)
            writer.stepOut()
        }

        override fun writeGetPasteResponse(value: GetPasteResponse) {
            writer.stepIn(IonType.SEXP)
            writer.writeString(value.contentType)
            writer.writeBlob(value.content)
            writer.stepOut()
        }

        override fun writeGetPasteRequest(value: GetPasteRequest) {
            writer.stepIn(IonType.SEXP)
            writer.writeInt(value.id!!)
            writer.stepOut()
        }
    }
}
