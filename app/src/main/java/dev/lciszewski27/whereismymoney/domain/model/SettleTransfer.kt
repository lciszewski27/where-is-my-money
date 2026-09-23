package dev.lciszewski27.whereismymoney.domain.model

/**
 * One suggested transfer that simplifies the debt graph, e.g.
 * "Alice pays Bob 12.50 PLN" so the user is cut out as a middleman.
 *
 * Amounts are in minor units of [currency] (already converted to the
 * primary currency by [dev.lciszewski27.whereismymoney.domain.usecase.SettleUpUseCase]).
 */
data class SettleTransfer(
    val fromPersonId: String,
    val fromPersonName: String,
    val fromPersonColorSeed: Long,
    val toPersonId: String,
    val toPersonName: String,
    val toPersonColorSeed: Long,
    val amountCents: Long,
    val currency: String
)
