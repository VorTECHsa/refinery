package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell

sealed class AbstractHeaderCell {

    abstract fun matches(cell: Cell): Boolean

    fun inside(values: Set<Cell>) = values.any { this.matches(it) }
}
