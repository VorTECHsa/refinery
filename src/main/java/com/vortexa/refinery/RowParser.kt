package com.vortexa.refinery

import com.vortexa.refinery.cell.AbstractHeaderCell
import com.vortexa.refinery.cell.parser.CellParser
import com.vortexa.refinery.cell.parser.DateTimeCellParser
import com.vortexa.refinery.cell.parser.DateTimeFormatCellParser
import com.vortexa.refinery.cell.parser.DoubleCellParser
import com.vortexa.refinery.cell.parser.IntCellParser
import com.vortexa.refinery.cell.parser.StringCellParser
import com.vortexa.refinery.exceptions.CellParserException
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.result.GenericParsedRecord
import com.vortexa.refinery.result.Metadata
import com.vortexa.refinery.result.ParsedRecord
import com.vortexa.refinery.result.RowParserData
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.round

/**
 * Used to define how to parse your rows
 * Includes access to metadata like sheet name, anchors and any key value pairs you might have extracted
 * @property rowParserData holder for the metadata. access through rowParserData.metadata
 */
abstract class RowParser(
    val rowParserData: RowParserData,
    private val exceptionManager: ExceptionManager
) {

    private val stringParser = StringCellParser()
    private val doubleParser = DoubleCellParser()
    private val intParser = IntCellParser()
    private val dateTimeParser = DateTimeCellParser()

    abstract fun toRecord(row: Row): ParsedRecord

    fun toRecordOrDefault(row: Row): ParsedRecord {
        val parsedRecord = try {
            toRecord(row)
        } catch (e: RuntimeException) {
            exceptionManager.register(e, ExceptionManager.Location(row.sheet.sheetName, row.rowNum + 1))
            val data = extractAllData(row)
            return GenericParsedRecord(data)
        }
        if (shouldStoreExtractedRawDataInParsedRecord()) {
            parsedRecord.extractedRawData = extractAllData(row)
        }
        return parsedRecord
    }

    /**
     * Optionally skip extraction of the data from the row if it meets some condition
     *
     * @param row subject to skip data extraction if condition is met
     * @return boolean whether the row should be skipped or not
     * */
    open fun skip(row: Row): Boolean = false

    /**
     * Optionally override this to extract data from the previous row if it's missing.
     * An example use case if missing date defined in the previous row
     *
     * @param current current record
     * @param previous previous record
     * @return a new records to be added
     */
    open fun extractDataFromPreviousRecord(current: ParsedRecord, previous: ParsedRecord): ParsedRecord {
        return current
    }

    /**
     * Optionally override this if you want to specify the rules for grouping consecutive rows within a table.
     * This will add a groupId that can then be used to correlate these rows.
     *
     * @param current current record
     * @param previous previous record
     * @return boolean of whether these should be grouped
     */
    open fun shouldGroupRows(current: ParsedRecord, previous: ParsedRecord): Boolean {
        return false
    }

    /**
     * Indicates whether the raw extracted data should be stored in ParsedRecord as Map<String, Any> or not
     * By default, the data will be extracted
     * @return boolean of whether the data should be extracted and stored or not
     * */
    open fun shouldStoreExtractedRawDataInParsedRecord(): Boolean {
        return true
    }

    /**
     * Extracts all data in denormalized way as a Map<String, Any>
     *
     * @return Map<String, Any> containing all the data extracted from the row + extracted metadata
     * */
    fun extractAllData(row: Row): Map<String, Any> {
        val rowData = extractDataFromRow(row)
        return rowParserData.metadata.allData() + rowData + (Metadata.ROW_NUMBER to row.rowNum + 1)
    }

    protected fun <T> parseRequiredField(
        row: Row,
        headerCell: AbstractHeaderCell,
        parser: CellParser<T>
    ): T {
        val cell = findCell(row, headerCell)
        parser.tryParse(cell)?.let { return it }

        val cellIndex = rowParserData.headerMap[headerCell]
        val columnMap = rowParserData.allHeadersMapping.entries.associate { (name, idx) -> idx to name }
        val columnName = columnMap[cellIndex]
        val parserName = parser::class.java.name.split(".").last()
        val reason = if (cell == null) "Cell is empty" else "Invalid value '$cell'"
        val message = "$parserName failed to parse required field '$columnName' at row ${row.rowNum}, col $cellIndex: $reason"

        throw CellParserException(message)
    }

    protected fun <T> parseOptionalField(row: Row, headerCell: AbstractHeaderCell, parser: CellParser<T>): T? {
        val cell = findCell(row, headerCell)
        return parser.tryParse(cell)
    }

    protected fun parseRequiredFieldAsString(row: Row, headerCell: AbstractHeaderCell): String {
        return parseRequiredField(row, headerCell, stringParser)
    }

    protected fun parseOptionalFieldAsString(row: Row, headerCell: AbstractHeaderCell): String? {
        return parseOptionalField(row, headerCell, stringParser)
    }

    protected fun parseRequiredFieldAsDouble(row: Row, headerCell: AbstractHeaderCell): Double {
        return parseRequiredField(row, headerCell, doubleParser)
    }

    protected fun parseOptionalFieldAsDouble(row: Row, headerCell: AbstractHeaderCell): Double? {
        return parseOptionalField(row, headerCell, doubleParser)
    }

    protected fun parseRequiredFieldAsInteger(row: Row, headerCell: AbstractHeaderCell): Int {
        return parseRequiredField(row, headerCell, intParser)
    }

    protected fun parseOptionalFieldAsInteger(row: Row, headerCell: AbstractHeaderCell): Int? {
        return parseOptionalField(row, headerCell, intParser)
    }

    protected fun parseRequiredFieldAsDateTime(row: Row, headerCell: AbstractHeaderCell): LocalDateTime {
        return parseRequiredField(row, headerCell, dateTimeParser)
    }

    protected fun parseOptionalFieldAsDateTime(row: Row, headerCell: AbstractHeaderCell): LocalDateTime? {
        return parseOptionalField(row, headerCell, dateTimeParser)
    }

    protected fun parseRequiredFieldAsDateTime(
        row: Row,
        headerCell: AbstractHeaderCell,
        format: DateTimeFormatter
    ): LocalDateTime {
        return parseRequiredField(row, headerCell, DateTimeFormatCellParser(format))
    }

    protected fun parseOptionalFieldAsDateTime(
        row: Row,
        headerCell: AbstractHeaderCell,
        format: DateTimeFormatter
    ): LocalDateTime? {
        return parseOptionalField(row, headerCell, DateTimeFormatCellParser(format))
    }

    private fun findCell(row: Row, headerCell: AbstractHeaderCell): Cell? {
        val cellIndex = rowParserData.headerMap[headerCell] ?: return null
        return findCellByIndex(row, cellIndex)
    }

    private fun findCellByIndex(
        row: Row,
        cellIndex: Int
    ): Cell? {
        val maybeMergedCell = rowParserData.mergedCellsResolver[row.rowNum, cellIndex]
        return maybeMergedCell ?: row.getCell(cellIndex, RETURN_BLANK_AS_NULL)
    }

    private fun extractDataFromRow(row: Row): Map<String, Any> {
        val headerMap = rowParserData.allHeadersMapping
        return headerMap.mapNotNull { resolveCellValue(it, row) }.toMap()
    }

    private fun resolveCellValue(headerCell: Map.Entry<String, Int>, row: Row): Pair<String, Any>? {
        val cell = findCellByIndex(row, headerCell.value) ?: return null
        val value = getCellValue(cell) ?: return null
        return headerCell.key to value
    }

    private fun getCellValue(cell: Cell): Any? {
        val cellType = if (cell.cellType == CellType.FORMULA) cell.cachedFormulaResultType else cell.cellType
        return when (cellType) {
            CellType.NUMERIC -> return if (DateUtil.isCellDateFormatted(cell)) {
                cell.localDateTimeCellValue
            } else {
                val doubleValue = cell.numericCellValue
                if (doubleValue == round(doubleValue)) doubleValue.toInt() else doubleValue
            }
            CellType.BOOLEAN -> cell.booleanCellValue
            CellType.STRING -> cell.stringCellValue
            else -> null
        }
    }
}
