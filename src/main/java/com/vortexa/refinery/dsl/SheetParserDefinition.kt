package com.vortexa.refinery.dsl

/**
 * Defined how to parse sheets
 *
 * @property sheetNameFilter can be used to specify which sheets should be parsed
 * @property tableDefinitions support for multiple sheets
 * @property metadataParserDefinition support for key value pairs within the sheet that need extraction (will end up in metadata)
 */
data class SheetParserDefinition(
    val sheetNameFilter: (String) -> Boolean,
    val tableDefinitions: List<TableParserDefinition>,
    val metadataParserDefinition: List<MetadataEntryDefinition> = emptyList(),
)
