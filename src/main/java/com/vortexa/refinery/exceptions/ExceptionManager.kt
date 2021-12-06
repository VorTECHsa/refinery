package com.vortexa.refinery.exceptions

class ExceptionManager {

    val exceptions = mutableListOf<ExceptionData>()

    fun register(exception: Exception, location: Location? = null) {
        when (exception) {
            is ManagedException -> exceptions.add(ExceptionData(exception, location))
            else -> exceptions.add(ExceptionData(UncategorizedException(exception.toString()), location))
        }
    }

    fun containsCritical(): Boolean {
        return exceptions.any { it.isCritical() }
    }

    fun isEmpty(): Boolean {
        return exceptions.isEmpty()
    }

    data class Location(val sheetName: String, val rowNumber: Int? = null) {
        fun extractData(): Map<String, Any> {
            val data = mapOf("sheetName" to sheetName)
            return if (rowNumber == null) {
                data
            } else {
                data + ("rowNumber" to rowNumber)
            }
        }
    }

    data class ExceptionData(val exception: ManagedException, val location: Location? = null) {
        fun isCritical(): Boolean {
            return exception.level == Level.CRITICAL
        }
    }

    fun extractData(): List<Map<String, Any>> {
        val sortedByCriticality = exceptions.sortedBy { it.exception.level }
        return sortedByCriticality.map {
            if (it.location == null) it.exception.extractData() else it.exception.extractData() + it.location.extractData()
        }
    }
}
