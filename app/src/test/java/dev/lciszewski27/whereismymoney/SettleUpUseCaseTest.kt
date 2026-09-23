package dev.lciszewski27.whereismymoney

import dev.lciszewski27.whereismymoney.domain.usecase.SettleUpUseCase
import dev.lciszewski27.whereismymoney.domain.usecase.SettleUpUseCase.Balance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettleUpUseCaseTest {

    private val engine = SettleUpUseCase()

    private fun balance(id: String, name: String, cents: Long) =
        Balance(personId = id, personName = name, personColorSeed = 0L, balanceCents = cents)

    @Test
    fun emptyBalances_suggestsNothing() {
        assertTrue(engine.suggest(emptyList(), "PLN").isEmpty())
    }

    @Test
    fun singleSidedBalances_suggestsNothing() {
        val balances = listOf(balance("a", "Alice", 10000), balance("b", "Bob", 5000))
        assertTrue(engine.suggest(balances, "PLN").isEmpty())
    }

    @Test
    fun exactMatch_producesSingleTransfer() {
        val balances = listOf(balance("a", "Alice", 10000), balance("b", "Bob", -10000))
        val result = engine.suggest(balances, "PLN")
        assertEquals(1, result.size)
        assertEquals("a", result[0].fromPersonId)
        assertEquals("b", result[0].toPersonId)
        assertEquals(10000L, result[0].amountCents)
        assertEquals("PLN", result[0].currency)
    }

    @Test
    fun partialMatch_splitsAcrossTransfers() {
        val balances = listOf(balance("a", "Alice", 10000), balance("b", "Bob", -4000))
        val result = engine.suggest(balances, "PLN")
        assertEquals(1, result.size)
        assertEquals(4000L, result[0].amountCents)
    }

    @Test
    fun oneCreditorTwoDebtors_fansOut() {
        val balances = listOf(
            balance("a", "Alice", 10000),
            balance("b", "Bob", -6000),
            balance("c", "Cara", -4000)
        )
        val result = engine.suggest(balances, "PLN")
        assertEquals(2, result.size)
        assertEquals(10000L, result.sumOf { it.amountCents })
    }

    @Test
    fun zeroBalances_areIgnored() {
        val balances = listOf(
            balance("a", "Alice", 10000),
            balance("b", "Bob", 0),
            balance("c", "Cara", -10000)
        )
        val result = engine.suggest(balances, "PLN")
        assertEquals(1, result.size)
    }
}
