package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType

/**
 * Matches headers using case insensitive substring comparison
 *
 * @property patterns
 */
class StringHeaderCell(val patterns: List<String>) : AbstractHeaderCell() {

    constructor(pattern: String) : this(listOf(pattern))

    override fun matches(cell: Cell): Boolean {
        if (cell.cellType != CellType.STRING) return false
        return patterns.any { cell.stringCellValue.trim().lowercase().contains(it.lowercase()) }
    }

}



