package dev.lciszewski27.whereismymoney.domain.usecase

import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.domain.model.DebtType
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
    val netCents: Long = 0L,
    val netCurrency: String = "PLN"
)

/**
 * Observes a single person and their full debt list.
 */
class GetPersonDetailUseCase(
    private val repository: DebtRepository
) {
    operator fun invoke(personId: String): Flow<PersonDetailData> {
        return combine(
            repository.observePerson(personId),
            repository.observeDebtsForPerson(personId)
        ) { person, debts ->
            val net = debts
                .filter { !it.isSettled }
                .sumOf {
                    when (it.type) {
                        DebtType.THEY_OWE_ME -> it.amountCents
                        DebtType.I_OWE_THEM -> -it.amountCents
                    }
                }
            PersonDetailData(
                person = person,
                debts = debts,
                netCents = net,
                netCurrency = debts.firstOrNull()?.currency ?: "PLN"
            )
        }
    }
}