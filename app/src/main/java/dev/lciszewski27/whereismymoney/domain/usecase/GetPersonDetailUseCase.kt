package dev.lciszewski27.whereismymoney.domain.usecase

import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Payment
import dev.lciszewski27.whereismymoney.domain.model.Person
import dev.lciszewski27.whereismymoney.domain.repository.DebtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Combined person info + their unsettled debts + computed net.
 */
data class PersonDetailData(
    val person: Person?,
    val debts: List<Debt>,
    val payments: List<Payment> = emptyList(),
    val netCents: Long = 0L,
    val netCurrency: String = "PLN"
)

/**
 * Observes a single person, their full debt list and their payment ledger.
 * The net balance is converted into primaryCurrency, matching the
 * dashboard — individual debts keep their own currencies.
 */
class GetPersonDetailUseCase(
    private val repository: DebtRepository,
    private val currencyConversion: CurrencyConversionUseCase
) {
    operator fun invoke(personId: String, primaryCurrency: String): Flow<PersonDetailData> {
        return combine(
            repository.observePerson(personId),
            repository.observeDebtsForPerson(personId),
            repository.observePaymentsForPerson(personId)
        ) { person, debts, payments ->
            val net = debts
                .filter { !it.isSettled }
                .sumOf {
                    val converted = currencyConversion.convert(
                        cents = it.amountCents,
                        fromCurrency = it.currency,
                        toCurrency = primaryCurrency
                    )
                    when (it.type) {
                        DebtType.THEY_OWE_ME -> converted
                        DebtType.I_OWE_THEM -> -converted
                    }
                }
            PersonDetailData(
                person = person,
                debts = debts,
                payments = payments,
                netCents = net,
                netCurrency = primaryCurrency
            )
        }
    }
}