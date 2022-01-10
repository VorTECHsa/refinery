package com.vortexa.refinery.cell.parser

import com.vortexa.refinery.exceptions.CellParserException
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType

interface CellParser<T> {

    fun parse(cell: Cell?): T {
        return tryParse(cell) ?: throw CellParserException("Cell is either empty or not parsable")
    }

    fun tryParse(cell: Cell?): T?

    fun Cell.getConcreteCellType(): CellType {
        return if (this.cellType == CellType.FORMULA) this.cachedFormulaResultType else this.cellType
    }
}
