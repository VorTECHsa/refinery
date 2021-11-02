package com.vortexa.refinery

import com.vortexa.refinery.dsl.WorkbookParserDefinition
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.result.ParsedRecord
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook

class WorkbookParser(
    private val definition: WorkbookParserDefinition,
    private val workbook: Workbook,
    private val exceptionManager: ExceptionManager = ExceptionManager()
) {

    fun parse(): List<ParsedRecord> {
        return try {
            workbook
                .filter { definition.includeHidden || !workbook.isSheetHidden((workbook.getSheetIndex(it.sheetName))) }
                .mapNotNull(this::resolveSheetParser)
                .flatMap(SheetParser::parse)
                .toList()
        } catch (e: Exception) {
            exceptionManager.register(e)
            emptyList()
        }
    }

    private fun resolveSheetParser(sheet: Sheet): SheetParser? {
        return definition.spreadsheetParserDefinitions
            .filter { definition -> definition.sheetNameFilter.invoke(sheet.sheetName) }
            .map { definition -> SheetParser(definition, sheet, exceptionManager) }
            .firstOrNull()
    }

}
