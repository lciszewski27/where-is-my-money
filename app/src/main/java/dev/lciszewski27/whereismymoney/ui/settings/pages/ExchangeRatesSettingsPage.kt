package dev.lciszewski27.whereismymoney.ui.settings.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.domain.model.ExchangeRate
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiEvent
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiState
import dev.lciszewski27.whereismymoney.ui.settings.components.AddExchangeRateRow
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme

/** Compact rate rendering: up to 4 decimals, no trailing zeros. */
private fun formatRate(rate: Double): String =
    "%.4f".format(rate).trimEnd('0').trimEnd('.').ifEmpty { "0" }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExchangeRatesSettingsPage(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit
) {
    var rateToEdit by remember { mutableStateOf<ExchangeRate?>(null) }

    // ── Edit rate dialog ─────────────────────────────────────────
    val editing = rateToEdit
    if (editing != null) {
        var rateText by remember(editing) { mutableStateOf(formatRate(editing.rate)) }
        val parsed = rateText.toDoubleOrNull()
        val valid = parsed != null && parsed > 0 && parsed.isFinite()
        AlertDialog(
            onDismissRequest = { rateToEdit = null },
            title = { Text("Edit rate") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1 ${editing.fromCurrency} equals how many ${editing.toCurrency}?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = it },
                        label = { Text("Rate") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        supportingText = {
                            if (!valid && rateText.isNotBlank()) {
                                Text(
                                    "Enter a positive number",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        isError = !valid && rateText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amount = rateText.toDoubleOrNull()
                        if (amount != null && amount > 0 && amount.isFinite()) {
                            onEvent(
                                SettingsUiEvent.AddExchangeRate(
                                    editing.fromCurrency,
                                    editing.toCurrency,
                                    amount
                                )
                            )
                            rateToEdit = null
                        }
                    },
                    enabled = valid
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { rateToEdit = null }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // ── Section Header ──────────────────────────────────────────
        Text(
            "Exchange Rates",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Local rates for multi-currency conversion. " +
                    "Tap a rate to edit it. Falls back to 1:1 when no rate is set.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
        )

        // ── No Rates State ──────────────────────────────────────────
        if (uiState.exchangeRates.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.CurrencyExchange,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No custom rates configured.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Add a rate below for automatic conversion.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            // ── Rate List ───────────────────────────────────────────
            uiState.exchangeRates.forEachIndexed { index, rate ->
                SegmentedListItem(
                    onClick = { rateToEdit = rate },
                    shapes = ListItemDefaults.segmentedShapes(
                        index = index,
                        count = uiState.exchangeRates.size
                    ),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    leadingContent = {
                        Icon(
                            Icons.Filled.CurrencyExchange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "1 ${rate.fromCurrency} = ${formatRate(rate.rate)} ${rate.toCurrency}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "1 ${rate.toCurrency} ≈ ${formatRate(1.0 / rate.rate)} ${rate.fromCurrency}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    onEvent(
                                        SettingsUiEvent.RemoveExchangeRate(
                                            rate.fromCurrency,
                                            rate.toCurrency
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Remove ${rate.fromCurrency}/${rate.toCurrency}",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Divider ──────────────────────────────────────────────────
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        Spacer(Modifier.height(4.dp))

        // ── Add Rate Section ─────────────────────────────────────────
        AddExchangeRateRow { from, to, rate ->
            onEvent(SettingsUiEvent.AddExchangeRate(from, to, rate))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExchangeRatesSettingsPagePreview() {
    WhereIsMyMoneyTheme {
        ExchangeRatesSettingsPage(
            uiState = SettingsUiState(
                primaryCurrency = "USD",
                exchangeRates = listOf(
                    ExchangeRate("USD", "EUR", 0.92),
                    ExchangeRate("USD", "PLN", 4.05)
                )
            ),
            onEvent = {}
        )
    }
}
