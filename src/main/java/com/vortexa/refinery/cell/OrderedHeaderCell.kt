package com.vortexa.refinery.cell

data class OrderedHeaderCell(val headerCell: IHeaderCell, val priority: Int) : IHeaderCell {
    override fun contains(s: String) = headerCell.contains(s)
}



