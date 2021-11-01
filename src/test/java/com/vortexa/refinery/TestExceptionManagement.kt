package com.vortexa.refinery

import com.vortexa.refinery.cell.HeaderCell
import com.vortexa.refinery.configuration.*
import com.vortexa.refinery.dsl.SheetParserDefinition
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.dsl.WorkbookParserDefinition
import com.vortexa.refinery.exceptions.*
import com.vortexa.refinery.result.ParsedRecord
import com.vortexa.refinery.result.RowParserData
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class TestExceptionManagement {

    @Test
    fun `test cell parsing raises exception when required cell is empty`() {
        // given
        class RequiredCellsRowParser(rowParserData: RowParserData,
                                     exceptionManager: ExceptionManager) : RowParser(rowParserData, exceptionManager) {
            override fun toRecord(row: Row): ParsedRecord {
                return object : ParsedRecord() {
                    val one = parseRequiredFieldAsString(row, string)
                    val two = parseRequiredFieldAsString(row, number)
                    val three = parseRequiredFieldAsString(row, date)
                    val four = parseRequiredFieldAsString(row, optionalString)
                }
            }
        }

        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { true },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            setOf(
                                string,
                                number,
                                date,
                                optionalString,
                            ),
                            setOf(),
                            ::RequiredCellsRowParser
                        )
                    )
                )
            )
        )

        val file = File(
            javaClass.classLoader.getResource("spreadsheet_examples/test_spreadsheet.xlsx")!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)
        val exceptionManager = ExceptionManager()

        // when
        val records = WorkbookParser(definition, workbook, exceptionManager).parse()

        // then
        assertThat(exceptionManager.exceptions).hasSize(2)
        assertThat(exceptionManager.containsCritical()).isFalse
        exceptionManager.exceptions.forEach { exceptionData ->
            assertThat(exceptionData).satisfies { it.exception is CellParserException }
        }
        assertThat(records).isNotEmpty
    }

    @Test
    fun `test badly defined table parser cannot find tables`() {
        // given
        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { true },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            setOf(),
                            setOf(),
                            ::GenericRowParser,
                            anchor = "Bad Anchor"
                        )
                    )
                )
            )
        )

        val file = File(
            javaClass.classLoader.getResource("spreadsheet_examples/test_spreadsheet.xlsx")!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)
        val exceptionManager = ExceptionManager()

        // when
        WorkbookParser(definition, workbook, exceptionManager).parse()

        // then
        assertThat(exceptionManager.exceptions).hasSize(1)
        exceptionManager.exceptions.forEach { exceptionData ->
            assertThat(exceptionData).satisfies { it.exception is SheetParserException }
        }

    }

    @Test
    fun `test unexpected exception results in critical failure`() {
        // given
        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { throw NullPointerException() },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            setOf(),
                            setOf(),
                            ::GenericRowParser,
                        )
                    )
                )
            )
        )

        val file = File(
            javaClass.classLoader.getResource("spreadsheet_examples/test_spreadsheet.xlsx")!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)
        val exceptionManager = ExceptionManager()

        // when
        WorkbookParser(definition, workbook, exceptionManager).parse()

        // then
        assertThat(exceptionManager.exceptions).hasSize(1)
        assertTrue(exceptionManager.containsCritical())
        exceptionManager.exceptions.forEach { exceptionData ->
            assertThat(exceptionData).satisfies { it.exception is UncategorizedException }
        }
    }

    @Test
    fun `test parsing forgotten header raises an exception`() {
        // given
        val string = HeaderCell("string")
        val number = HeaderCell("number")
        val date = HeaderCell("date")
        val optionalString = HeaderCell("optional_str")

        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { true },
                    tableDefinitions = listOf(1, 2, 3).map {
                        TableParserDefinition(
                            setOf(string, number, date),
                            setOf(optionalString),
                            ::GenericRowParser,
                            anchor = "table $it"
                        )
                    }
                )
            )
        )

        val file = File(
            javaClass.classLoader.getResource(
                "spreadsheet_examples/test_spreadsheet_multitable_anchors_no_header.xlsx")!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)
        val exceptionManager = ExceptionManager()

        // when
        val records = WorkbookParser(definition, workbook, exceptionManager).parse()


        // then
        assertThat(exceptionManager.exceptions).hasSize(1)
        exceptionManager.exceptions.forEach { exceptionData ->
            assertThat(exceptionData).satisfies { it.exception is TableParserException }
        }
        assertThat(records).hasSize(6)
    }

    @Test
    fun `test uncaptured header in registered as exception`() {
        // given
        val file = File(
            javaClass.classLoader.getResource("spreadsheet_examples/test_spreadsheet_uncaptured.xlsx")!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)
        val expectedException = ExceptionManager.ExceptionData(
            UncapturedHeadersException("uncaptured @ 5"),
            ExceptionManager.Location("Sheet1", 1)
        )

        // when
        val exceptionManager = ExceptionManager()
        val parsedRecords = WorkbookParser(testDefinition, workbook, exceptionManager).parse()

        // then
        assertThat(exceptionManager.exceptions)
            .hasSize(1)
            .containsExactly(expectedException)
        // and
        assertThat(parsedRecords).hasSize(3)
    }
}
