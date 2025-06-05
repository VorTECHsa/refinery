package com.vortexa.refinery.cell.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IntCellParserTest : CellParserTest() {

    private val parser: CellParser<Int> = IntCellParser()

    @Test
    fun `should parse to int`() {
        // expect
        assertThat(parser.tryParse(intCell())).isEqualTo(1)
        assertThat(parser.tryParse(doubleInt())).isEqualTo(3)
    }

    @Test
    fun `should try to parse to int or return null if failed to do that`() {
        // expect
        assertThat(parser.tryParse(stringCell())).isNull()
        assertThat(parser.tryParse(emptyStringCell())).isNull()
        assertThat(parser.tryParse(doubleCell())).isNull()
        assertThat(parser.tryParse(intCell())).isEqualTo(1)
        assertThat(parser.tryParse(dateCell())).isNull()
        assertThat(parser.tryParse(boolCell())).isNull()
        assertThat(parser.tryParse(nullCell())).isNull()
        assertThat(parser.tryParse(doubleAsStringCell())).isNull()
        assertThat(parser.tryParse(doubleInt())).isEqualTo(3)
    }

    @Test
    fun `should parse to int from formula`() {
        // expect
        assertThat(parser.tryParse(intCellFromFormula())).isEqualTo(1)
        assertThat(parser.tryParse(doubleIntFromFormula())).isEqualTo(3)
    }

    @Test
    fun `should try to parse to int or return null if failed to do that from formula`() {
        // expect
        assertThat(parser.tryParse(stringCellFromFormula())).isNull()
        assertThat(parser.tryParse(emptyStringCellFromFormula())).isNull()
        assertThat(parser.tryParse(doubleCellFromFormula())).isNull()
        assertThat(parser.tryParse(intCellFromFormula())).isEqualTo(1)
        assertThat(parser.tryParse(dateCellFromFormula())).isNull()
        assertThat(parser.tryParse(boolCellFromFormula())).isNull()
        assertThat(parser.tryParse(doubleAsStringCellFromFormula())).isNull()
        assertThat(parser.tryParse(doubleIntFromFormula())).isEqualTo(3)
    }
}
