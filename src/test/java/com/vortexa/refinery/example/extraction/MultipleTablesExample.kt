package com.vortexa.refinery.example.extraction

import com.vortexa.refinery.WorkbookParser
import com.vortexa.refinery.cell.HeaderCell
import com.vortexa.refinery.dsl.SheetParserDefinition
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.dsl.WorkbookParserDefinition
import com.vortexa.refinery.result.GenericParsedRecord
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate

class MultipleTablesExample {

    @Test
    fun `should extract data from multiple tables within the spreadsheet`() {
        // given
        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { it == "multiple tables" },
                    tableDefinitions = listOf(
                        // 1st table
                        TableParserDefinition(
                            requiredColumns = setOf(
                                HeaderCell("team"),
                                HeaderCell("plays"),
                                HeaderCell("wins"),
                                HeaderCell("goal diff"),
                                HeaderCell("points"),
                                HeaderCell("date")
                            )
                        ),
                        // 2nd table
                        TableParserDefinition(
                            requiredColumns = setOf(
                                HeaderCell("home team"),
                                HeaderCell("guest team"),
                                HeaderCell("home score"),
                                HeaderCell("guest score"),
                                HeaderCell("date")
                            )
                        )
                    )
                )
            )
        )

        // and
        val file = File(
            javaClass.classLoader.getResource("examples/basic_examples.xlsx")!!.file
        )

        val workbook = WorkbookFactory.create(file)
        // when
        val parsedRecords = WorkbookParser(definition, workbook).parse()

        // then
        assertThat(parsedRecords)
            .hasSize(12)
            .containsExactly(
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 2,
                        "team" to "PSG",
                        "plays" to 3.0,
                        "wins" to 2.0,
                        "goal diff" to 3.0,
                        "points" to 7.0,
                        "date" to LocalDate.of(2021, 11, 2).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 3,
                        "team" to "Manchester City",
                        "plays" to 3.0,
                        "wins" to 2.0,
                        "goal diff" to 5.0,
                        "points" to 6.0,
                        "date" to LocalDate.of(2021, 11, 2).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 4,
                        "team" to "Club Brugge",
                        "plays" to 3.0,
                        "wins" to 1.0,
                        "goal diff" to -3.0,
                        "points" to 4.0,
                        "date" to LocalDate.of(2021, 11, 2).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 5,
                        "team" to "RB Leipzig",
                        "plays" to 3.0,
                        "wins" to 0.0,
                        "goal diff" to -5.0,
                        "points" to 0.0,
                        "date" to LocalDate.of(2021, 11, 2).atStartOfDay(),
                    )
                ),
                // and denormalized records from the second table
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 10,
                        "home team" to "Manchester City",
                        "guest team" to "RB Leipzig",
                        "home score" to 6.0,
                        "guest score" to 3.0,
                        "date" to LocalDate.of(2021, 9, 15).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 11,
                        "home team" to "Club Brugge",
                        "guest team" to "PSG",
                        "home score" to 1.0,
                        "guest score" to 1.0,
                        "date" to LocalDate.of(2021, 9, 15).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 12,
                        "home team" to "RB Leipzig",
                        "guest team" to "Club Brugge",
                        "home score" to 1.0,
                        "guest score" to 2.0,
                        "date" to LocalDate.of(2021, 9, 28).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 13,
                        "home team" to "PSG",
                        "guest team" to "Manchester City",
                        "home score" to 2.0,
                        "guest score" to 0.0,
                        "date" to LocalDate.of(2021, 9, 28).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 14,
                        "home team" to "Club Brugge",
                        "guest team" to "Manchester City",
                        "home score" to 1.0,
                        "guest score" to 5.0,
                        "date" to LocalDate.of(2021, 10, 19).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 15,
                        "home team" to "PSG",
                        "guest team" to "RB Leipzig",
                        "home score" to 3.0,
                        "guest score" to 2.0,
                        "date" to LocalDate.of(2021, 10, 19).atStartOfDay(),
                    )
                ),
                // and fixtures
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 16,
                        "home team" to "Manchester City",
                        "guest team" to "Club Brugge",
                        "date" to LocalDate.of(2021, 11, 3).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables",
                        "row_number" to 17,
                        "home team" to "RB Leipzig",
                        "guest team" to "PSG",
                        "date" to LocalDate.of(2021, 11, 3).atStartOfDay(),
                    )
                )
            )
    }
}
