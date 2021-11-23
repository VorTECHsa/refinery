package com.vortexa.refinery.dsl

import com.vortexa.refinery.GenericRowParser
import com.vortexa.refinery.RowParser
import com.vortexa.refinery.cell.IHeaderCell
import com.vortexa.refinery.cell.MergedHeaderCell
import com.vortexa.refinery.cell.OrderedHeaderCell
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.result.RowParserData
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
    val requiredColumns: Set<IHeaderCell>,
    val optionalColumns: Set<IHeaderCell> = setOf(),
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

    fun resolveHeaderCellIndex(headerRow: Row): Map<IHeaderCell, Int> {
        val (orderedCells, unorderedCells) = allColumns().partition { it is OrderedHeaderCell }
            .let { Pair(it.first as List<OrderedHeaderCell>, it.second) }

        val result = resolveOrderedHeaders(headerRow, orderedCells) + resolveUnorderedHeaders(headerRow,unorderedCells)

        return result.flatMap {
            when(val cell = it.key) {
                is MergedHeaderCell -> cell.headerCells.mapIndexed{i, hc -> Pair(hc, it.value + i)}
                else -> listOf(Pair(cell, it.value))
            }
        }.toMap()
    }

    private fun resolveOrderedHeaders(row: Row, orderedCells: List<OrderedHeaderCell>): Map<IHeaderCell, Int> {
        val matches = mutableMapOf<IHeaderCell, Int>()
        row.cellIterator().asSequence().forEach { cell ->
            val headerCellOrNull = orderedCells.sortedBy { it.priority }
                .filterNot { matches.contains(it) }
                .firstOrNull {oc -> oc.contains(cell.stringCellValue)}
            if (headerCellOrNull != null) matches[headerCellOrNull.headerCell] = cell.columnIndex
        }
        return matches
    }

    private fun resolveUnorderedHeaders(row: Row, orderedCells: List<IHeaderCell>): Map<IHeaderCell, Int> {
        return row.cellIterator().asSequence().mapNotNull { cell ->
            val headerCellOrNull = orderedCells.firstOrNull { hc -> hc.contains(cell.stringCellValue) }
            if (headerCellOrNull != null) Pair(headerCellOrNull,cell.columnIndex) else null
        }.toMap()
    }

    private fun allColumns(): Set<IHeaderCell> = requiredColumns + optionalColumns

}
