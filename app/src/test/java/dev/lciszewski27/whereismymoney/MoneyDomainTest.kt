package dev.lciszewski27.whereismymoney

import dev.lciszewski27.whereismymoney.domain.util.MoneyInput
import dev.lciszewski27.whereismymoney.domain.util.SplitCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoneyDomainTest {

    @Test
    fun parseInput_handlesNegativeSign() {
        assertEquals(-520L, MoneyInput.parseInputToCents("-5.20"))
        assertEquals(-500L, MoneyInput.parseInputToCents("-5"))
    }

    @Test
    fun parseInput_handlesCommonFormats() {
        assertEquals(1250L, MoneyInput.parseInputToCents("12.50"))
        assertEquals(1250L, MoneyInput.parseInputToCents("12,5"))
        assertEquals(99L, MoneyInput.parseInputToCents(".99"))
        assertEquals(0L, MoneyInput.parseInputToCents("12.3.4"))
        assertEquals(0L, MoneyInput.parseInputToCents(""))
    }

    @Test
    fun formatCents_roundTrips() {
        assertEquals("12.50", MoneyInput.formatCentsForInput(1250L))
        assertEquals("0.05", MoneyInput.formatCentsForInput(5L))
    }

    @Test
    fun equalShares_sumBackToTotal() {
        val shares = SplitCalculator.equalShares(1000L, 3)
        assertEquals(listOf(334L, 333L, 333L), shares)
        assertEquals(1000L, shares.sum())
    }

    @Test
    fun equalShares_evenSplit() {
        assertEquals(listOf(500L, 500L), SplitCalculator.equalShares(1000L, 2))
    }

    @Test
    fun equalShares_invalidInput_returnsEmpty() {
        assertTrue(SplitCalculator.equalShares(1000L, 0).isEmpty())
        assertTrue(SplitCalculator.equalShares(0L, 3).isEmpty())
        assertTrue(SplitCalculator.equalShares(-100L, 3).isEmpty())
    }
}
