package dev.lciszewski27.whereismymoney.ui.settings.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun CategoriesSettingsPage() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Debt Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Manage your debt types and person-based categories here.",
            style = MaterialTheme.typography.bodyMedium
        )
        // Placeholder for real categories management
        ListItem(
            headlineContent = { Text("Food & Dining") },
            leadingContent = { Icon(Icons.Filled.Category, null) }
        )
        ListItem(
            headlineContent = { Text("Rent & Bills") },
            leadingContent = { Icon(Icons.Filled.Category, null) }
        )
    }
}
