package dev.lciszewski27.whereismymoney.ui.settings.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.ui.settings.PersonSortOrder
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiEvent
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiState
import dev.lciszewski27.whereismymoney.ui.settings.components.SettingsToggle
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme

private fun DebtType.displayName(): String = when (this) {
    DebtType.THEY_OWE_ME -> "They owe me"
    DebtType.I_OWE_THEM -> "I owe them"
}

/**
 * General behavior settings: new-debt defaults, list sorting, start
 * screen, and destructive-action confirmations. Every row is a
 * [SegmentedListItem] in a visually connected group, matching the
 * Appearance and Exchange Rates pages.
 */
@Composable
internal fun GeneralSettingsPage(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            "New Debts",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        // ── Default debt type ─────────────────────────────────────
        SegmentedListItem(
            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            leadingContent = { Text("Default direction") },
            content = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEvent(SettingsUiEvent.ToggleDefaultTypeDropdown) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Box {
                        Text(
                            text = debtTypeDisplayName(uiState.defaultDebtTypeName),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        DropdownMenu(
                            expanded = uiState.isDefaultTypeDropdownExpanded,
                            onDismissRequest = { onEvent(SettingsUiEvent.DismissDefaultTypeDropdown) }
                        ) {
                            DebtType.entries.forEach { type ->
                                DropdownMenuItem(
                                    onClick = {
                                        onEvent(SettingsUiEvent.SetDefaultDebtType(type.name))
                                        onEvent(SettingsUiEvent.DismissDefaultTypeDropdown)
                                    },
                                    text = {
                                        Text(
                                            text = type.displayName(),
                                            fontWeight = if (uiState.defaultDebtTypeName == type.name) {
                                                FontWeight.Bold
                                            } else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )

        // ── Default category ──────────────────────────────────────
        SegmentedListItem(
            shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            leadingContent = { Text("Default category") },
            content = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEvent(SettingsUiEvent.ToggleDefaultCategoryDropdown) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Box {
                        val selectedName = uiState.categories
                            .firstOrNull { it.id == uiState.defaultCategoryId }?.name
                            ?: "None"
                        Text(
                            text = selectedName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        DropdownMenu(
                            expanded = uiState.isDefaultCategoryDropdownExpanded,
                            onDismissRequest = { onEvent(SettingsUiEvent.DismissDefaultCategoryDropdown) }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    onEvent(SettingsUiEvent.SetDefaultCategory(""))
                                    onEvent(SettingsUiEvent.DismissDefaultCategoryDropdown)
                                },
                                text = {
                                    Text(
                                        text = "None",
                                        fontWeight = if (uiState.defaultCategoryId.isEmpty()) {
                                            FontWeight.Bold
                                        } else FontWeight.Normal
                                    )
                                }
                            )
                            uiState.categories.forEach { category ->
                                DropdownMenuItem(
                                    onClick = {
                                        onEvent(SettingsUiEvent.SetDefaultCategory(category.id))
                                        onEvent(SettingsUiEvent.DismissDefaultCategoryDropdown)
                                    },
                                    text = {
                                        Text(
                                            text = category.name,
                                            fontWeight = if (uiState.defaultCategoryId == category.id) {
                                                FontWeight.Bold
                                            } else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )

        Text(
            "Lists",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp)
        )

        // ── Person sort order ─────────────────────────────────────
        SegmentedListItem(
            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 1),
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            leadingContent = { Text("Sort people by") },
            content = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEvent(SettingsUiEvent.ToggleSortOrderDropdown) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Box {
                        Text(
                            text = uiState.personSortOrder.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        DropdownMenu(
                            expanded = uiState.isSortOrderDropdownExpanded,
                            onDismissRequest = { onEvent(SettingsUiEvent.DismissSortOrderDropdown) }
                        ) {
                            PersonSortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    onClick = {
                                        onEvent(SettingsUiEvent.SetPersonSortOrder(order))
                                        onEvent(SettingsUiEvent.DismissSortOrderDropdown)
                                    },
                                    text = {
                                        Text(
                                            text = order.displayName,
                                            fontWeight = if (uiState.personSortOrder == order) {
                                                FontWeight.Bold
                                            } else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )

        Text(
            "Safety",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp)
        )

        SettingsToggle(
            title = "Confirm before Settle All",
            subtitle = "Ask first when clearing a person's balance",
            icon = Icons.Filled.DoneAll,
            checked = uiState.confirmBeforeSettle,
            index = 0,
            count = 1,
            onCheckedChange = { onEvent(SettingsUiEvent.ToggleConfirmBeforeSettle(it)) }
        )
    }
}

private fun debtTypeDisplayName(typeName: String): String = try {
    DebtType.valueOf(typeName).displayName()
} catch (e: IllegalArgumentException) {
    DebtType.THEY_OWE_ME.displayName()
}

@Preview(showBackground = true)
@Composable
private fun GeneralSettingsPagePreview() {
    WhereIsMyMoneyTheme(dynamicColor = false) {
        GeneralSettingsPage(uiState = SettingsUiState(), onEvent = {})
    }
}
