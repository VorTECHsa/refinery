package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell

class OrderedHeaderCell(val headerCell: AbstractHeaderCell, val priority: Int) : AbstractHeaderCell() {
    override fun matches(cell: Cell) = headerCell.matches(cell)
}



