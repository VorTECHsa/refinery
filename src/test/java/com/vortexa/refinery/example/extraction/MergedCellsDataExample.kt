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

class MergedCellsDataExample {

    @Test
    fun `should extract the data from the merged data cells and persist in each generic record`() {
        // given
        val headerColumns = setOf(
            StringHeaderCell("team"),
            StringHeaderCell("plays"),
            StringHeaderCell("points")
        )
        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { it == "data with merged cells" },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            requiredColumns = headerColumns,
                            hasDivider = true
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
            .hasSize(4)
            .containsExactly(
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "data with merged cells",
                        "row_number" to 2,
                        "team" to "PSG",
                        "plays" to 3,
                        "points" to 7,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "data with merged cells",
                        "row_number" to 3,
                        "team" to "Manchester City",
                        "plays" to 3,
                        "points" to 6,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "data with merged cells",
                        "row_number" to 4,
                        "team" to "Club Brugge",
                        "plays" to 3,
                        "points" to 4,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "data with merged cells",
                        "row_number" to 5,
                        "team" to "RB Leipzig",
                        "plays" to 3,
                        "points" to 0,
                    )
                )
            )
    }
}
