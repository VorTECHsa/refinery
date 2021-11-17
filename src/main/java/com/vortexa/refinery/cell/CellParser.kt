package com.vortexa.refinery.cell

import com.vortexa.refinery.exceptions.CellParserException
import org.apache.poi.ss.usermodel.Cell

interface CellParser<T> {

    fun parse(cell: Cell?): T {
        return tryParse(cell) ?: throw CellParserException("Cell is either empty or not parsable")
    }

    fun tryParse(cell: Cell?): T?
}
