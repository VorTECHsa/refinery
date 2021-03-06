package com.vortexa.refinery.cell

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class TestMergedCellsResolver {

    @Test
    fun `should throw if cell row index is negative`() {
        // expect
        assertThatThrownBy { MergedCellsResolver.CellLocation(-1, 0) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("row index should be non-negative")
    }

    @Test
    fun `should throw if cell column index is negative`() {
        // expect
        assertThatThrownBy { MergedCellsResolver.CellLocation(0, -1) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("column index should be non-negative")
    }
}
