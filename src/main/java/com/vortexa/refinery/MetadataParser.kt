package com.vortexa.refinery

import com.vortexa.refinery.dsl.MetadataEntryDefinition
import com.vortexa.refinery.dsl.MetadataValueLocation.NEXT_CELL_VALUE
import com.vortexa.refinery.dsl.MetadataValueLocation.NEXT_ROW_VALUE
import com.vortexa.refinery.dsl.MetadataValueLocation.PREVIOUS_CELL_VALUE
import com.vortexa.refinery.dsl.MetadataValueLocation.PREVIOUS_ROW_VALUE
import com.vortexa.refinery.dsl.MetadataValueLocation.SAME_CELL_VALUE
import com.vortexa.refinery.result.Metadata
import com.vortexa.refinery.result.Metadata.Companion.SPREADSHEET_NAME
import com.vortexa.refinery.result.Metadata.Companion.WORKBOOK_NAME
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Sheet

internal class MetadataParser(
    private val definitions: List<MetadataEntryDefinition>,
    private val sheet: Sheet,
    private val workbookName: String?
) {

    fun extractMetadata(): Metadata {
        val metadata = hashMapOf<String, Any>(
            SPREADSHEET_NAME to sheet.sheetName
        )
        if (workbookName != null) metadata[WORKBOOK_NAME] = workbookName
        definitions.forEach {
            val kv = findMetadata(it)
            metadata[kv.first] = kv.second
        }
        return Metadata(metadata)
    }

    private fun findMetadata(definition: MetadataEntryDefinition): Pair<String, Any> {
        val matchingCell = sheet.rowIterator().asSequence()
            .flatMap { it.cellIterator().asSequence() }
            .filter { it.toString().contains(definition.matchingCellKey) }
            .first()
        val cell: Cell = when (definition.valueLocation) {
            PREVIOUS_ROW_VALUE -> sheet.getRow(matchingCell.rowIndex - 1).getCell(matchingCell.columnIndex)
            NEXT_ROW_VALUE -> sheet.getRow(matchingCell.rowIndex + 1).getCell(matchingCell.columnIndex)
            PREVIOUS_CELL_VALUE -> sheet.getRow(matchingCell.rowIndex).getCell(matchingCell.columnIndex - 1)
            SAME_CELL_VALUE -> sheet.getRow(matchingCell.rowIndex).getCell(matchingCell.columnIndex)
            NEXT_CELL_VALUE -> sheet.getRow(matchingCell.rowIndex).getCell(matchingCell.columnIndex + 1)
        }
        return definition.metadataName to definition.extractor.invoke(cell)
    }
}
