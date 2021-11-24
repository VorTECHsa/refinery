package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType

class RegexHeaderCell(val patterns: List<Regex>) : AbstractHeaderCell() {

    constructor(pattern: Regex) : this(listOf(pattern))

    override fun matches(cell: Cell): Boolean {
        if (cell.cellType != CellType.STRING) return false
        return patterns.any { it.containsMatchIn(cell.stringCellValue) }
    }

}



