package com.vortexa.refinery.cell.parser

import com.vortexa.refinery.exceptions.CellParserException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class DoubleCellParserTest : CellParserTest() {

    private val parser: CellParser<Double> = DoubleCellParser()

    @Test
    fun `should parse to double`() {
        // expect
        assertThat(parser.parse(doubleCell())).isEqualTo(3.1415)
        assertThat(parser.parse(intCell())).isEqualTo(1.0)
        assertThat(parser.parse(doubleAsStringCell())).isEqualTo(2.78)
        assertThat(parser.parse(doubleInt())).isEqualTo(3.0)
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
    fun `should throw exception if failed to parse to double`() {
        // expect
        assertThatThrownBy { parser.parse(stringCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(emptyStringCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(boolCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(nullCell()) }
            .isInstanceOf(CellParserException::class.java)
        assertThatThrownBy { parser.parse(dateCell()) }
            .isInstanceOf(CellParserException::class.java)
    }
}
