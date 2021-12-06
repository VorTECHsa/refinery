package com.vortexa.refinery

import com.vortexa.refinery.configuration.testDefinition
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.result.GenericParsedRecord
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDateTime

class TestGenericRowParser {

    @Test
    fun `test generic row parser parses table and provides the correct raw parsed output including uncaptured headers`() {
        // given
        val fileName = "spreadsheet_examples/test_spreadsheet_uncaptured.xlsx"
        val file = File(
            javaClass.classLoader.getResource(fileName)!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)

        // when
        val exceptionManager = ExceptionManager()
        val parsedRecords = WorkbookParser(testDefinition, workbook, exceptionManager, fileName).parse()

        // then
        assertThat(exceptionManager.exceptions).hasSize(1)
        assertThat(parsedRecords)
            .hasSize(3)
            .containsExactly(
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "one",
                        "number" to 1,
                        "date" to LocalDateTime.of(2021, 1, 1, 0, 0),
                        "optional_str" to "exist",
                        "row_number" to 2,
                        "uncaptured" to "Bob",
                        "double" to 1.05,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "two",
                        "number" to 2,
                        "date" to LocalDateTime.of(2021, 1, 2, 0, 0),
                        "row_number" to 3,
                        "uncaptured" to "Alice",
                        "double" to 2.5,
                    )
                ),
                GenericParsedRecord(
                    mapOf(
                        "workbook_name" to fileName,
                        "spreadsheet_name" to "Sheet1",
                        "string" to "three",
                        "number" to 3,
                        "date" to LocalDateTime.of(2021, 1, 3, 0, 0),
                        "row_number" to 4,
                        "double" to 3.14,
                    )
                )
            )
    }
}
