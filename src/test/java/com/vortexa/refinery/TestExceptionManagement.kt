package com.vortexa.refinery

import com.vortexa.refinery.cell.StringHeaderCell
import com.vortexa.refinery.configuration.date
import com.vortexa.refinery.configuration.number
import com.vortexa.refinery.configuration.optionalString
import com.vortexa.refinery.configuration.string
import com.vortexa.refinery.configuration.testDefinition
import com.vortexa.refinery.dsl.SheetParserDefinition
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.dsl.WorkbookParserDefinition
import com.vortexa.refinery.exceptions.CellParserException
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.exceptions.SheetParserException
import com.vortexa.refinery.exceptions.TableParserException
import com.vortexa.refinery.exceptions.UncapturedHeadersException
import com.vortexa.refinery.exceptions.UncapturedHeadersException.UncapturedHeaderCell
import com.vortexa.refinery.exceptions.UncategorizedException
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
        class RequiredCellsRowParser(
            rowParserData: RowParserData,
            exceptionManager: ExceptionManager
        ) : RowParser(rowParserData, exceptionManager) {
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

        val fileName = "spreadsheet_examples/test_spreadsheet.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)
        val exceptionManager = ExceptionManager()

        // when
        val records = WorkbookParser(definition, workbook, exceptionManager, fileName).parse()

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

        val fileName = "spreadsheet_examples/test_spreadsheet.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)
        val exceptionManager = ExceptionManager()

        // when
        WorkbookParser(definition, workbook, exceptionManager, fileName).parse()

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

        val fileName = "spreadsheet_examples/test_spreadsheet.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)
        val exceptionManager = ExceptionManager()

        // when
        WorkbookParser(definition, workbook, exceptionManager, fileName).parse()

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
        val string = StringHeaderCell("string")
        val number = StringHeaderCell("number")
        val date = StringHeaderCell("date")
        val optionalString = StringHeaderCell("optional_str")

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

        val fileName = "spreadsheet_examples/test_spreadsheet_multitable_anchors_no_header.xlsx"
        val file = File(
            javaClass.classLoader.getResource(
                fileName
            )!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)
        val exceptionManager = ExceptionManager()

        // when
        val records = WorkbookParser(definition, workbook, exceptionManager, fileName).parse()

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
        val fileName = "spreadsheet_examples/test_spreadsheet_uncaptured.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)
        val expectedException = ExceptionManager.ExceptionData(
            UncapturedHeadersException(listOf(UncapturedHeaderCell("uncaptured", 4), UncapturedHeaderCell("double", 5))),
            ExceptionManager.Location("Sheet1", 1)
        )

        // when
        val exceptionManager = ExceptionManager()
        val parsedRecords = WorkbookParser(testDefinition, workbook, exceptionManager, fileName).parse()

        // then
        assertThat(exceptionManager.exceptions)
            .hasSize(1)
            .containsExactly(expectedException)
        // and
        assertThat(parsedRecords).hasSize(3)
    }
}
