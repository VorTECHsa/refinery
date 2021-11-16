package com.vortexa.refinery

import com.vortexa.refinery.cell.HeaderCell
import com.vortexa.refinery.configuration.date
import com.vortexa.refinery.configuration.number
import com.vortexa.refinery.configuration.optionalString
import com.vortexa.refinery.configuration.string
import com.vortexa.refinery.dsl.SheetParserDefinition
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.dsl.WorkbookParserDefinition
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.result.ParsedRecord
import com.vortexa.refinery.result.RowParserData
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TestRowParser {

    @Test
    fun `test all field parsers`() {
        // given
        val string = HeaderCell("string")
        val integer = HeaderCell("integer")
        val double = HeaderCell("double")
        val date = HeaderCell("date")
        val formatDate = HeaderCell("format_date")
        val optionalString = HeaderCell("optional_str")
        val optionalInteger = HeaderCell("optional_integer")
        val optionalDouble= HeaderCell("optional_double")
        val optionalDate = HeaderCell("optional_date")
        val optionalFormatDate = HeaderCell("optional_format_date")

        data class ExampleData(
            val stringField: String,
            val integerField: Int,
            val doubleField: Double,
            val dateTimeField: LocalDateTime,
            val dateField: LocalDate,
            val optionalStringField: String?,
            val optionalIntegerField: Int?,
            val optionalDoubleField: Double?,
            val optionalDateTimeField: LocalDateTime?,
            val optionalDateField: LocalDate?
        ) : ParsedRecord()

        class TestParser(rowParserData: RowParserData, exceptionManager: ExceptionManager) :
            RowParser(rowParserData, exceptionManager) {
            override fun toRecord(row: Row): ExampleData {
                return ExampleData(
                    stringField = parseRequiredFieldAsString(row, string),
                    integerField = parseRequiredFieldAsInteger(row, integer),
                    doubleField = parseRequiredFieldAsDouble(row, double),
                    dateTimeField = parseRequiredFieldAsDateTime(row, date),
                    dateField = parseRequiredFieldAsDateWithFormat(row, formatDate,
                        DateTimeFormatter.ofPattern("MM-dd-yyyy")),
                    optionalStringField = parseOptionalFieldAsString(row, optionalString),
                    optionalIntegerField = parseOptionalFieldAsInteger(row, optionalInteger),
                    optionalDoubleField = parseOptionalFieldAsDouble(row, optionalDouble),
                    optionalDateTimeField = parseOptionalFieldAsDateTime(row, optionalDate),
                    optionalDateField = parseOptionalFieldAsDateWithFormat(row, optionalFormatDate,
                        DateTimeFormatter.ofPattern("MM-dd-yyyy"))
                )
            }
        }

        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { true },
                    tableDefinitions = listOf(
                        TableParserDefinition(
                            setOf(string, integer, double, date, formatDate),
                            setOf(optionalString, optionalInteger, optionalDouble, optionalDate, optionalFormatDate),
                            ::TestParser,
                        )
                    )
                )
            )
        )

        val file = File(
            javaClass.classLoader.getResource("spreadsheet_examples/test_spreadsheet_multiple_fields.xlsx")!!.file
        )
        val workbook: Workbook = WorkbookFactory.create(file)
        val exceptionManager = ExceptionManager()

        // when
        val records = WorkbookParser(definition, workbook, exceptionManager).parse()

        // then
        Assertions.assertThat(exceptionManager.exceptions).isEmpty()
        Assertions.assertThat(records).containsExactly(
            ExampleData("one",1,1.00, LocalDate.parse("2021-01-01").atStartOfDay(), LocalDate.parse("2021-01-01"),
            "exist",1,null, null,LocalDate.parse("2021-01-01") ),
            ExampleData("two",2,2.02, LocalDate.parse("2021-01-02").atStartOfDay(), LocalDate.parse("2021-01-02"),
                "",null,2.00, LocalDate.parse("2021-01-02").atStartOfDay(),null ),
            ExampleData("three",3,3.03, LocalDate.parse("2021-01-03").atStartOfDay(), LocalDate.parse("2021-01-03"),
                null,null,3.03, LocalDate.parse("2021-01-03").atStartOfDay(),LocalDate.parse("2021-01-03") )
        )
    }
}