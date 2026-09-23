package dev.lciszewski27.whereismymoney.ui.settings.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiEvent
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiState
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme

/**
 * Primary currency picker as a full selectable list.
 *
 * A dropdown hid all 17 currencies behind one field; a radio list shows
 * every option with its symbol and name, and marks the active primary
 * with a tonal container plus a selected radio — state carried by more
 * than tint alone.
 */
@Composable
internal fun CurrencySettingsPage(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            "Primary Currency",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "All totals are shown in the primary currency. " +
                    "Tap to select.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
        )

        CurrencyInfo.AVAILABLE.forEachIndexed { index, currency ->
            val selected = uiState.primaryCurrency == currency.code
            SegmentedListItem(
                onClick = { onEvent(SettingsUiEvent.SetPrimaryCurrency(currency.code)) },
                shapes = ListItemDefaults.segmentedShapes(
                    index = index,
                    count = CurrencyInfo.AVAILABLE.size
                ),
                colors = ListItemDefaults.colors(
                    containerColor = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    }
                ),
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currency.symbol,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSecondaryContainer,
                            maxLines = 1
                        )
                    }
                },
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currency.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = currency.code,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        RadioButton(
                            selected = selected,
                            onClick = null
                        )
                    }
                }
            )
        }

        Text(
            text = "Exchange rates convert multi-currency debts into this currency. " +
                    "Without a custom rate, conversion falls back to 1:1.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, start = 12.dp, end = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencySettingsPagePreview() {
    WhereIsMyMoneyTheme(dynamicColor = false) {
        CurrencySettingsPage(
            uiState = SettingsUiState(primaryCurrency = "USD"),
            onEvent = {}
        )
    }
}
