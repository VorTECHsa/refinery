package com.vortexa.refinery.example.extraction

import com.vortexa.refinery.WorkbookParser
import com.vortexa.refinery.cell.SimpleHeaderCell
import com.vortexa.refinery.dsl.MetadataEntryDefinition
import com.vortexa.refinery.dsl.MetadataValueLocation
import com.vortexa.refinery.dsl.SheetParserDefinition
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.dsl.WorkbookParserDefinition
import com.vortexa.refinery.result.GenericParsedRecord
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate

class MediumArticleExample {

    @Test
    fun `should extract from medium article example`() {
        // given
        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { it == "Awesome port" },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            anchor = "Expected",
                            requiredColumns = setOf(
                                SimpleHeaderCell("Vessel"),
                                SimpleHeaderCell("ETA"),
                                SimpleHeaderCell("Quantity"),
                            ),
                            hasDivider = true
                        ),
                        TableParserDefinition(
                            anchor = "At Berth",
                            requiredColumns = setOf(
                                SimpleHeaderCell("Vessel"),
                                SimpleHeaderCell("Berthed"),
                                SimpleHeaderCell("Quantity"),
                            ),
                            hasDivider = true
                        )
                    ),
                    metadataParserDefinition = listOf(
                        MetadataEntryDefinition(
                            "Report date",
                            "Report date",
                            MetadataValueLocation.NEXT_CELL_VALUE
                        ) { it.localDateTimeCellValue }
                    )
                )
            )
        )

        // and
        val fileName = "examples/medium_article_example.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )

        // when
        val parsedRecords =
            WorkbookFactory.create(file).use { WorkbookParser(definition, it, workbookName = fileName).parse() }

        // then
        assertThat(parsedRecords)
            .hasSize(7)
            .startsWith(
                GenericParsedRecord(
                    mapOf(
                        "spreadsheet_name" to "Awesome port",
                        "workbook_name" to fileName,
                        "Report date" to LocalDate.of(2022, 4, 1).atStartOfDay(),
                        "anchor" to "Expected",
                        "divider" to "Crude oil",
                        "Vessel" to "Lorem",
                        "ETA" to LocalDate.of(2022, 4, 2).atStartOfDay(),
                        "Quantity" to 10000,
                        "row_number" to 6,
                    )
                )
            )
    }
}
