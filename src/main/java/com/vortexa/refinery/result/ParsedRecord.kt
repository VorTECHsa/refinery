package com.vortexa.refinery.result

import java.util.UUID

abstract class ParsedRecord {
    var groupId: UUID? = null

    // easy way to exclude the data from serialization
    // the better way would be to write the exclusion strategy on serializer level
    @Transient
    var extractedRawData: Map<String, Any>? = null

    fun cloneRawData(from: ParsedRecord) {
        extractedRawData = from.extractedRawData
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParsedRecord

        if (groupId != other.groupId) return false
        if (extractedRawData != other.extractedRawData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupId?.hashCode() ?: 0
        result = 31 * result + (extractedRawData?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "ParsedRecord(groupId=$groupId, extractedRawData=$extractedRawData)"
    }
}
