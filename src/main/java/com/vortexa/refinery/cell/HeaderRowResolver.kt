package com.vortexa.refinery.cell

import org.apache.poi.ss.usermodel.Row

class HeaderRowResolver {

    fun resolveHeaderCellIndex(headerRow: Row, headerCells: Set<AbstractHeaderCell>): Map<AbstractHeaderCell, Int> {
        val (orderedCells, unorderedCells) = headerCells.partition { it is OrderedHeaderCell }
            .let { Pair(it.first as List<OrderedHeaderCell>, it.second) }

        val result = resolveOrderedHeaders(headerRow, orderedCells) + resolveUnorderedHeaders(headerRow, unorderedCells)

        return result.flatMap {
            when (val cell = it.key) {
                is MergedHeaderCell -> cell.headerCells.mapIndexed { i, hc -> Pair(hc, it.value + i) }
                else -> listOf(Pair(cell, it.value))
            }
        }.toMap()
    }

    private fun resolveOrderedHeaders(row: Row, orderedCells: List<OrderedHeaderCell>): Map<AbstractHeaderCell, Int> {
        val matches = mutableMapOf<AbstractHeaderCell, Int>()
        val sortedCells = orderedCells.sortedBy { it.priority }
        row.cellIterator().asSequence().forEach { cell ->
            val filtered = sortedCells.filterNot { matches.contains(it.headerCell) }
            val headerCellOrNull = filtered.firstOrNull { oc -> oc.matches(cell) }
            if (headerCellOrNull != null) matches[headerCellOrNull.headerCell] = cell.columnIndex
        }
        return matches
    }

    private fun resolveUnorderedHeaders(row: Row,
                                        unorderedCells: List<AbstractHeaderCell>): Map<AbstractHeaderCell, Int> {
        return row.cellIterator().asSequence().mapNotNull { cell ->
            val headerCellOrNull = unorderedCells.firstOrNull { hc -> hc.matches(cell) }
            if (headerCellOrNull != null) Pair(headerCellOrNull, cell.columnIndex) else null
        }.toMap()
    }
}