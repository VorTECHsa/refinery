package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell

class MergedHeaderCell(val headerCell: AbstractHeaderCell, val headerCells: List<AbstractHeaderCell>) :
    AbstractHeaderCell() {

    init {
        if (headerCell is OrderedHeaderCell) throw IllegalArgumentException("Use OrderedHeaderCell first instead")
    }

    override fun matches(cell: Cell) = headerCell.matches(cell)
}
