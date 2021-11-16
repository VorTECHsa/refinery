package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType.NUMERIC

class DoubleCellParser : CellParser<Double> {

    override fun tryParse(cell: Cell?): Double? {
        if (cell == null) return null
        return when (cell.cellType) {
            NUMERIC -> cell.numericCellValue
            else -> cell.toString().trim().toDoubleOrNull()
        }
    }
}
