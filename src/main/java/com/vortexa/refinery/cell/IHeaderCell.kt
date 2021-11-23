package com.vortexa.refinery.cell

sealed interface IHeaderCell {

    fun contains(s: String): Boolean

    fun inside(values: Set<String>) = values.any { this.contains(it) }
}
