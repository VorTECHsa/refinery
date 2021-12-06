package com.vortexa.refinery.exceptions

enum class Level {
    // Please keep the order for now from most critical to least as using that for sorting.

    CRITICAL,
    WARNING
}

abstract class ManagedException(override val message: String, val level: Level) : RuntimeException(message) {
    fun extractData(): Map<String, Any> {
        return mapOf("class" to javaClass.simpleName, "level" to level, "message" to message)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ManagedException

        if (message != other.message) return false
        if (level != other.level) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + level.hashCode()
        return result
    }
}

class CellParserException(message: String) : ManagedException(message, Level.WARNING)

class TableParserException(message: String) : ManagedException(message, Level.WARNING)

class SheetParserException(message: String) : ManagedException(message, Level.WARNING)

class WorkbookParserException(message: String) : ManagedException(message, Level.CRITICAL)

class UncategorizedException(message: String) : ManagedException(message, Level.CRITICAL)
