package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell

/**
 * Can be used to define order of header cell.
 * In case you have the same header mentioned twice use this to differentiate.
 * `OrderedHeaderCell(SimpleHeaderCell("header_pattern"), 1)`
 *
 * You can also have `MergedHeaderCell` as a target
 *
 * @property headerCell
 * @property priority lower means it comes first
 */
class OrderedHeaderCell(val headerCell: AbstractHeaderCell, val priority: Int) : AbstractHeaderCell() {
    override fun matches(cell: Cell) = headerCell.matches(cell)
}
