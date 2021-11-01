package com.vortexa.refinery.configuration

import com.vortexa.refinery.GenericRowParser
import com.vortexa.refinery.cell.HeaderCell
import com.vortexa.refinery.dsl.SheetParserDefinition
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.dsl.WorkbookParserDefinition

val string = HeaderCell("string")
val number = HeaderCell("number")
val date = HeaderCell("date")
val optionalString = HeaderCell("optional_str")

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
                    ::GenericRowParser
                )
            )
        )
    )
)
