package dev.lciszewski27.whereismymoney.ui.settings.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiEvent
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiState
import dev.lciszewski27.whereismymoney.ui.settings.components.PrimaryCurrencySelector

@Composable
internal fun CurrencySettingsPage(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PrimaryCurrencySelector(
            selectedCurrency = uiState.primaryCurrency,
            onCurrencySelected = { onEvent(SettingsUiEvent.SetPrimaryCurrency(it)) }
        )
        Text(
            text = "All totals will be shown in the selected primary currency.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
