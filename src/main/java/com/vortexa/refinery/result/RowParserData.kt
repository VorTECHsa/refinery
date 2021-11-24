package com.vortexa.refinery.result

import com.vortexa.refinery.cell.AbstractHeaderCell
import com.vortexa.refinery.cell.MergedCellsResolver

data class RowParserData(
    val headerMap: Map<AbstractHeaderCell, Int>,
    val mergedCellsResolver: MergedCellsResolver,
    val metadata: Metadata,
    val allHeadersMapping: Map<String, Int>
)
