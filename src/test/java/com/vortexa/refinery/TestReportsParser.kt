package com.vortexa.refinery

import com.vortexa.refinery.cell.MergedHeaderCell
import com.vortexa.refinery.cell.StringHeaderCell
import com.vortexa.refinery.configuration.date
import com.vortexa.refinery.configuration.number
import com.vortexa.refinery.configuration.optionalString
import com.vortexa.refinery.configuration.string
import com.vortexa.refinery.dsl.SheetParserDefinition
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.dsl.WorkbookParserDefinition
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.result.GenericParsedRecord
import com.vortexa.refinery.result.ParsedRecord
import com.vortexa.refinery.result.RowParserData
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDateTime

class TestReportsParser {

    @Test
    fun `test parsing multiple tables with anchors`() {
        // given
        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { true },
                    tableDefinitions = listOf(1, 2, 3).map {
                        TableParserDefinition(
                            setOf(string, number, date),
                            setOf(optionalString),
                            anchor = "table $it"
                        )
                    }
                )
            )
        )

        val fileName = "spreadsheet_examples/test_spreadsheet_multitable_anchors.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )

        val exceptionManager = ExceptionManager()

        // when
        val records = WorkbookFactory.create(file).use {
            WorkbookParser(definition, it, exceptionManager, fileName).parse()
        }
        // then
        assertThat(exceptionManager.exceptions).isEmpty()
        assertThat(records).hasSize(9)
            .contains(
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "one",
                        "number" to 1,
                        "date" to LocalDateTime.of(2021, 1, 1, 0, 0),
                        "optional_str" to "exist",
                        "anchor" to "table 1",
                        "row_number" to 3
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "four",
                        "number" to 4,
                        "date" to LocalDateTime.of(2021, 1, 4, 0, 0),
                        "anchor" to "table 2",
                        "row_number" to 10
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "three",
                        "number" to 3,
                        "date" to LocalDateTime.of(2021, 1, 3, 0, 0),
                        "anchor" to "table 3",
                        "row_number" to 20
                    )
                )
            )
    }

    @Test
    fun `test parsing multiple tables without anchors and with bad formatting`() {
        // given
        val string = StringHeaderCell("string")
        val number = StringHeaderCell("number")
        val date = StringHeaderCell("date")
        val optionalString = StringHeaderCell("optional_str")

        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { true },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            setOf(string, number, date),
                            setOf(optionalString),
                            ::GenericRowParser,
                        )
                    )
                )
            )
        )

        val fileName = "spreadsheet_examples/test_spreadsheet_multitable_unaligned.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )
        val exceptionManager = ExceptionManager()

        // when
        val records = WorkbookFactory.create(file).use {
            WorkbookParser(definition, it, exceptionManager, fileName).parse()
        }

        // then
        assertThat(exceptionManager.exceptions).isEmpty()
        assertThat(records).hasSize(9)
            .contains(
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "one",
                        "number" to 1,
                        "date" to LocalDateTime.of(2021, 1, 1, 0, 0),
                        "optional_str" to "exist",
                        "row_number" to 3
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "four",
                        "number" to 4,
                        "date" to LocalDateTime.of(2021, 1, 4, 0, 0),
                        "row_number" to 10
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "three",
                        "number" to 3,
                        "date" to LocalDateTime.of(2021, 1, 3, 0, 0),
                        "row_number" to 20
                    )
                )
            )
    }

    @Test
    fun `test parsing multiple sheets with a sheet filter`() {
        // given
        val string = StringHeaderCell("string")
        val number = StringHeaderCell("number")
        val date = StringHeaderCell("date")
        val optionalString = StringHeaderCell("optional_str")

        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { it != "Sheet3" },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            setOf(string, number, date),
                            setOf(optionalString),
                            ::GenericRowParser,
                        )
                    )
                )
            )
        )

        val fileName = "spreadsheet_examples/test_spreadsheet_multisheet.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )
        val exceptionManager = ExceptionManager()

        // when
        val records = WorkbookFactory.create(file).use {
            WorkbookParser(definition, it, exceptionManager, fileName).parse()
        }

        // then
        assertThat(exceptionManager.exceptions).isEmpty()
        assertThat(records).hasSize(6)
            .contains(
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "one",
                        "number" to 1,
                        "date" to LocalDateTime.of(2021, 1, 1, 0, 0),
                        "optional_str" to "exist",
                        "row_number" to 2
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet2",
                        "string" to "four",
                        "number" to 4,
                        "date" to LocalDateTime.of(2021, 1, 4, 0, 0),
                        "optional_str" to "exist",
                        "row_number" to 2
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet2",
                        "string" to "six",
                        "number" to 6,
                        "date" to LocalDateTime.of(2021, 1, 6, 0, 0),
                        "row_number" to 4
                    )
                )
            )
    }

    @Test
    fun `test parsing merged rows and columns`() {
        // given
        val string = StringHeaderCell("string")
        val number = StringHeaderCell("number")
        val date = StringHeaderCell("date")
        val optionalString = StringHeaderCell("optional_str")
        val optionalString2 = StringHeaderCell("optional_str2")
        val mergedCols = MergedHeaderCell(
            StringHeaderCell("optional_str"),
            listOf(optionalString, optionalString2)
        )

        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { it == "Sheet1" },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            setOf(string, number, date),
                            setOf(mergedCols),
                            ::GenericRowParser,
                        )
                    )
                )
            )
        )

        val fileName = "spreadsheet_examples/test_spreadsheet_merged_cells.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )
        val exceptionManager = ExceptionManager()

        // when
        val records = WorkbookFactory.create(file).use {
            WorkbookParser(definition, it, exceptionManager, fileName).parse()
        }

        // then
        assertTrue(exceptionManager.exceptions.isEmpty())
        assertThat(records).hasSize(5)
            .contains(
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "one",
                        "number" to 1,
                        "date" to LocalDateTime.of(2021, 1, 1, 0, 0),
                        "optional_str_4" to "exist",
                        "optional_str_5" to "exist2",
                        "row_number" to 2
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "two",
                        "number" to 2,
                        "date" to LocalDateTime.of(2021, 1, 2, 0, 0),
                        "optional_str_4" to "exist",
                        "optional_str_5" to "exist2",
                        "row_number" to 3
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "three",
                        "number" to 3,
                        "date" to LocalDateTime.of(2021, 1, 3, 0, 0),
                        "row_number" to 4
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "four and five",
                        "number" to 4,
                        "date" to LocalDateTime.of(2021, 1, 4, 0, 0),
                        "optional_str_4" to "same",
                        "optional_str_5" to "same",
                        "row_number" to 5
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "four and five",
                        "number" to 5,
                        "date" to LocalDateTime.of(2021, 1, 5, 0, 0),
                        "row_number" to 6
                    )
                )
            )
    }

    @Test
    fun `test parsing merged row header`() {
        // given
        val string = StringHeaderCell("string")
        val number = StringHeaderCell("number")
        val date = StringHeaderCell("date")
        val optionalString = StringHeaderCell("opt1")
        val optionalString2 = StringHeaderCell("opt2")

        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { it == "Sheet2" },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            setOf(string, number, date, optionalString, optionalString2),
                            setOf(),
                            ::GenericRowParser,
                        )
                    )
                )
            )
        )

        val fileName = "spreadsheet_examples/test_spreadsheet_merged_cells.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )
        val exceptionManager = ExceptionManager()

        // when
        val records = WorkbookFactory.create(file).use {
            WorkbookParser(definition, it, exceptionManager, fileName).parse()
        }

        // then
        assertTrue(exceptionManager.exceptions.isEmpty())
        assertThat(records).hasSize(5)
            .contains(
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet2",
                        "string_1" to "one",
                        "number_2" to 1,
                        "date_3" to LocalDateTime.of(2021, 1, 1, 0, 0),
                        "opt1" to "exist",
                        "opt2" to "exist2",
                        "row_number" to 3
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet2",
                        "string_1" to "two",
                        "number_2" to 2,
                        "date_3" to LocalDateTime.of(2021, 1, 2, 0, 0),
                        "opt1" to "exist",
                        "opt2" to "exist2",
                        "row_number" to 4
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet2",
                        "string_1" to "three",
                        "number_2" to 3,
                        "date_3" to LocalDateTime.of(2021, 1, 3, 0, 0),
                        "row_number" to 5
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet2",
                        "string_1" to "four and five",
                        "number_2" to 4,
                        "date_3" to LocalDateTime.of(2021, 1, 4, 0, 0),
                        "opt1" to "same",
                        "opt2" to "same",
                        "row_number" to 6
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet2",
                        "string_1" to "four and five",
                        "number_2" to 5,
                        "date_3" to LocalDateTime.of(2021, 1, 5, 0, 0),
                        "row_number" to 7
                    )
                )
            )
    }

    @Test
    fun `test parsing non string rows and formulas`() {
        // given
        val number1 = StringHeaderCell("number1")
        val number2 = StringHeaderCell("number2")
        val date = StringHeaderCell("date")
        val formula = StringHeaderCell("formula")

        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { it != "Sheet3" },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            setOf(number1, number2, date, formula),
                            setOf(),
                            ::GenericRowParser,
                        )
                    )
                )
            )
        )

        val fileName = "spreadsheet_examples/test_spreadsheet_numbers_and_formulas.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )
        val exceptionManager = ExceptionManager()

        // when
        val records = WorkbookFactory.create(file).use {
            WorkbookParser(definition, it, exceptionManager, fileName).parse()
        }

        // then
        assertThat(exceptionManager.exceptions).isEmpty()
        assertThat(records).hasSize(2)
            .contains(
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "number1" to 1,
                        "number2" to 1,
                        "date" to LocalDateTime.of(2021, 1, 1, 0, 0),
                        "formula" to 2,
                        "row_number" to 3
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "number1" to 1,
                        "number2" to 2,
                        "date" to LocalDateTime.of(2021, 1, 2, 0, 0),
                        "formula" to LocalDateTime.of(2021, 1, 3, 0, 0),
                        "row_number" to 4
                    )
                )
            )
    }

    @Test
    fun `test grouping of rows`() {
        // given
        class RequiredCellsRowParser(
            rowParserData: RowParserData,
            exceptionManager: ExceptionManager
        ) : RowParser(rowParserData, exceptionManager) {
            override fun toRecord(row: Row): ParsedRecord {
                return object : ParsedRecord() {}
            }

            override fun shouldGroupRows(current: ParsedRecord, previous: ParsedRecord): Boolean {
                return true
            }

            override fun extractDataFromPreviousRecord(
                current: ParsedRecord,
                previous: ParsedRecord
            ): ParsedRecord {
                return object : ParsedRecord() {}
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
                            ),
                            setOf(optionalString),
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

        val exceptionManager = ExceptionManager()

        // when
        val records =
            WorkbookFactory.create(file)
                .use { WorkbookParser(definition, it, exceptionManager, fileName).parse() }
        // then
        assertThat(records.map { it.groupId }).containsOnly(records.first().groupId!!)
    }

    @Test
    fun `test skipping the rows`() {
        // given
        val fileName = "spreadsheet_examples/test_spreadsheet.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )

        class SkippingGenericRowParser(rdp: RowParserData, em: ExceptionManager) :
            GenericRowParser(rdp, em) {
            override fun skip(row: Row): Boolean {
                val optionalStr = parseOptionalFieldAsString(row, optionalString)
                return optionalStr != "exist"
            }
        }

        val testDefinition = WorkbookParserDefinition(
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
                            ::SkippingGenericRowParser
                        )
                    )
                )
            )
        )

        // when
        val exceptionManager = ExceptionManager()
        val parsedRecords = WorkbookFactory.create(file)
            .use { WorkbookParser(testDefinition, it, exceptionManager, fileName).parse() }

        // then
        assertThat(parsedRecords)
            .hasSize(1)
            .containsExactly(
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "one",
                        "number" to 1,
                        "date" to LocalDateTime.of(2021, 1, 1, 0, 0),
                        "optional_str" to "exist",
                        "row_number" to 2,
                    )
                )
            )
    }

    @Test
    fun `test ignore the columns`() {
        // given
        val fileName = "spreadsheet_examples/test_spreadsheet.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )

        val testDefinition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { true },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            requiredColumns = setOf(
                                string,
                                number,
                            ),
                            ignoredColumns = setOf(optionalString, date)
                        )
                    )
                )
            )
        )

        // when
        val exceptionManager = ExceptionManager()
        val parsedRecords = WorkbookFactory.create(file)
            .use { WorkbookParser(testDefinition, it, exceptionManager, fileName).parse() }

        // then
        assertThat(parsedRecords)
            .hasSize(3)
            .containsExactly(
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "one",
                        "number" to 1,
                        "row_number" to 2,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "two",
                        "number" to 2,
                        "row_number" to 3,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "three",
                        "number" to 3,
                        "row_number" to 4,
                    )
                )
            )
    }
}
