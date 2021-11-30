package com.vortexa.refinery.exceptions

class UncapturedHeadersException(val uncapturedHeaders: List<UncapturedHeaderCell>) : ManagedException(
    generateMessage(uncapturedHeaders),
    Level.WARNING
) {

    init {
        if (uncapturedHeaders.isEmpty()) throw IllegalArgumentException("Should be at least 1 uncaptured header")
    }

    companion object {
        private fun generateMessage(uncapturedHeaders: List<UncapturedHeaderCell>): String = uncapturedHeaders
            .map { "${it.name} @ ${it.index + 1}" }
            .joinToString { it }
    }

    data class UncapturedHeaderCell(val name: String, val index: Int) {
        init {
            if (index < 0) throw IllegalArgumentException("Index should be non-negative")
        }
    }
}
