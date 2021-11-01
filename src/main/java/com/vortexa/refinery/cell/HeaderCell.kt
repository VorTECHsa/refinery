package com.vortexa.refinery.cell

data class HeaderCell(val patterns: List<String>) : AbstractHeaderCell(patterns) {

    constructor(pattern: String) : this(listOf(pattern))

    fun name(): String {
        return patterns.component1()
    }

}



