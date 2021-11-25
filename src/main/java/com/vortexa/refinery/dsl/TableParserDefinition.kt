package com.vortexa.refinery.dsl

import com.vortexa.refinery.GenericRowParser
import com.vortexa.refinery.RowParser
import com.vortexa.refinery.cell.AbstractHeaderCell
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.result.RowParserData
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

    // todo: does not belong to TableParserDefinition, should be extracted to a separate class
    fun isHeaderRow(row: Row): Boolean {
        val cellValues = row.cellIterator().asSequence().toSet()
        return this.requiredColumns.filterNot { it.inside(cellValues) }
            .isEmpty()
    }

    fun allColumns(): Set<AbstractHeaderCell> = requiredColumns + optionalColumns

}
