package com.vortexa.refinery

import com.vortexa.refinery.cell.HeaderCell
import com.vortexa.refinery.cell.HeaderRowResolver
import com.vortexa.refinery.cell.MergedCellsResolver
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.exceptions.TableParserException
import com.vortexa.refinery.exceptions.UncapturedHeadersException
import com.vortexa.refinery.result.GenericParsedRecord
import com.vortexa.refinery.result.Metadata
import com.vortexa.refinery.result.ParsedRecord
import com.vortexa.refinery.result.RowParserData
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import java.util.*

internal class TableParser(
    private val sheet: Sheet,
    private val definition: TableParserDefinition,
    private val metadata: Metadata,
    private val location: TableLocation,
    private val mergedCellsResolver: MergedCellsResolver,
    private val exceptionManager: ExceptionManager
) {

    private val headerRowResolver = HeaderRowResolver()

    fun parse(): List<ParsedRecord> {
        val headerRow = findHeaderRow() ?: return emptyList()
        val tableLocationWithHeader = TableLocationWithHeader(location.minRow, headerRow.rowNum, location.maxRow)
        val columnHeaders = headerRowResolver.resolveHeaderCellIndex(headerRow, definition.allColumns())
        val allHeadersMapping = mapAllHeaders(headerRow)
        checkUncapturedHeaders(columnHeaders, allHeadersMapping, tableLocationWithHeader)
        val enrichedMetadata = enrichMetadata()
        return when (definition.hasDivider) {
            false -> {
                val rowParser = definition.rowParserFactory.invoke(
                    RowParserData(
                        columnHeaders,
                        mergedCellsResolver,
                        enrichedMetadata,
                        allHeadersMapping
                    ), exceptionManager
                )
                parseTableWithoutDividers(rowParser, tableLocationWithHeader)
            }
            true -> parseTableWithDividers(columnHeaders, enrichedMetadata, tableLocationWithHeader, allHeadersMapping)
        }
    }

    private fun checkUncapturedHeaders(
        columnHeaders: Map<HeaderCell, Int>,
        allHeadersMapping: Map<String, Int>,
        tableLocationWithHeader: TableLocationWithHeader
    ) {
        val capturedColumnIndexes = columnHeaders.values.toSet()
        val uncapturedColumns = allHeadersMapping
            .filterNot { capturedColumnIndexes.contains(it.value) }
            .map { "${it.key} @ ${it.value + 1}" }
        if (uncapturedColumns.isNotEmpty()) {
            val uncapturedHeadersMessage = uncapturedColumns.joinToString { it }
            exceptionManager.register(
                UncapturedHeadersException(uncapturedHeadersMessage),
                ExceptionManager.Location(sheet.sheetName, tableLocationWithHeader.headerRow + 1)
            )
        }
    }

    private fun parseTableWithoutDividers(rowParser: RowParser, location: TableLocationWithHeader): List<ParsedRecord> {
        val parsedRecords = mutableListOf<ParsedRecord>()
        for (rowIndex in location.range()) {
            val row = sheet.getRow(rowIndex)
            if (row.containsSomethingMeaningful() && !definition.isHeaderRow(row)) {
                parseRecordAndAddToResults(rowParser, row, parsedRecords)
            }
        }
        return parsedRecords
    }

    private fun parseTableWithDividers(
        columnHeaders: Map<HeaderCell, Int>,
        enrichedMetadata: Metadata,
        location: TableLocationWithHeader,
        allHeadersMapping: Map<String, Int>
    ): List<ParsedRecord> {
        val parsedRecords = mutableListOf<ParsedRecord>()
        val rowParser =
            definition.rowParserFactory.invoke(
                RowParserData(
                    columnHeaders,
                    mergedCellsResolver,
                    enrichedMetadata,
                    allHeadersMapping
                ), exceptionManager
            )
        for (rowIndex in location.range()) {
            val row = sheet.getRow(rowIndex)
            if (row.containsSomethingMeaningful() && !definition.isHeaderRow(row)) {
                if (row.isDivider()) {
                    enrichedMetadata.setDivider(row.divider())
                    continue
                }
                parseRecordAndAddToResults(rowParser, row, parsedRecords)
            }
        }
        return parsedRecords
    }

    private fun parseRecordAndAddToResults(
        rowParser: RowParser,
        row: Row,
        parsedRecords: MutableList<ParsedRecord>
    ) {
        val record = rowParser.toRecordOrDefault(row)
        if (record is GenericParsedRecord) {
            parsedRecords.add(record)
        } else {
            if (parsedRecords.isEmpty() || parsedRecords.last() is GenericParsedRecord) {
                parsedRecords.add(record)
            } else {
                val recordToAdd = rowParser.extractDataFromPreviousRecord(record, parsedRecords.last())
                recordToAdd.cloneRawData(record)
                if (rowParser.shouldGroupRows(recordToAdd, parsedRecords.last())) {
                    setGroupIdForRows(recordToAdd, parsedRecords.last())
                }
                parsedRecords.add(recordToAdd)
            }
        }
    }

    private fun setGroupIdForRows(current: ParsedRecord, previous: ParsedRecord) {
        if (previous.groupId == null) {
            val id = UUID.randomUUID()
            previous.groupId = id
            current.groupId = id
        } else {
            current.groupId = previous.groupId
        }
    }

    private fun findHeaderRow(): Row? {
        return try {
            val locationRange = location.minRow..location.maxRow
            locationRange.asSequence()
                .map { sheet.getRow(it) }
                .first { row -> definition.isHeaderRow(row) }
        } catch (e: RuntimeException) {
            exceptionManager.register(
                TableParserException(
                    "No table header found for anchor:${definition.anchor} and reqCols:${definition.requiredColumns}"
                ),
                ExceptionManager.Location(sheet.sheetName, location.minRow + 1)
            )
            null
        }
    }

    private fun mapAllHeaders(headerRow: Row): Map<String, Int> {
        return headerRow.cellIterator().asSequence()
            .mapNotNull { cell ->
                val mergedCell: Cell? = mergedCellsResolver[cell.rowIndex, cell.columnIndex]
                if (mergedCell != null) {
                    return@mapNotNull ("${mergedCell}_${cell.columnIndex + 1}") to cell.columnIndex
                } else if (cell.stringCellValue.isNotEmpty()) {
                    return@mapNotNull cell.stringCellValue to cell.columnIndex
                } else {
                    return@mapNotNull null
                }
            }
            .toMap()
    }

    private fun enrichMetadata(): Metadata {
        return if (definition.anchor != null) {
            metadata + (Metadata.ANCHOR to definition.anchor)
        } else {
            metadata
        }
    }

    private fun Row?.containsSomethingMeaningful(): Boolean {
        if (this == null) {
            return false
        }
        return this.prefilterCells().any()
    }

    private fun Row.isDivider(): Boolean {
        val cellValues = this.prefilterCells()
            .map { it.toString() }
            .toList()
        return cellValues.size == 1
    }

    private fun Row.divider(): String {
        return this.prefilterCells()
            .map { it.toString() }
            .single()
    }

    private fun Row.prefilterCells() = this.cellIterator().asSequence()
        .filter { it.cellType != CellType.BLANK }
        .filter { it.toString().isNotBlank() }


    internal data class TableLocation(val minRow: Int, val maxRow: Int)

    private data class TableLocationWithHeader(val minRow: Int, val headerRow: Int, val maxRow: Int) {
        fun range(): IntRange {
            return headerRow..maxRow
        }
    }

}
