package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell

class MergedHeaderCell(val headerCell: HeaderCell, val headerCells: List<HeaderCell>) : HeaderCell() {

    init {
        if (headerCell is OrderedHeaderCell) throw IllegalArgumentException("Use OrderedHeaderCell first instead")
    }

    override fun matches(cell: Cell) = headerCell.matches(cell)
}
