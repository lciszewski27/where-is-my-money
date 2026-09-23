package dev.lciszewski27.whereismymoney.domain.util

/**
 * Pure helper for splitting one expense equally across people.
 *
 * The total is divided in minor units; any remainder cents go to the first
 * participants so the shares always sum back to [totalCents].
 */
object SplitCalculator {

    /**
     * Split [totalCents] across [participantCount] people.
     * Returns per-person shares in order; empty when count <= 0.
     */
    fun equalShares(totalCents: Long, participantCount: Int): List<Long> {
        if (participantCount <= 0 || totalCents <= 0) return emptyList()
        val base = totalCents / participantCount
        val remainder = (totalCents % participantCount).toInt()
        return List(participantCount) { index ->
            if (index < remainder) base + 1 else base
        }
    }
}
