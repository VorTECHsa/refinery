package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell

class SimpleHeaderCell(val name: String) : HeaderCell() {
    override fun matches(cell: Cell): Boolean {
        return cell.toString().trim() == name
    }

}



