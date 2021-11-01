package com.vortexa.refinery.result

class GenericParsedRecord() : ParsedRecord() {
    constructor(data: Map<String, Any>) : this() {
        this.extractedRawData = data
    }
}
