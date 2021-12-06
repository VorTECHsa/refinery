package com.vortexa.refinery.result

class Metadata(private val data: Map<String, Any>) {

    private var divider: String? = null

    fun getDivider(): String {
        return divider!!
    }

    fun setDivider(value: String) {
        this.divider = value
    }

    fun getWorkbookName(): String {
        return data[WORKBOOK_NAME]!! as String
    }

    fun getSheetName(): String {
        return data[SPREADSHEET_NAME]!! as String
    }

    fun getAnchor(): String {
        return data[ANCHOR]!! as String
    }

    operator fun plus(element: Pair<String, Any>): Metadata {
        return Metadata(this.data + mapOf(element))
    }

    operator fun get(key: String): Any = this.data[key]!!

    fun allData(): Map<String, Any> {
        return if (divider == null) {
            data
        } else {
            data + (DIVIDER to divider!!)
        }
    }


    companion object {
        const val WORKBOOK_NAME = "workbook_name"
        const val SPREADSHEET_NAME = "spreadsheet_name"
        const val ANCHOR = "anchor"
        const val DIVIDER = "divider"
        const val ROW_NUMBER = "row_number"
    }

}
