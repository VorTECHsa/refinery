package com.vortexa.refinery.cell.parser

import com.vortexa.refinery.exceptions.CellParserException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class IntCellParserTest : CellParserTest() {

    private val parser: CellParser<Int> = IntCellParser()

    @Test
    fun `should parse to int`() {
        // expect
        assertThat(parser.parse(intCell())).isEqualTo(1)
        assertThat(parser.parse(doubleInt())).isEqualTo(3)
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
    fun `should throw exception if failed to parse to int`() {
        // expect
        assertThatThrownBy { parser.parse(stringCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(emptyStringCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(doubleCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(doubleAsStringCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(dateCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(boolCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(nullCell()) }
            .isInstanceOf(CellParserException::class.java)
    }

    @Test
    fun `should parse to int from formula`() {
        // expect
        assertThat(parser.parse(intCellFromFormula())).isEqualTo(1)
        assertThat(parser.parse(doubleIntFromFormula())).isEqualTo(3)
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
