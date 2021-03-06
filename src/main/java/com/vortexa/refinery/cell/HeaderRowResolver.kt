package com.vortexa.refinery.cell

import com.vortexa.refinery.dsl.TableParserDefinition
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row

class HeaderRowResolver(private val mergedCellsResolver: MergedCellsResolver) {

    fun resolveHeaderCellIndex(headerRow: Row, headerCells: Set<AbstractHeaderCell>): Map<AbstractHeaderCell, Int> {
        val (orderedCells, unorderedCells) = headerCells.partitionByType<OrderedHeaderCell, AbstractHeaderCell>()

        val result = resolveOrderedHeaders(headerRow, orderedCells) + resolveUnorderedHeaders(headerRow, unorderedCells)

        return result.flatMap {
            when (val cell = it.key) {
                is MergedHeaderCell -> cell.headerCells.mapIndexed { i, hc -> Pair(hc, it.value + i) }
                else -> listOf(Pair(cell, it.value))
            }
        }.toMap()
    }

    fun isHeaderRow(row: Row, definition: TableParserDefinition): Boolean {
        val cellValues = row.asMappedSequence().toSet()
        return definition.requiredColumns.filterNot { it.inside(cellValues) }
            .isEmpty()
    }

    private fun Row.asMappedSequence(): Sequence<Cell> {
        return this.cellIterator().asSequence().map {
            val mergedCell = mergedCellsResolver[it.rowIndex, it.columnIndex]
            return@map if (mergedCell != null && mergedCell.columnIndex == it.columnIndex) mergedCell else it
        }
    }

    private fun resolveOrderedHeaders(row: Row, orderedCells: List<OrderedHeaderCell>): Map<AbstractHeaderCell, Int> {
        val matches = mutableMapOf<AbstractHeaderCell, Int>()
        val sortedCells = orderedCells.sortedBy { it.priority }
        row.asMappedSequence().forEach { cell ->
            val filtered = sortedCells.filterNot { matches.contains(it.headerCell) }
            val headerCellOrNull = filtered.firstOrNull { oc -> oc.matches(cell) }
            if (headerCellOrNull != null) matches[headerCellOrNull.headerCell] = cell.columnIndex
        }
        return matches
    }

    private fun resolveUnorderedHeaders(
        row: Row,
        unorderedCells: List<AbstractHeaderCell>
    ): Map<AbstractHeaderCell, Int> {
        return row.asMappedSequence().mapNotNull { cell ->
            val headerCellOrNull = unorderedCells.firstOrNull { hc -> hc.matches(cell) }
            if (headerCellOrNull != null) Pair(headerCellOrNull, cell.columnIndex) else null
        }.toMap()
    }

    private inline fun <reified U : T, T> Iterable<T>.partitionByType(): Pair<List<U>, List<T>> {
        val first = ArrayList<U>()
        val second = ArrayList<T>()
        for (element in this) {
            if (element is U) first.add(element)
            else second.add(element)
        }
        return Pair(first, second)
    }
}
