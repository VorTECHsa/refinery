package com.vortexa.refinery.cell

import com.vortexa.refinery.exceptions.CellParserException
import org.apache.poi.ss.usermodel.Cell

class RequiredStringCellParser : CellParser<String> {

    override fun parse(cell: Cell?): String {
        val value = cell?.toString()?.trim()
        if (value == null || value.isEmpty()) {
            throw CellParserException("Cell could not be empty")
        }
        return value
    }
}
