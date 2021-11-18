package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType.*
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.io.File

@TestInstance(PER_CLASS)
open class CellParserTest {

    private lateinit var workbook: Workbook
    private lateinit var sheet: Sheet

    @BeforeAll
    fun setUp() {
        val file = File(
            javaClass.classLoader.getResource("spreadsheet_examples/cell_parsers.xlsx")!!.file
        )

        workbook = WorkbookFactory.create(file)
        sheet = workbook.getSheet("cell_parsers")
    }

    @AfterAll
    internal fun tearDown() {
        workbook.close()
    }

    protected fun stringCell(): Cell {
        val cell = sheet[1, 0]!!
        assertThat(cell.cellType).isEqualTo(STRING)
        return cell
    }

    protected fun emptyStringCell(): Cell {
        val cell = sheet[1, 1]!!
        assertThat(cell.cellType).isEqualTo(STRING)
        return cell
    }

    protected fun doubleCell(): Cell {
        val cell = sheet[1, 2]!!
        assertThat(cell.cellType).isEqualTo(NUMERIC)
        return cell
    }

    protected fun intCell(): Cell {
        val cell = sheet[1, 3]!!
        assertThat(cell.cellType).isEqualTo(NUMERIC)
        return cell
    }

    protected fun dateCell(): Cell {
        val cell = sheet[1, 4]!!
        assertThat(cell.cellType).isEqualTo(NUMERIC)
        return cell
    }

    protected fun boolCell(): Cell {
        val cell = sheet[1, 5]!!
        assertThat(cell.cellType).isEqualTo(BOOLEAN)
        return cell
    }

    protected fun nullCell(): Cell? {
        val cell = sheet[1, 6]
        assertThat(cell).isNull()
        return cell
    }

    protected fun doubleAsStringCell(): Cell {
        val cell = sheet[1, 7]!!
        assertThat(cell.cellType).isEqualTo(STRING)
        return cell
    }

    protected fun doubleInt(): Cell {
        val cell = sheet[1, 8]!!
        assertThat(cell.cellType).isEqualTo(NUMERIC)
        return cell
    }

    protected fun dateTime(): Cell {
        val cell = sheet[1, 9]!!
        assertThat(cell.cellType).isEqualTo(NUMERIC)
        return cell
    }

    protected fun dateStr(): Cell {
        val cell = sheet[1, 10]!!
        assertThat(cell.cellType).isEqualTo(STRING)
        return cell
    }

    protected fun dateTimeStr(): Cell {
        val cell = sheet[1, 11]!!
        assertThat(cell.cellType).isEqualTo(STRING)
        return cell
    }

    private operator fun Sheet.get(rowIndex: Int, columnIndex: Int): Cell? {
        return this.getRow(rowIndex).getCell(columnIndex)
    }
}
