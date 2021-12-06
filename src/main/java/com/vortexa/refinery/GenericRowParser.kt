package com.vortexa.refinery

import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.result.GenericParsedRecord
import com.vortexa.refinery.result.RowParserData
import org.apache.poi.ss.usermodel.Row

open class GenericRowParser(rowParserData: RowParserData, exceptionManager: ExceptionManager) :
    RowParser(rowParserData, exceptionManager) {

    override fun toRecord(row: Row): GenericParsedRecord {
        val allData = extractAllData(row)
        return GenericParsedRecord(allData)
    }

    override fun shouldStoreExtractedRawDataInParsedRecord(): Boolean {
        return false
    }
}
