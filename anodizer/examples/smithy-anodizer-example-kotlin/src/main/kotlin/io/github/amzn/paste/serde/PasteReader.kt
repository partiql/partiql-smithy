package io.github.amzn.paste.serde

import com.amazon.ion.IonReader
import com.amazon.ion.IonType
import com.amazon.ion.system.IonReaderBuilder
import io.github.amzn.paste.model.GetPasteRequest
import io.github.amzn.paste.model.GetPasteResponse
import io.github.amzn.paste.model.PutPasteRequest
import io.github.amzn.paste.model.PutPasteResponse
import io.github.amzn.paste.runtime.AnodizerReader
import java.io.InputStream

/**
 * Modifications
 *  - Import from model.
 *  - Drop the namespace prefix.
 *  - Change input/output to request/response.
 *  - Change constructors to the lambda builder.
 */
public abstract class PasteReader(reader: IonReader) : AnodizerReader(reader) {

    public abstract fun readPutPasteRequest(): PutPasteRequest

    public abstract fun readPutPasteResponse(): PutPasteResponse

    public abstract fun readGetPasteResponse(): GetPasteResponse

    public abstract fun readGetPasteRequest(): GetPasteRequest

    public companion object {

        @JvmStatic
        public fun text(input: InputStream): PasteReader = Text(IonReaderBuilder.standard().build(input))

        @JvmStatic
        public fun text(reader: IonReader): PasteReader = Text(reader)

        @JvmStatic
        public fun packed(input: InputStream): PasteReader = Packed(IonReaderBuilder.standard().build(input))

        @JvmStatic
        public fun packed(reader: IonReader): PasteReader = Packed(reader)
    }

    private class Text(reader: IonReader) : PasteReader(reader) {

        override fun readPutPasteRequest(): PutPasteRequest {
            assertType(IonType.STRUCT, "put_paste_input")
            reader.stepIn()
            assertField("content_type"); reader.next()
            val contentType = reader.stringValue()
            assertField("content"); reader.next()
            val content = reader.newBytes()
            reader.stepOut()
            return PutPasteRequest {
                this.contentType = contentType
                this.content = content
            }
        }

        override fun readPutPasteResponse(): PutPasteResponse {
            assertType(IonType.STRUCT, "put_paste_output")
            reader.stepIn()
            assertField("id"); reader.next()
            val id = reader.longValue()
            reader.stepOut()
            return PutPasteResponse {
                this.id = id
            }
        }

        override fun readGetPasteResponse(): GetPasteResponse {
            assertType(IonType.STRUCT, "get_paste_output")
            reader.stepIn()
            assertField("content_type"); reader.next()
            val contentType = reader.stringValue()
            assertField("content"); reader.next()
            val content = reader.newBytes()
            reader.stepOut()
            return GetPasteResponse {
                this.contentType = contentType
                this.content = content
            }
        }

        override fun readGetPasteRequest(): GetPasteRequest {
            assertType(IonType.STRUCT, "get_paste_input")
            reader.stepIn()
            assertField("id"); reader.next()
            val id = reader.longValue()
            reader.stepOut()
            return GetPasteRequest {
                this.id = id
            }
        }
    }

    private class Packed(reader: IonReader) : PasteReader(reader) {

        override fun readPutPasteRequest(): PutPasteRequest {
            assertType(IonType.SEXP)
            reader.stepIn()
            reader.next()
            val contentType = reader.stringValue()
            reader.next()
            val content = reader.newBytes()
            reader.stepOut()
            return PutPasteRequest {
                this.contentType = contentType
                this.content = content
            }
        }

        override fun readPutPasteResponse(): PutPasteResponse {
            assertType(IonType.SEXP)
            reader.stepIn()
            reader.next()
            val id = reader.longValue()
            reader.stepOut()
            return PutPasteResponse {
                this.id = id
            }
        }

        override fun readGetPasteResponse(): GetPasteResponse {
            assertType(IonType.SEXP)
            reader.stepIn()
            reader.next()
            val contentType = reader.stringValue()
            reader.next()
            val content = reader.newBytes()
            reader.stepOut()
            return GetPasteResponse {
                this.contentType = contentType
                this.content = content
            }
        }

        override fun readGetPasteRequest(): GetPasteRequest {
            assertType(IonType.SEXP)
            reader.stepIn()
            reader.next()
            val id = reader.longValue()
            reader.stepOut()
            return GetPasteRequest {
                this.id = id
            }
        }
    }
}
