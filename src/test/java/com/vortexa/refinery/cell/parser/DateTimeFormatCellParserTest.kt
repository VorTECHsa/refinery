package com.vortexa.refinery.cell.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeFormatCellParserTest : CellParserTest() {

    @Test
    fun `should parse date time with formatter`() {
        // expect
        val parser = DateTimeFormatCellParser(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        assertThat(parser.tryParse(dateStr()))
            .isEqualTo(LocalDate.of(2021, 11, 15).atStartOfDay())

        val parser2 = DateTimeFormatCellParser(DateTimeFormatter.ofPattern("dd-MM-yyyy @ HH:mm"))
        assertThat(parser2.tryParse(dateTimeStr()))
            .isEqualTo(LocalDateTime.of(2021, 11, 15, 15, 43))
    }

    @Test
    fun `should return null if could not convert string to date time`() {
        val parser = DateTimeFormatCellParser(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        assertThat(parser.tryParse(stringCell())).isNull()
    }

    @Test
    fun `should parse date time with formatter from formula`() {
        // expect
        val parser = DateTimeFormatCellParser(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        assertThat(parser.tryParse(dateStrFromFormula()))
            .isEqualTo(LocalDate.of(2021, 11, 15).atStartOfDay())

        val parser2 = DateTimeFormatCellParser(DateTimeFormatter.ofPattern("dd-MM-yyyy @ HH:mm"))
        assertThat(parser2.tryParse(dateTimeStrFromFormula()))
            .isEqualTo(LocalDateTime.of(2021, 11, 15, 15, 43))
    }
}
