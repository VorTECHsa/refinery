package com.vortexa.refinery

import com.vortexa.refinery.exceptions.CellParserException

class RequiredFieldParser {

    fun parse(field: String?): String {
        if (field == null || field.isEmpty()) {
            throw CellParserException("Cell could not be empty")
        }
        return field
    }

    fun parse(field: Any?): Any {
        return field ?: CellParserException("Cell could not be empty")
    }
}