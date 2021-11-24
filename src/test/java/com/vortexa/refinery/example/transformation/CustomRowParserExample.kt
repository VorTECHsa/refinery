package com.vortexa.refinery.example.transformation

import com.vortexa.refinery.RowParser
import com.vortexa.refinery.WorkbookParser
import com.vortexa.refinery.cell.StringHeaderCell
import com.vortexa.refinery.dsl.SheetParserDefinition
import com.vortexa.refinery.dsl.TableParserDefinition
import com.vortexa.refinery.dsl.WorkbookParserDefinition
import com.vortexa.refinery.exceptions.ExceptionManager
import com.vortexa.refinery.result.ParsedRecord
import com.vortexa.refinery.result.RowParserData
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate

class CustomRowParserExample {

    @Test
    fun `should extract and transform the data into the data class`() {
        // given
        // header cells
        val team = StringHeaderCell("team")
        val plays = StringHeaderCell("plays")
        val wins = StringHeaderCell("wins")
        val goalDiff = StringHeaderCell("goal diff")
        val points = StringHeaderCell("points")
        val date = StringHeaderCell("date")

        // and data class to store the data
        data class TeamStat(
            val team: String,
            val group: String,
            val plays: Int,
            val wins: Int?,
            val goalDiff: Int?,
            val points: Int,
            val date: LocalDate
        ) : ParsedRecord()

        // and row parser
        class StatParser(rowParserData: RowParserData, exceptionManager: ExceptionManager) :
            RowParser(rowParserData, exceptionManager) {

            override fun toRecord(row: Row): TeamStat {
                return TeamStat(
                    team = parseRequiredFieldAsString(row, team),
                    group = rowParserData.metadata.getAnchor(),
                    plays = parseOptionalFieldAsInteger(row, plays)!!,
                    wins = parseOptionalFieldAsInteger(row, wins),
                    goalDiff = parseOptionalFieldAsInteger(row, goalDiff),
                    points = parseOptionalFieldAsInteger(row, points)!!,
                    date = parseOptionalFieldAsDateTime(row, date)!!.toLocalDate()
                )
            }
        }

        // and finally the definition
        val headerColumns = setOf(team, plays, wins, goalDiff, points, date)
        val definition = WorkbookParserDefinition(
            spreadsheetParserDefinitions = listOf(
                SheetParserDefinition(
                    sheetNameFilter = { it == "broken stats" },
                    tableDefinitions = listOf(
                        // 1st table
                        TableParserDefinition(
                            requiredColumns = headerColumns,
                            anchor = "Group A",
                            rowParserFactory = ::StatParser
                        ),
                        // 2nd table
                        TableParserDefinition(
                            requiredColumns = headerColumns,
                            anchor = "Group B",
                            rowParserFactory = ::StatParser
                        )
                    )
                )
            )
        )

        // and
        val file = File(
            javaClass.classLoader.getResource("examples/basic_examples.xlsx")!!.file
        )
        val workbook = WorkbookFactory.create(file)

        // when
        val em = ExceptionManager()
        val parsedRecords = WorkbookParser(definition, workbook, em).parse()

        println(em.exceptions)
        // then
        assertThat(parsedRecords)
            .hasSize(8)

        // and
        assertThat(parsedRecords.filterIsInstance<TeamStat>())
            .hasSize(7)
            .containsExactly(
                TeamStat("PSG", "Group A", 3, 2, 3, 7, LocalDate.of(2021, 11, 2)),
                TeamStat("Manchester City", "Group A", 3, 2, 5, 6, LocalDate.of(2021, 11, 2)),
                TeamStat("RB Leipzig", "Group A", 3, 0, -5, 0, LocalDate.of(2021, 11, 2)),
                TeamStat("Liverpool", "Group B", 4, 4, 8, 12, LocalDate.of(2021, 11, 4)),
                TeamStat("FC Porto", "Group B", 4, 1, -3, 5, LocalDate.of(2021, 11, 4)),
                TeamStat("Atletico Madrid", "Group B", 4, 1, null, 4, LocalDate.of(2021, 11, 4)),
                TeamStat("AC Milan", "Group B", 4, 0, null, 1, LocalDate.of(2021, 11, 4)),
            )
    }
}
