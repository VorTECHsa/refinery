package com.vortexa.refinery.dsl

/**
 * Top level definition for how to parse the workbook
 *
 * @property spreadsheetParserDefinitions
 * @property includeHidden if true will parse hidden sheets
 */
data class WorkbookParserDefinition(
    val spreadsheetParserDefinitions: List<SheetParserDefinition>,
    val includeHidden: Boolean = false
)
