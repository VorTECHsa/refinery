package com.vortexa.refinery.cell

import com.vortexa.refinery.exceptions.CellParserException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeFormatCellParserTest : CellParserTest() {

    private val parser = DateTimeFormatCellParser()

    @Test
    fun `should parse date time with formatter`() {
        // expect
        assertThat(parser.parse(dateStr(), DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            .isEqualTo(LocalDate.of(2021, 11, 15).atStartOfDay())

        assertThat(parser.parse(dateTimeStr(), DateTimeFormatter.ofPattern("dd-MM-yyyy @ HH:mm")))
            .isEqualTo(LocalDateTime.of(2021, 11, 15, 15, 43))
    }

    @Test
    fun `should throw if could not convert string to date time`() {
        assertThatThrownBy { parser.parse(stringCell(), DateTimeFormatter.ofPattern("dd-MM-yyyy")) }
            .isInstanceOf(CellParserException::class.java)
    }
}
