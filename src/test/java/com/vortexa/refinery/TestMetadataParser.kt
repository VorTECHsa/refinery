package com.vortexa.refinery

import com.vortexa.refinery.dsl.MetadataEntryDefinition
import com.vortexa.refinery.dsl.MetadataValueLocation
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class TestMetadataParser {

    private val fileName = "spreadsheet_examples/test_metadata_extractor.xlsx"
    private val file = File(
        javaClass.classLoader.getResource(fileName)!!.file
    )
    private val workbook: Workbook = WorkbookFactory.create(file)
    private val sheet = workbook.sheetIterator().asSequence().first()

    private fun lookForMetadata(anchor: String, location: MetadataValueLocation): List<String> {
        val found = mutableListOf<String>()
        val parser = MetadataParser(
            listOf(
                MetadataEntryDefinition(
                    "MetadataName",
                    anchor,
                    location
                ) { c -> found.add(c.toString()) }
            ),
            sheet,
            null
        )

        parser.extractMetadata()
        return found
    }

    @Test
    fun testExtractPreviousRow() {
        val found = lookForMetadata("Anchor to Previous Row", MetadataValueLocation.PREVIOUS_ROW_VALUE)
        assertEquals(1, found.size)
        assertEquals("METADATA PREVIOUS ROW", found[0].trim())
    }

    @Test
    fun testExtractNextRow() {
        val found = lookForMetadata("Anchor to Next Row", MetadataValueLocation.NEXT_ROW_VALUE)
        assertEquals(1, found.size)
        assertEquals("METADATA NEXT ROW", found[0].trim())
    }

    @Test
    fun testExtractNextCell() {
        val found = lookForMetadata("Anchor to Next Cell", MetadataValueLocation.NEXT_CELL_VALUE)
        assertEquals(1, found.size)
        assertEquals("METADATA NEXT CELL", found[0].trim())
    }

    @Test
    fun testExtractPreviousCell() {
        val found = lookForMetadata("Anchor to Previous Cell", MetadataValueLocation.PREVIOUS_CELL_VALUE)
        assertEquals(1, found.size)
        assertEquals("METADATA PREVIOUS CELL", found[0].trim())
    }

    @Test
    fun testExtractSameCell() {
        val found = lookForMetadata("SAME CELL: METADATA SAME CELL", MetadataValueLocation.SAME_CELL_VALUE)
        assertEquals(1, found.size)
        assertEquals("SAME CELL: METADATA SAME CELL", found[0].trim())
    }
}
