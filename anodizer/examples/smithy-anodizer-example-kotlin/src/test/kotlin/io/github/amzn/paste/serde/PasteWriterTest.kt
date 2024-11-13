package io.github.amzn.paste.serde

import io.github.amzn.paste.model.PutPasteRequest
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalStdlibApi::class)
class PasteWriterTest {

    private val format = HexFormat {
        upperCase = true
        bytes {
            bytesPerLine = 16
            bytesPerGroup = 4
            groupSeparator = " | "
            byteSeparator = " "
            bytePrefix = ""
            byteSuffix = ""
        }
    }

    @Test
    public fun text() {
        val writer = PasteWriter.text(System.out)
        val req = PutPasteRequest {
            contentType = "text/plain"
            content = "Hello World".toByteArray(Charsets.UTF_8)
        }
        writer.writePutPasteRequest(req)
    }

    @Test
    public fun packed() {
        val buffer = ByteArrayOutputStream()
        val writer = PasteWriter.packed(buffer)
        val req = PutPasteRequest {
            contentType = "text/plain"
            content = "Hello World".toByteArray(Charsets.UTF_8)
        }
        writer.writePutPasteRequest(req)
        writer.finish()

        println(buffer.toByteArray().toHexString(format))
    }
}
