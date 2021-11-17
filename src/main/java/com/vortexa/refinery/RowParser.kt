package com.vortexa.refinery

import com.vortexa.refinery.cell.*
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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

    protected fun parseRequiredFieldAsString(row: Row, headerCell: HeaderCell): String {
        val cell = findCell(row, headerCell)
        return stringParser.parse(cell)
    }

    protected fun parseOptionalFieldAsString(row: Row, headerCell: HeaderCell): String? {
        val cell = findCell(row, headerCell)
        return stringParser.tryParse(cell)
    }

    protected fun parseRequiredFieldAsDouble(row: Row, headerCell: HeaderCell): Double {
        val cell = findCell(row, headerCell)
        return doubleParser.parse(cell)
    }

    protected fun parseOptionalFieldAsDouble(row: Row, headerCell: HeaderCell): Double? {
        val cell = findCell(row, headerCell)
        return doubleParser.tryParse(cell)
    }

    protected fun parseRequiredFieldAsInteger(row: Row, headerCell: HeaderCell): Int {
        val cell = findCell(row, headerCell)
        return intParser.parse(cell)
    }

    protected fun parseOptionalFieldAsInteger(row: Row, headerCell: HeaderCell): Int? {
        val cell = findCell(row, headerCell)
        return intParser.tryParse(cell)
    }

    protected fun parseRequiredFieldAsDateTime(row: Row, headerCell: HeaderCell): LocalDateTime {
        val cell = findCell(row, headerCell)
        return dateTimeParser.parse(cell)
    }

    protected fun parseOptionalFieldAsDateTime(row: Row, headerCell: HeaderCell): LocalDateTime? {
        val cell = findCell(row, headerCell)
        return dateTimeParser.tryParse(cell)
    }

    protected fun parseOptionalDateWithFormat(row: Row, headerCell: HeaderCell, format: DateTimeFormatter): LocalDate? {
        val cell = findCell(row, headerCell)
        return if (cell != null && cell.toString().isNotBlank()) {
            try {
                LocalDate.parse(cell.toString(), format)
            } catch (exception: DateTimeParseException) {
                throw CellParserException("Could not parse as date $cell")
            }
        } else {
            null
        }
    }

    private fun findCell(row: Row, headerCell: HeaderCell): Cell? {
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
