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

class TestMultipleTablesWithAnchorExample {

    @Test
    fun `should extract data from multiple tables with the same headers but different anchors`() {
        // given
        val headerColumns = setOf(
            HeaderCell("team"),
            HeaderCell("plays"),
            HeaderCell("points")
        )
        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { it == "multiple tables with anchors" },
                    tableDefinitions = listOf(
                        // 1st table
                        TableParserDefinition(
                            requiredColumns = headerColumns,
                            anchor = "Group A"
                        ),
                        // 2nd table
                        TableParserDefinition(
                            requiredColumns = headerColumns,
                            anchor = "Group B"
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
            .hasSize(8)
            .containsExactly(
                // data from table 1
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables with anchors",
                        "row_number" to 3,
                        "anchor" to "Group A",
                        "team" to "PSG",
                        "plays" to 3.0,
                        "points" to 7.0,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables with anchors",
                        "row_number" to 4,
                        "anchor" to "Group A",
                        "team" to "Manchester City",
                        "plays" to 3.0,
                        "points" to 6.0,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables with anchors",
                        "row_number" to 5,
                        "anchor" to "Group A",
                        "team" to "Club Brugge",
                        "plays" to 3.0,
                        "points" to 4.0,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables with anchors",
                        "row_number" to 6,
                        "anchor" to "Group A",
                        "team" to "RB Leipzig",
                        "plays" to 3.0,
                        "points" to 0.0,
                    )
                ),
                // data from table 2
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables with anchors",
                        "row_number" to 11,
                        "anchor" to "Group B",
                        "team" to "Liverpool",
                        "plays" to 3.0,
                        "points" to 9.0,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables with anchors",
                        "row_number" to 12,
                        "anchor" to "Group B",
                        "team" to "Atletico Madrid",
                        "plays" to 3.0,
                        "points" to 4.0,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables with anchors",
                        "row_number" to 13,
                        "anchor" to "Group B",
                        "team" to "FC Porto",
                        "plays" to 3.0,
                        "points" to 4.0,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "multiple tables with anchors",
                        "row_number" to 14,
                        "anchor" to "Group B",
                        "team" to "AC Milan",
                        "plays" to 3.0,
                        "points" to 0.0,
                    )
                ),
            )
    }
}
