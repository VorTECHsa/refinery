package com.vortexa.refinery.cell.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DateTimeCellParserTest : CellParserTest() {

    private val parser: CellParser<LocalDateTime> = DateTimeCellParser()

    @Test
    fun `should parse to date time`() {
        // expect
        assertThat(parser.tryParse(dateCell())).isEqualTo(LocalDateTime.parse("2021-12-12T00:00"))
    }

    @Test
    fun `should try to parse to date time or return null if failed to do that`() {
        // expect
        assertThat(parser.tryParse(stringCell())).isNull()
        assertThat(parser.tryParse(emptyStringCell())).isNull()
        assertThat(parser.tryParse(doubleCell())).isNull()
        assertThat(parser.tryParse(intCell())).isNull()
        assertThat(parser.tryParse(dateCell())).isEqualTo(LocalDateTime.parse("2021-12-12T00:00"))
        assertThat(parser.tryParse(boolCell())).isNull()
        assertThat(parser.tryParse(nullCell())).isNull()
        assertThat(parser.tryParse(doubleAsStringCell())).isNull()
        assertThat(parser.tryParse(doubleInt())).isNull()
        assertThat(parser.tryParse(dateTime())).isEqualTo(LocalDateTime.parse("2021-12-12T15:43:43"))
    }

    @Test
    fun `should parse to date time from formula`() {
        // expect
        assertThat(parser.tryParse(dateCellFromFormula())).isEqualTo(LocalDateTime.parse("2021-12-12T00:00"))
    }

    @Test
    fun `should try to parse to date time or return null if failed to do that from formula`() {
        // expect
        assertThat(parser.tryParse(stringCellFromFormula())).isNull()
        assertThat(parser.tryParse(emptyStringCellFromFormula())).isNull()
        assertThat(parser.tryParse(doubleCellFromFormula())).isNull()
        assertThat(parser.tryParse(intCellFromFormula())).isNull()
        assertThat(parser.tryParse(dateCellFromFormula())).isEqualTo(LocalDateTime.parse("2021-12-12T00:00"))
        assertThat(parser.tryParse(boolCellFromFormula())).isNull()
        assertThat(parser.tryParse(doubleAsStringCellFromFormula())).isNull()
        assertThat(parser.tryParse(doubleIntFromFormula())).isNull()
        assertThat(parser.tryParse(dateTimeFromFormula())).isEqualTo(LocalDateTime.parse("2021-12-12T15:43:43"))
    }
}
