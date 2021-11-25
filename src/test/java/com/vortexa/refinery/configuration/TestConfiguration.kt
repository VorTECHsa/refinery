package com.vortexa.refinery.configuration

import com.vortexa.refinery.GenericRowParser
import com.vortexa.refinery.cell.StringHeaderCell
import com.vortexa.refinery.dsl.SheetParserDefinition
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.dsl.WorkbookParserDefinition

val string = StringHeaderCell("string")
val number = StringHeaderCell("number")
val date = StringHeaderCell("date")
val optionalString = StringHeaderCell("optional_str")

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
