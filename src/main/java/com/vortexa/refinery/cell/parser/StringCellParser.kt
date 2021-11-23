package com.vortexa.refinery.cell.parser

import org.apache.poi.ss.usermodel.Cell

class StringCellParser : CellParser<String> {

    override fun tryParse(cell: Cell?): String? {
        val value = cell?.toString()?.trim()
        return if (value == null || value.isEmpty()) {
            null
        } else {
            value
        }
    }
}
