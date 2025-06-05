package com.vortexa.refinery.cell.parser

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType

interface CellParser<T> {
    fun tryParse(cell: Cell?): T?

    fun Cell.getConcreteCellType(): CellType {
        return if (this.cellType == CellType.FORMULA) this.cachedFormulaResultType else this.cellType
    }
}
