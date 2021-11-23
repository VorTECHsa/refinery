package com.vortexa.refinery.cell

data class HeaderCell(val patterns: List<String>) : IHeaderCell {

    constructor(pattern: String) : this(listOf(pattern))

    override fun contains(s: String)  = patterns.any { s.trim().lowercase().contains(it.lowercase()) }

}



