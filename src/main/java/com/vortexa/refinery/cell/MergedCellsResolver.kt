package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
import org.apache.poi.ss.usermodel.Sheet
import java.util.Collections

class MergedCellsResolver(sheet: Sheet) {

    private val mergedCellsMap: Map<CellLocation, Cell>

    init {
        val tempMap = mutableMapOf<CellLocation, Cell>()
        sheet.mergedRegions.forEach { cellRangeAddress ->
            val cell =
                sheet.getRow(cellRangeAddress.firstRow).getCell(cellRangeAddress.firstColumn, RETURN_BLANK_AS_NULL)
            cell?.let { cellRef ->
                for (i in cellRangeAddress.firstRow until cellRangeAddress.lastRow + 1) {
                    for (j in cellRangeAddress.firstColumn until cellRangeAddress.lastColumn + 1) {
                        tempMap[CellLocation(i, j)] = cellRef
                    }
                }
            }
        }
        mergedCellsMap = Collections.unmodifiableMap(tempMap)
    }

    operator fun get(rowIndex: Int, columnIndex: Int): Cell? {
        return mergedCellsMap[CellLocation(rowIndex, columnIndex)]
    }

    data class CellLocation(val rowIndex: Int, val columnIndex: Int) {

        init {
            if (rowIndex < 0) throw IllegalArgumentException("row index should be non-negative")
            if (columnIndex < 0) throw IllegalArgumentException("column index should be non-negative")
        }
    }
}
