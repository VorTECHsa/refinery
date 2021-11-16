package com.vortexa.refinery.cell

sealed class AbstractHeaderCell(private val patterns: List<String>) {

    fun contains(s: String) = patterns.any { s.trim().lowercase().contains(it.lowercase()) }

    fun inside(values: Set<String>) = values.any { this.contains(it) }

    fun maxContainsLength(s: String) =
        patterns.filter { s.trim().lowercase().contains(it.lowercase()) }.map {it.length}.maxOrNull()
}
