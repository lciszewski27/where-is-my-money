package dev.lciszewski27.whereismymoney.domain.usecase

import dev.lciszewski27.whereismymoney.domain.model.SettleTransfer
import kotlin.math.min

/**
 * Pure debt-simplification engine (settle-up suggestions).
 *
 * Input balances use the app's convention: positive = the person owes the
 * user (receivable), negative = the user owes the person (payable).
 *
 * The engine matches receivables against payables greedily: if Alice owes
 * the user 100 and the user owes Bob 100, it suggests "Alice pays Bob 100",
 * cutting the user out as a middleman. Output transfers are minimal in
 * count for the matched amounts.
 */
class SettleUpUseCase {

    data class Balance(
        val personId: String,
        val personName: String,
        val personColorSeed: Long,
        /** Positive = they owe me, negative = I owe them. Minor units. */
        val balanceCents: Long
    )

    fun suggest(
        balances: List<Balance>,
        currency: String
    ): List<SettleTransfer> {
        // Creditors of the user (they owe me) can pay on my behalf;
        // people I owe receive money. Match largest first.
        val creditors = balances
            .filter { it.balanceCents > 0 }
            .sortedByDescending { it.balanceCents }
            .map { it to it.balanceCents }
            .toMutableList()
        val debtors = balances
            .filter { it.balanceCents < 0 }
            .sortedBy { it.balanceCents }
            .map { it to -it.balanceCents }
            .toMutableList()

        val transfers = mutableListOf<SettleTransfer>()
        var i = 0
        var j = 0
        while (i < creditors.size && j < debtors.size) {
            val (creditor, creditOwed) = creditors[i]
            val (debtor, debtOwed) = debtors[j]
            if (creditOwed <= 0) {
                i++
                continue
            }
            if (debtOwed <= 0) {
                j++
                continue
            }
            val amount = min(creditOwed, debtOwed)
            transfers.add(
                SettleTransfer(
                    fromPersonId = creditor.personId,
                    fromPersonName = creditor.personName,
                    fromPersonColorSeed = creditor.personColorSeed,
                    toPersonId = debtor.personId,
                    toPersonName = debtor.personName,
                    toPersonColorSeed = debtor.personColorSeed,
                    amountCents = amount,
                    currency = currency
                )
            )
            creditors[i] = creditor to (creditOwed - amount)
            debtors[j] = debtor to (debtOwed - amount)
        }
        return transfers
    }
}
