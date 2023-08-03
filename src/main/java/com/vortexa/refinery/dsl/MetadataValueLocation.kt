package com.vortexa.refinery.dsl

/**
 * Where to look for the value
 */
enum class MetadataValueLocation {
    PREVIOUS_ROW_VALUE,
    NEXT_ROW_VALUE,
    PREVIOUS_CELL_VALUE,
    SAME_CELL_VALUE,
    NEXT_CELL_VALUE
}
