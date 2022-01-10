package com.vortexa.refinery.cell.parser

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType.NUMERIC
import org.apache.poi.ss.usermodel.DateUtil

class DoubleCellParser : CellParser<Double> {

    override fun tryParse(cell: Cell?): Double? {
        if (cell == null) return null
        return when {
            cell.getConcreteCellType() == NUMERIC && !DateUtil.isCellDateFormatted(cell) -> cell.numericCellValue
            else -> cell.toString().trim().toDoubleOrNull()
        }
    }
}
