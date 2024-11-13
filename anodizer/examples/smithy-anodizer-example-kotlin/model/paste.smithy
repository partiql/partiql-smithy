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
    input: PutPasteInput
    output: PutPasteOutput
}

structure PutPasteInput {
    contentType: String
    content: Blob
}

structure PutPasteOutput {
    id: Long
}

operation GetPaste {
    input: GetPasteInput
    output: GetPasteOutput
}

structure GetPasteInput {
    id: Long
}

structure GetPasteOutput {
    contentType: String
    content: Blob
}
