package com.vortexa.refinery.cell

import com.vortexa.refinery.exceptions.CellParserException
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeFormatCellParser {

    fun tryParse(cell: Cell?, format: DateTimeFormatter): LocalDateTime? {
        if (cell == null) return null
        return when {
            cell.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell) -> cell.localDateTimeCellValue
            else -> parseStringWithFormatter(cell.toString().trim(), format)
        }
    }

    fun parse(cell: Cell?, format: DateTimeFormatter): LocalDateTime {
        return tryParse(cell, format)
            ?: throw CellParserException("Failed to parse ${cell.toString()} to LocalDateTime with formatter $format")
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
