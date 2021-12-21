package com.vortexa.refinery

import com.vortexa.refinery.TableParser.TableLocation
import com.vortexa.refinery.cell.HeaderRowResolver
import com.vortexa.refinery.cell.MergedCellsResolver
import com.vortexa.refinery.dsl.SheetParserDefinition
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.exceptions.ManagedException
import com.vortexa.refinery.exceptions.SheetParserException
import com.vortexa.refinery.result.Metadata
import com.vortexa.refinery.result.ParsedRecord
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

internal class SheetParser(
    private val definition: SheetParserDefinition,
    private val sheet: Sheet,
    private val exceptionManager: ExceptionManager,
    private val workbookName: String?
) {
    private val mergedCellsResolver = MergedCellsResolver(sheet)
    private val headerRowResolver = HeaderRowResolver(mergedCellsResolver)

    fun parse(): List<ParsedRecord> {
        return try {
            val metadata = MetadataParser(definition.metadataParserDefinition, sheet, workbookName).extractMetadata()
            val tableParsers = resolveTableParsers(metadata)
            tableParsers.flatMap { it.parse() }
        } catch (e: ManagedException) {
            exceptionManager.register(e, ExceptionManager.Location(sheet.sheetName))
            emptyList()
        }
    }

    private fun resolveTableParsers(metadata: Metadata): List<TableParser> {
        val tableLocations = resolveTableLocations()
        return tableLocations.map {
            TableParser(sheet, it.first, metadata, it.second, mergedCellsResolver, exceptionManager, headerRowResolver)
        }
    }

    private fun resolveTableLocations(): List<Pair<TableParserDefinition, TableLocation>> {
        val definitionsBeginning: List<Pair<TableParserDefinition, Int>> = sheet.rowIterator().asSequence()
            .mapNotNull { row ->
                when (val tableDefinition = row.tryMatchToTableParserDefinition()) {
                    null -> return@mapNotNull null
                    else -> return@mapNotNull Pair(tableDefinition, row.rowNum)
                }
            }
            .sortedBy { it.second }
            .toList()
        if (definitionsBeginning.isEmpty()) throw SheetParserException("Could not locate any tables")
        return definitionsBeginning.zipWithNext { def1, def2 ->
            Pair(def1.first, TableLocation(def1.second, def2.second - 1))
        }.toMutableList().also {
            it.add(
                Pair(
                    definitionsBeginning.last().first,
                    TableLocation(definitionsBeginning.last().second, sheet.lastRowNum)
                )
            )
        }
    }

    private fun Row.tryMatchToTableParserDefinition(): TableParserDefinition? {
        for (sd in definition.tableDefinitions) {
            if (sd.anchor != null) {
                val cellValues = this.cellIterator()
                    .asSequence()
                    .filter { it.cellType == CellType.STRING }
                    .map { it.stringCellValue.trim().lowercase() }
                if (cellValues.any { it.contains(sd.anchor.lowercase()) } &&
                    !headerRowResolver.isHeaderRow(this, sd)
                ) {
                    return sd
                }
            } else if (headerRowResolver.isHeaderRow(this, sd)) {
                return sd
            }
        }
        return null
    }
}
