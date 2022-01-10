package com.vortexa.refinery.cell.parser

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeFormatCellParser(private val format: DateTimeFormatter) : CellParser<LocalDateTime> {

    override fun tryParse(cell: Cell?): LocalDateTime? {
        if (cell == null) return null
        return when {
            cell.getConcreteCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(
                cell
            ) -> cell.localDateTimeCellValue
            else -> parseStringWithFormatter(cell.toString().trim(), format)
        }
    }

    private fun parseStringWithFormatter(dateStr: String, format: DateTimeFormatter): LocalDateTime? {
        return try {
            LocalDateTime.parse(dateStr, format)
        } catch (e: DateTimeException) {
            try {
                LocalDate.parse(dateStr, format).atStartOfDay()
            } catch (re: RuntimeException) {
                null
            }
        }
    }
}
