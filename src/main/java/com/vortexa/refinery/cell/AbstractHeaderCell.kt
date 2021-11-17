package com.vortexa.refinery.cell

sealed class AbstractHeaderCell(private val patterns: List<String>) {

    fun contains(s: String) = patterns.any { it.toRegex(option = RegexOption.IGNORE_CASE).containsMatchIn(s) }

    fun inside(values: Set<String>) = values.any { this.contains(it) }
}
