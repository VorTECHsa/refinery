package com.vortexa.refinery.cell.parser

import com.vortexa.refinery.exceptions.CellParserException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DateTimeCellParserTest : CellParserTest() {

    private val parser: CellParser<LocalDateTime> = DateTimeCellParser()

    @Test
    fun `should parse to date time`() {
        // expect
        assertThat(parser.parse(dateCell())).isEqualTo(LocalDateTime.parse("2021-12-12T00:00"))
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
    fun `should throw exception if failed to parse to date time`() {
        // expect
        assertThatThrownBy { parser.parse(stringCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(emptyStringCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(boolCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(nullCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(doubleCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(doubleAsStringCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(doubleInt()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(intCell()) }
            .isInstanceOf(CellParserException::class.java)
    }

}
