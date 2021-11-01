package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell

interface CellParser<T> {

    fun parse(cell: Cell?): T
}
