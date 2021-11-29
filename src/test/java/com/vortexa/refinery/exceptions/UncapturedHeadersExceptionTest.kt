package com.vortexa.refinery.exceptions

import com.vortexa.refinery.exceptions.UncapturedHeadersException.UncapturedHeaderCell
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class UncapturedHeadersExceptionTest {

    @Test
    fun `should convert to the message correctly`() {
        // when
        val exception =
            UncapturedHeadersException(listOf(UncapturedHeaderCell("foo", 1), UncapturedHeaderCell("bar", 2)))

        // then
        assertThat(exception.message).isEqualTo("foo @ 2, bar @ 3")
    }

    @Test
    fun `should throw if empty list is provided`() {
        // expect
        assertThatThrownBy { UncapturedHeadersException(listOf()) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Should be at least 1 uncaptured header")
    }

    @Test
    fun `should throw if index is negative`() {
        // expect
        assertThatThrownBy { UncapturedHeaderCell("foo", -1) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Index should be non-negative")
    }
}
