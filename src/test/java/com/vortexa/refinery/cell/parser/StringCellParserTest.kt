package com.vortexa.refinery.cell.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StringCellParserTest : CellParserTest() {

    private val parser: CellParser<String> = StringCellParser()

    @Test
    fun `should parse to string`() {
        // expect
        assertThat(parser.tryParse(stringCell())).isEqualTo("example")
        assertThat(parser.tryParse(doubleCell())).isEqualTo("3.1415")
        assertThat(parser.tryParse(intCell())).isEqualTo("1.0")
        assertThat(parser.tryParse(dateCell())).isEqualTo("12-Dec-2021")
        assertThat(parser.tryParse(boolCell())).isEqualTo("TRUE")
        assertThat(parser.tryParse(doubleAsStringCell())).isEqualTo("2.78")
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
    fun `should parse to string from formula`() {
        // expect
        assertThat(parser.tryParse(stringCellFromFormula())).isEqualTo("example")
        assertThat(parser.tryParse(doubleAsStringCellFromFormula())).isEqualTo("2.78")
        assertThat(parser.tryParse(dateStrFromFormula())).isEqualTo("15-11-2021")
        assertThat(parser.tryParse(dateTimeStrFromFormula())).isEqualTo("15-11-2021 @ 15:43")
    }

    @Test
    fun `should try to parse to string or return null if failed to do that from formula`() {
        // expect
        assertThat(parser.tryParse(stringCellFromFormula())).isEqualTo("example")
        assertThat(parser.tryParse(doubleAsStringCellFromFormula())).isEqualTo("2.78")
        assertThat(parser.tryParse(dateStrFromFormula())).isEqualTo("15-11-2021")
        assertThat(parser.tryParse(dateTimeStrFromFormula())).isEqualTo("15-11-2021 @ 15:43")
        assertThat(parser.tryParse(emptyStringCellFromFormula())).isNull()
    }
}
