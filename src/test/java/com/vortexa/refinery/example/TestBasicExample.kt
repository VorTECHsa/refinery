package com.vortexa.refinery.example

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

class TestBasicExample {

    @Test
    fun `should extract data from the basic example`() {
        // given
        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { it == "simple spreadsheet" },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            requiredColumns = setOf(
                                HeaderCell("team"),
                                HeaderCell("plays"),
                                HeaderCell("wins"),
                                HeaderCell("goal diff"),
                                HeaderCell("points"),
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
            .hasSize(4)
            .containsExactly(
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "simple spreadsheet",
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
                        "spreadsheet_name" to "simple spreadsheet",
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
                        "spreadsheet_name" to "simple spreadsheet",
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
                        "spreadsheet_name" to "simple spreadsheet",
                        "row_number" to 5,
                        "team" to "RB Leipzig",
                        "plays" to 3.0,
                        "wins" to 0.0,
                        "goal diff" to -5.0,
                        "points" to 0.0,
                        "date" to LocalDate.of(2021, 11, 2).atStartOfDay(),
                    )
                ),
            )
    }
}
