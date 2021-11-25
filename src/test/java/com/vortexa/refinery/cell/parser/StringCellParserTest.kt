package com.vortexa.refinery.cell.parser

import com.vortexa.refinery.exceptions.CellParserException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class StringCellParserTest : CellParserTest() {

    private val parser: CellParser<String> = StringCellParser()

    @Test
    fun `should parse to string`() {
        // expect
        assertThat(parser.parse(stringCell())).isEqualTo("example")
        assertThat(parser.parse(doubleCell())).isEqualTo("3.1415")
        assertThat(parser.parse(intCell())).isEqualTo("1.0")
        assertThat(parser.parse(dateCell())).isEqualTo("12-Dec-2021")
        assertThat(parser.parse(boolCell())).isEqualTo("TRUE")
        assertThat(parser.parse(doubleAsStringCell())).isEqualTo("2.78")
    }

    @Test
    fun `should try to parse to string or return null if failed to do that`() {
        // expect
        assertThat(parser.tryParse(stringCell())).isEqualTo("example")
        assertThat(parser.tryParse(emptyStringCell())).isNull()
        assertThat(parser.tryParse(doubleCell())).isEqualTo("3.1415")
        assertThat(parser.tryParse(intCell())).isEqualTo("1.0")
        assertThat(parser.tryParse(dateCell())).isEqualTo("12-Dec-2021")
        assertThat(parser.tryParse(boolCell())).isEqualTo("TRUE")
        assertThat(parser.tryParse(nullCell())).isNull()
        assertThat(parser.tryParse(doubleAsStringCell())).isEqualTo("2.78")
    }

    @Test
    fun `should throw exception if failed to parse to string`() {
        // expect
        assertThatThrownBy { parser.parse(emptyStringCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(nullCell()) }
            .isInstanceOf(CellParserException::class.java)
    }
}
