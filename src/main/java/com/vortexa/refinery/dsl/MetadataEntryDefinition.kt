package com.vortexa.refinery.dsl

import org.apache.poi.ss.usermodel.Cell

/**
 * Defines where to look for key value data and how to parse it
 *
 * @property metadataName name that will be available in metadata
 * @property matchingCellKey pattern for matching the key cell
 * @property valueLocation where to look for the data
 * @property extractor how to parse the data
 */
data class MetadataEntryDefinition(
    val metadataName: String,
    val matchingCellKey: String,
    val valueLocation: MetadataValueLocation,
    val extractor: (Cell) -> Any
) {
}
