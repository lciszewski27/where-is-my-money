package dev.lciszewski27.whereismymoney.domain.usecase

import dev.lciszewski27.whereismymoney.domain.model.DashboardSummary
import dev.lciszewski27.whereismymoney.domain.repository.DebtRepository

/**
 * Observable dashboard summary with currency conversion.
 */
class GetDashboardSummaryUseCase(
    private val repository: DebtRepository
) {
    operator fun invoke(primaryCurrency: String) =
        repository.observeDashboardSummary(primaryCurrency)
}