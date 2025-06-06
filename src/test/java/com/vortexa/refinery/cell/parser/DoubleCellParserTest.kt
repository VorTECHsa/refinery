package com.vortexa.refinery.cell.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DoubleCellParserTest : CellParserTest() {

    private val parser: CellParser<Double> = DoubleCellParser()

    @Test
    fun `should parse to double`() {
        // expect
        assertThat(parser.tryParse(doubleCell())).isEqualTo(3.1415)
        assertThat(parser.tryParse(intCell())).isEqualTo(1.0)
        assertThat(parser.tryParse(doubleAsStringCell())).isEqualTo(2.78)
        assertThat(parser.tryParse(doubleInt())).isEqualTo(3.0)
    }

    @Test
    fun `should try to parse to double or return null if failed to do that`() {
        // expect
        assertThat(parser.tryParse(stringCell())).isNull()
        assertThat(parser.tryParse(emptyStringCell())).isNull()
        assertThat(parser.tryParse(doubleCell())).isEqualTo(3.1415)
        assertThat(parser.tryParse(intCell())).isEqualTo(1.0)
        assertThat(parser.tryParse(dateCell())).isNull()
        assertThat(parser.tryParse(boolCell())).isNull()
        assertThat(parser.tryParse(nullCell())).isNull()
        assertThat(parser.tryParse(doubleAsStringCell())).isEqualTo(2.78)
        assertThat(parser.tryParse(doubleInt())).isEqualTo(3.0)
    }

    @Test
    fun `should parse to double from formula`() {
        // expect
        assertThat(parser.tryParse(doubleCellFromFormula())).isEqualTo(3.1415)
        assertThat(parser.tryParse(intCellFromFormula())).isEqualTo(1.0)
        assertThat(parser.tryParse(doubleIntFromFormula())).isEqualTo(3.0)
    }

    @Test
    fun `should try to parse to double or return null if failed to do that from formula`() {
        // expect
        assertThat(parser.tryParse(stringCellFromFormula())).isNull()
        assertThat(parser.tryParse(emptyStringCellFromFormula())).isNull()
        assertThat(parser.tryParse(doubleCellFromFormula())).isEqualTo(3.1415)
        assertThat(parser.tryParse(intCellFromFormula())).isEqualTo(1.0)
        assertThat(parser.tryParse(dateCellFromFormula())).isNull()
        assertThat(parser.tryParse(boolCellFromFormula())).isNull()
        assertThat(parser.tryParse(doubleAsStringCellFromFormula())).isNull() // Cannot parse double formula as string
        assertThat(parser.tryParse(doubleIntFromFormula())).isEqualTo(3.0)
    }
}
