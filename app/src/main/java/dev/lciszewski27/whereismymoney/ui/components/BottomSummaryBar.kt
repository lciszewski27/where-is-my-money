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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

/**
 * Expressive Bottom Summary Bar showing 3 distinct metrics:
 * 1. They Owe Me (receivables - green/positive tone)
 * 2. I Owe Them (payables - red/negative tone)
 * 3. Net Balance (final result)
 *
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
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 8.dp,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // 3 metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                // They Owe Me
                MetricItem(
                    label = "They Owe Me",
                    amountCents = summary.totalReceivablesCents,
                    currencySymbol = currencySymbol,
                    positive = true,
                    modifier = Modifier.weight(1f)
                )

                // Divider
                Text(
                    text = "|",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // I Owe Them
                MetricItem(
                    label = "I Owe Them",
                    amountCents = summary.totalPayablesCents,
                    currencySymbol = currencySymbol,
                    positive = false,
                    modifier = Modifier.weight(1f)
                )

                // Divider
                Text(
                    text = "|",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Net Balance
                MetricItem(
                    label = "Net",
                    amountCents = summary.netBalanceCents,
                    currencySymbol = currencySymbol,
                    positive = summary.netBalanceCents >= 0,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Active debts count
            AnimatedVisibility(
                visible = summary.totalActiveDebts > 0,
                enter = fadeIn(spring()) + expandVertically(spring()),
                exit = fadeOut(spring()) + shrinkVertically(spring())
            ) {
                Text(
                    text = "${summary.totalActiveDebts} active debt${if (summary.totalActiveDebts != 1) "s" else ""} • ${summary.activeCurrencies.size} currency${if (summary.activeCurrencies.size != 1) "ies" else "y"}",
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
    positive: Boolean,
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
        Spacer(modifier = Modifier.height(2.dp))
        val sign = if (amountCents >= 0) "" else "-"
        val abs = kotlin.math.abs(amountCents)
        val major = abs / 100
        val minor = abs % 100
        val displayText = "$sign$major.${minor.toString().padStart(2, '0')}$currencySymbol"

        val textColor = if (amountCents == 0L) {
            MaterialTheme.colorScheme.onSurface
        } else if (positive) {
            androidx.compose.ui.graphics.Color(0xFF2E7D32)
        } else {
            androidx.compose.ui.graphics.Color(0xFFC62828)
        }

        AnimatedAmountText(
            amountCents = amountCents,
            currencySymbol = currencySymbol,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}