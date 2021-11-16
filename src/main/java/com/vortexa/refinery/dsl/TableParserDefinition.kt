package com.vortexa.refinery.dsl

import com.vortexa.refinery.GenericRowParser
import com.vortexa.refinery.RowParser
import com.vortexa.refinery.cell.AbstractHeaderCell
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.result.RowParserData
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row

/**
 * Tells us how to parse tables within spreadsheets
 *
 * @property requiredColumns will be used to find header rows
 * @property optionalColumns the columns that might not have values
 * @property rowParserFactory a factory for creating a row parser
 * @property anchor support for table headers (will end up in metadata)
 * @property hasDivider support for single rows within the table that split data (will end up in metadata)
 */
data class TableParserDefinition(
    val requiredColumns: Set<AbstractHeaderCell>,
    val optionalColumns: Set<AbstractHeaderCell> = setOf(),
    val rowParserFactory: (rowParserData: RowParserData, exceptionManager: ExceptionManager) -> RowParser = ::GenericRowParser,
    val anchor: String? = null,
    val hasDivider: Boolean = false
) {

    fun isHeaderRow(row: Row): Boolean {
        // abstract HeaderRow implementation
        val cellValues = row.cellIterator().asSequence()
            .filter { it.cellType == CellType.STRING }
            .map { it.stringCellValue.trim().lowercase() }
            .toSet()
        return this.requiredColumns.filterNot { it.inside(cellValues) }
            .isEmpty()
    }

    fun resolveHeaderCellIndex(cell: Cell): Pair<AbstractHeaderCell, Int>? {
        val headerCellOrNull = allColumns().filter { hc -> hc.contains(cell.stringCellValue) }
        val maxCellOrNull = when {
            headerCellOrNull.isEmpty() -> null
            headerCellOrNull.size == 1 -> headerCellOrNull.first()
            else -> headerCellOrNull.maxByOrNull { it.maxContainsLength(cell.stringCellValue)!! }

        }
        return if (maxCellOrNull == null) null else Pair(maxCellOrNull, cell.columnIndex)
    }

    private fun allColumns(): Set<AbstractHeaderCell> = requiredColumns + optionalColumns

}
