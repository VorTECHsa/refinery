package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType

/**
 * Macthes headers using a regex or list of regexes
 *
 * @property patterns
 */
class RegexHeaderCell(val patterns: List<Regex>) : AbstractHeaderCell() {

    constructor(pattern: Regex) : this(listOf(pattern))

    constructor(pattern: String) : this(listOf(pattern.toRegex()))

    override fun matches(cell: Cell): Boolean {
        if (cell.cellType != CellType.STRING) return false
        return patterns.any { it.containsMatchIn(cell.stringCellValue) }
    }
}
