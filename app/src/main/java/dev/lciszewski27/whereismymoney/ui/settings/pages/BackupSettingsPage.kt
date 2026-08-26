package dev.lciszewski27.whereismymoney.ui.settings.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiEvent

@Composable
internal fun BackupSettingsPage(onEvent: (SettingsUiEvent) -> Unit) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick = { onEvent(SettingsUiEvent.ExportBackup) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Filled.FileUpload, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Export JSON Backup")
        }
        OutlinedButton(
            onClick = { onEvent(SettingsUiEvent.ImportBackup) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Filled.FileDownload, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Import JSON Backup")
        }
        Text(
            text = "All data is stored locally on this device. No cloud sync.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
