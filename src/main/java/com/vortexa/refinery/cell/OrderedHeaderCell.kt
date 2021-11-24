package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell

class OrderedHeaderCell(val headerCell: HeaderCell, val priority: Int) : HeaderCell() {
    override fun matches(cell: Cell) = headerCell.matches(cell)
}



