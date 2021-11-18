package com.vortexa.refinery.cell

import com.vortexa.refinery.exceptions.CellParserException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeFormatCellParserTest : CellParserTest() {

    @Test
    fun `should parse date time with formatter`() {
        // expect
        val parser = DateTimeFormatCellParser(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        assertThat(parser.parse(dateStr()))
            .isEqualTo(LocalDate.of(2021, 11, 15).atStartOfDay())

        val parser2 = DateTimeFormatCellParser(DateTimeFormatter.ofPattern("dd-MM-yyyy @ HH:mm"))
        assertThat(parser2.parse(dateTimeStr()))
            .isEqualTo(LocalDateTime.of(2021, 11, 15, 15, 43))
    }

    @Test
    fun `should throw if could not convert string to date time`() {
        val parser = DateTimeFormatCellParser(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        assertThatThrownBy { parser.parse(stringCell()) }
            .isInstanceOf(CellParserException::class.java)
    }
}
