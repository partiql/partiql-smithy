$version: "2"

namespace amzn.paste

service Paste {
    version: "0.0.1"
    operations: [
        PutPaste
        GetPaste
    ]
}

@idempotent
@http(code: 200, method: "PUT", uri: "/paste")
operation PutPaste {
    input: PutPasteInput
    output: PutPasteOutput
}

@input
structure PutPasteInput {
    @required
    @httpHeader("Content-Type")
    contentType: String

    @httpPayload
    content: Blob
}

@output
structure PutPasteOutput {
    @required
    id: Long
}

@readonly
@http(code: 200, method: "GET", uri: "/paste/{id}")
operation GetPaste {
    input: GetPasteInput
    output: GetPasteOutput
}

@input
structure GetPasteInput {
    @required
    @httpLabel
    id: Long
}

@output
structure GetPasteOutput {
    @required
    @httpHeader("Content-Type")
    contentType: String

    @httpPayload
    content: Blob
}
