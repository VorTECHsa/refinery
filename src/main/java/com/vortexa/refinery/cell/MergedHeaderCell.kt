package com.vortexa.refinery.cell

class MergedHeaderCell(val headerCell: IHeaderCell, val headerCells: List<IHeaderCell>) : IHeaderCell {
    override fun contains(s: String) = headerCell.contains(s)
}
