package com.vortexa.refinery.cell.parser

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import java.time.LocalDateTime

class DateTimeCellParser : CellParser<LocalDateTime> {

    override fun tryParse(cell: Cell?): LocalDateTime? {
        if (cell == null) return null
        return when {
            cell.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell) -> cell.localDateTimeCellValue
            else -> null
        }
    }

}
