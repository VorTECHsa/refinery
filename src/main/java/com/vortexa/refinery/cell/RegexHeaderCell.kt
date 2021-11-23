package com.vortexa.refinery.cell

data class RegexHeaderCell(val patterns: List<Regex>) : IHeaderCell {

    constructor(pattern: Regex) : this(listOf(pattern))

    override fun contains(s: String)  = patterns.any { it.containsMatchIn(s)}

}



