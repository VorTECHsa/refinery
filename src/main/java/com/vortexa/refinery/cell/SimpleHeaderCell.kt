package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell


/**
 * Matches headers using exact string comparison
 *
 * @property name
 */
class SimpleHeaderCell(val name: String) : AbstractHeaderCell() {
    override fun matches(cell: Cell): Boolean {
        return cell.toString().trim() == name
    }

}



