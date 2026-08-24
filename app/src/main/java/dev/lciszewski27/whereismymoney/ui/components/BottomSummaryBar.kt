package dev.lciszewski27.whereismymoney.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo
import dev.lciszewski27.whereismymoney.domain.model.DashboardSummary
import dev.lciszewski27.whereismymoney.ui.theme.MoneySpacing

/**
 * Expressive Bottom Summary Bar showing 3 distinct metrics:
 * 1. They Owe Me (receivables — positive/tertiary tone)
 * 2. I Owe Them (payables — error/negative tone)
 * 3. Net Balance (final result)
 *
 * Uses tonal elevation instead of shadow for MD3 depth communication.
 * Fixed at the bottom with elevated M3 Surface and ExtraLarge top corners.
 */
@Composable
fun BottomSummaryBar(
    summary: DashboardSummary,
    modifier: Modifier = Modifier
) {
    val currencySymbol = CurrencyInfo.fromCode(summary.primaryCurrency).symbol

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MoneySpacing.lg, vertical = MoneySpacing.md)
        ) {
            // 3 metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                MetricItem(
                    label = "They Owe Me",
                    amountCents = summary.totalReceivablesCents,
                    currencySymbol = currencySymbol,
                    isPositive = true,
                    modifier = Modifier.weight(1f)
                )

                // Vertical divider
                Text(
                    text = "|",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(horizontal = MoneySpacing.xs)
                )

                MetricItem(
                    label = "I Owe Them",
                    amountCents = summary.totalPayablesCents,
                    currencySymbol = currencySymbol,
                    isPositive = false,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "|",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(horizontal = MoneySpacing.xs)
                )

                MetricItem(
                    label = "Net",
                    amountCents = summary.netBalanceCents,
                    currencySymbol = currencySymbol,
                    isPositive = summary.netBalanceCents >= 0,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(MoneySpacing.xxs))

            // Active debts count — spring animated
            AnimatedVisibility(
                visible = summary.totalActiveDebts > 0,
                enter = fadeIn(spring()) + expandVertically(spring()),
                exit = fadeOut(spring()) + shrinkVertically(spring())
            ) {
                Text(
                    text = "${summary.totalActiveDebts} active debt${if (summary.totalActiveDebts != 1) "s" else ""}" +
                            " • ${summary.activeCurrencies.size} " +
                            "${if (summary.activeCurrencies.size != 1) "currencies" else "currency"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    amountCents: Long,
    currencySymbol: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(MoneySpacing.micro))

        // Use MD3 semantic color roles instead of hardcoded hex colors:
        // - Positive (receivables) → tertiary
        // - Negative (payables, debits) → error
        // - Zero → onSurface
        val textColor = when {
            amountCents == 0L -> MaterialTheme.colorScheme.onSurface
            isPositive -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.error
        }

        AnimatedAmountText(
            amountCents = amountCents,
            currencySymbol = currencySymbol,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}