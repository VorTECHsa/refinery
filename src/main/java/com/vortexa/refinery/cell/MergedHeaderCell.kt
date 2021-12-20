package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell

/**
 * Can be used to parse a merged header cell
 *
 * @see com.vortexa.refinery.cell.OrderedHeaderCell
 *
 * @property headerCell matching style. Any of `SimpleHeaderCell`, `StringHeaderCell` or `RegexHeaderCell`
 * @property headerCells what cells to map to. Any of `SimpleHeaderCell`, `StringHeaderCell` or `RegexHeaderCell`
 */
class MergedHeaderCell(val headerCell: AbstractHeaderCell, val headerCells: List<AbstractHeaderCell>) :
    AbstractHeaderCell() {

    init {
        if (headerCell is OrderedHeaderCell) throw IllegalArgumentException("Use OrderedHeaderCell first instead")
    }

    override fun matches(cell: Cell) = headerCell.matches(cell)

    override fun toString(): String = headerCells.toString()
}
