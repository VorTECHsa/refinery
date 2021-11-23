package com.vortexa.refinery.dsl

import com.vortexa.refinery.cell.HeaderCell
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.usermodel.CellType.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.io.File

@TestInstance(PER_CLASS)
open class HeaderCellTest {

    private lateinit var workbook: Workbook
    private lateinit var sheet: Sheet

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

    @Test
    fun crap() {
        val headerRow = sheet.getHeaderRow()

        val simple1 = HeaderCell("simpleHeader1")
        val simple2 = HeaderCell("simpleHeader2")
        val merged1 = HeaderCell("mergedHeader1")
        val merged2 = HeaderCell("mergedHeader2")
        val regex1 = HeaderCell("regexHeader1")
        val regex2 = HeaderCell("regexHeader2")

        val tableParserDefinition = TableParserDefinition (
            requiredColumns = setOf(simple1,simple2,merged1,merged2,regex1,regex2)
        )

        val result = tableParserDefinition.resolveHeaderCellIndex(headerRow)

        assert(true)

    }

    private fun Sheet.getHeaderRow(): Row {
        return this.getRow(1)
    }
}
