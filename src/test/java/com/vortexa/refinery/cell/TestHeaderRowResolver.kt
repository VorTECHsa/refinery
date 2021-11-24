package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.io.File

@TestInstance(PER_CLASS)
open class TestHeaderRowResolver {

    private lateinit var workbook: Workbook
    private lateinit var sheet: Sheet

    private val headerRowResolver = HeaderRowResolver()

    @BeforeAll
    fun setUp() {
        val file = File(
            javaClass.classLoader.getResource("spreadsheet_examples/header_cells.xlsx")!!.file
        )

        workbook = WorkbookFactory.create(file)
        sheet = workbook.getSheet("header_cells")
    }

    @AfterAll
    internal fun tearDown() {
        workbook.close()
    }

    private fun Sheet.getHeaderRow(): Row {
        return this.getRow(0)
    }

    @Test
    fun `test string cell parser with exact matching`() {
        // given
        val headerRow = sheet.getHeaderRow()

        val simple1 = SimpleHeaderCell("simpleHeader1")
        val simple2 = SimpleHeaderCell("simpleHeader2")
        val merged1 = SimpleHeaderCell("mergedHeader1")
        val merged2 = SimpleHeaderCell("mergedHeader2")
        val regex1 = SimpleHeaderCell("regexHeader")
        val regex2 = SimpleHeaderCell("regexHeader2")

        // when
        val result = headerRowResolver.resolveHeaderCellIndex(headerRow,
            setOf(simple1, simple2, merged1, merged2, regex1, regex2))

        // then
        assertThat(result).containsExactlyEntriesOf(
            mapOf(simple1 to 0,
                simple2 to 1,
                merged1 to 2,
                merged2 to 4,
                regex1 to 6,
                regex2 to 7)
        )

    }

    @Test
    fun `test regex cell parser`() {
        // given
        val headerRow = sheet.getHeaderRow()

        val regex1 = RegexHeaderCell("regexHeader$")
        val regex2 = RegexHeaderCell("regexHeader2")

        // when
        val result = headerRowResolver.resolveHeaderCellIndex(headerRow, setOf(regex1, regex2))
        // then
        assertThat(result).containsExactlyEntriesOf(
            mapOf(regex1 to 6,
                regex2 to 7)
        )

    }

    @Test
    fun `test merged cell parser`() {
        // given
        val headerRow = sheet.getHeaderRow()

        val m1 = SimpleHeaderCell("merged1")
        val m2 = SimpleHeaderCell("merged2")
        val m3 = SimpleHeaderCell("merged3")
        val m4 = SimpleHeaderCell("merged4")


        val merged1 = MergedHeaderCell(StringHeaderCell("mergedHeader1"), listOf(m1, m2))
        val merged2 = MergedHeaderCell(StringHeaderCell("mergedHeader2"), listOf(m3, m4))

        // when
        val result = headerRowResolver.resolveHeaderCellIndex(headerRow, setOf(merged1, merged2))
        // then
        assertThat(result).containsExactlyEntriesOf(
            mapOf(m1 to 2,
                m2 to 3,
                m3 to 4,
                m4 to 5)
        )
    }

    @Test
    fun `test ordered cell parser`() {
        // given
        val headerRow = sheet.getHeaderRow()

        val simple1 = StringHeaderCell("header")
        val simple2 = StringHeaderCell("header")
        val simple3 = StringHeaderCell("header")
        val simple4 = StringHeaderCell("header")
        val simple5 = StringHeaderCell("header")
        val simple6 = StringHeaderCell("header")


        val ordered1 = OrderedHeaderCell(simple1, 6)
        val ordered2 = OrderedHeaderCell(simple2, 5)
        val ordered3 = OrderedHeaderCell(simple3, 4)
        val ordered4 = OrderedHeaderCell(simple4, 3)
        val ordered5 = OrderedHeaderCell(simple5, 2)
        val ordered6 = OrderedHeaderCell(simple6, 1)

        // when
        val result = headerRowResolver.resolveHeaderCellIndex(headerRow,
            setOf(ordered5, ordered3, ordered2, ordered4, ordered6, ordered1))
        // then
        assertThat(result).containsExactlyEntriesOf(
            mapOf(simple6 to 0,
                simple5 to 1,
                simple4 to 2,
                simple3 to 4,
                simple2 to 6,
                simple1 to 7)
        )
    }

    @Test
    fun `test ordered merged cell parser`() {
        // given
        val headerRow = sheet.getHeaderRow()

        val m1 = SimpleHeaderCell("merged1")
        val m2 = SimpleHeaderCell("merged2")
        val m3 = SimpleHeaderCell("merged3")
        val m4 = SimpleHeaderCell("merged4")


        val merged1 = MergedHeaderCell(StringHeaderCell("merged"), listOf(m1, m2))
        val merged2 = MergedHeaderCell(StringHeaderCell("merged"), listOf(m3, m4))
        val ordered1 = OrderedHeaderCell(merged1, 1)
        val ordered2 = OrderedHeaderCell(merged2, 2)

        // when
        val result = headerRowResolver.resolveHeaderCellIndex(headerRow, setOf(ordered1, ordered2))
        // then
        assertThat(result).containsExactlyEntriesOf(
            mapOf(m1 to 2,
                m2 to 3,
                m3 to 4,
                m4 to 5)
        )
    }

    @Test
    fun `test merged ordered cell throws exception`() {
        assertThatThrownBy { MergedHeaderCell(OrderedHeaderCell(SimpleHeaderCell(""), 1), listOf()) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

}
