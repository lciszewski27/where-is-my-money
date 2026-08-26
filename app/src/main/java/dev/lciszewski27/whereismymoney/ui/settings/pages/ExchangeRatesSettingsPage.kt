package dev.lciszewski27.whereismymoney.ui.settings.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiEvent
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiState
import dev.lciszewski27.whereismymoney.ui.settings.components.AddExchangeRateRow

@Composable
internal fun ExchangeRatesSettingsPage(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Set local exchange rates for multi-currency conversion. Falls back to 1:1 when no rate is set.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        uiState.exchangeRates.forEach { rate ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1 ${rate.fromCurrency} = ${rate.rate} ${rate.toCurrency}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    onEvent(
                        SettingsUiEvent.RemoveExchangeRate(
                            rate.fromCurrency,
                            rate.toCurrency
                        )
                    )
                }) {
                    Icon(
                        Icons.Filled.Backup,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (uiState.exchangeRates.isEmpty()) {
            Text(
                "No custom rates configured.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

        HorizontalDivider()
        AddExchangeRateRow { from, to, rate ->
            onEvent(
                SettingsUiEvent.AddExchangeRate(
                    from,
                    to,
                    rate
                )
            )
        }
    }
}
