package com.vortexa.refinery.cell.parser

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType

class StringCellParser : CellParser<String> {

    override fun tryParse(cell: Cell?): String? {
        if (cell == null) return null
        val value = when {
            cell.getConcreteCellType() == CellType.STRING -> cell.stringCellValue.trim()
            else -> cell.toString().trim()
        }
        return value.ifEmpty { null }
    }
}
