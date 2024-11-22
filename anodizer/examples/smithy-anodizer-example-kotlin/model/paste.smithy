$version: "2"

namespace amzn.paste

service Paste {
    version: "0.0.1"
    operations: [
        PutPaste
        GetPaste
    ]
}

operation PutPaste {
    input: PutPasteRequest
    output: PutPasteResponse
}

structure PutPasteRequest {
    @required
    contentType: String

    @required
    content: Blob
}

structure PutPasteResponse {
    @required
    id: Long
}

operation GetPaste {
    input: GetPasteRequest
    output: GetPasteResponse
}

structure GetPasteRequest {
    @required
    id: Long
}

structure GetPasteResponse {
    @required
    contentType: String

    @required
    content: Blob
}
