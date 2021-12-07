package com.vortexa.refinery.example.extraction

import com.vortexa.refinery.WorkbookParser
import com.vortexa.refinery.cell.StringHeaderCell
import com.vortexa.refinery.dsl.SheetParserDefinition
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.dsl.WorkbookParserDefinition
import com.vortexa.refinery.result.GenericParsedRecord
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate

class MergedCellsHeaderExample {

    @Test
    fun `should extract the data from all the sub-cells of the merged header`() {
        // given
        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { it == "merged cells header" },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            requiredColumns = setOf(
                                StringHeaderCell("home team"),
                                StringHeaderCell("guest team"),
                                StringHeaderCell("score"),
                                StringHeaderCell("date")
                            )
                        )
                    )
                )
            )
        )

        // and
        val fileName = "examples/basic_examples.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )

        // when
        val parsedRecords =
            WorkbookFactory.create(file).use { WorkbookParser(definition, it, workbookName = fileName).parse() }

        // then
        assertThat(parsedRecords)
            .hasSize(6)
            .containsExactly(
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "merged cells header",
                        "row_number" to 2,
                        "home team" to "Manchester City",
                        "guest team" to "RB Leipzig",
                        "score_3" to 6,
                        "score_4" to 3,
                        "date" to LocalDate.of(2021, 9, 15).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "merged cells header",
                        "row_number" to 3,
                        "home team" to "Club Brugge",
                        "guest team" to "PSG",
                        "score_3" to 1,
                        "score_4" to 1,
                        "date" to LocalDate.of(2021, 9, 15).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "merged cells header",
                        "row_number" to 4,
                        "home team" to "RB Leipzig",
                        "guest team" to "Club Brugge",
                        "score_3" to 1,
                        "score_4" to 2,
                        "date" to LocalDate.of(2021, 9, 28).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "merged cells header",
                        "row_number" to 5,
                        "home team" to "PSG",
                        "guest team" to "Manchester City",
                        "score_3" to 2,
                        "score_4" to 0,
                        "date" to LocalDate.of(2021, 9, 28).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "merged cells header",
                        "row_number" to 6,
                        "home team" to "Club Brugge",
                        "guest team" to "Manchester City",
                        "score_3" to 1,
                        "score_4" to 5,
                        "date" to LocalDate.of(2021, 10, 19).atStartOfDay(),
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "merged cells header",
                        "row_number" to 7,
                        "home team" to "PSG",
                        "guest team" to "RB Leipzig",
                        "score_3" to 3,
                        "score_4" to 2,
                        "date" to LocalDate.of(2021, 10, 19).atStartOfDay(),
                    )
                )
            )
    }
}
