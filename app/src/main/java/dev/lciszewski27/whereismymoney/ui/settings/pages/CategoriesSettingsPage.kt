package dev.lciszewski27.whereismymoney.ui.settings.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lciszewski27.whereismymoney.WhereIsMyMoneyApp
import dev.lciszewski27.whereismymoney.domain.model.Category
import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.ui.components.rememberSeedColorRole
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme
import kotlinx.coroutines.launch

/**
 * Category manager suited to how categories are actually used.
 *
 * Each row shows its deterministic seed color, how many active debts use
 * it, and an overflow menu (rename / delete) instead of a bare delete
 * icon — renaming preserves history, deleting never touches debts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoriesSettingsPage() {
    val context = LocalContext.current
    val app = context.applicationContext as WhereIsMyMoneyApp
    val repository = app.repository
    val scope = rememberCoroutineScope()

    // ── State ────────────────────────────────────────────────────────
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var debts by remember { mutableStateOf<List<Debt>>(emptyList()) }
    var newCategoryName by remember { mutableStateOf("") }
    var showAddField by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var categoryToRename by remember { mutableStateOf<Category?>(null) }
    var menuForCategoryId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // ── Load categories + debts on first composition ─────────────────
    LaunchedEffect(Unit) {
        launch {
            repository.observeCategories().collect { list ->
                categories = list
                isLoading = false
            }
        }
        launch {
            repository.observeActiveDebts().collect { list ->
                debts = list
            }
        }
    }

    val activeCountByCategory = remember(debts) {
        debts.groupingBy { it.categoryId }.eachCount()
    }

    // ── Delete confirmation dialog ───────────────────────────────────
    val pendingDelete = categoryToDelete
    if (pendingDelete != null) {
        val usage = activeCountByCategory[pendingDelete.id] ?: 0
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = {
                Text(
                    "Delete category?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete \"${pendingDelete.name}\"? " +
                            if (usage > 0) {
                                "$usage active debt(s) use it — they will keep their amounts but lose the label."
                            } else {
                                "No debts use it, so nothing else changes."
                            },
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.deleteCategory(pendingDelete.id)
                            categoryToDelete = null
                        }
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.extraLarge
        )
    }

    // ── Rename dialog ────────────────────────────────────────────────
    val pendingRename = categoryToRename
    if (pendingRename != null) {
        var renameText by remember(pendingRename) { mutableStateOf(pendingRename.name) }
        AlertDialog(
            onDismissRequest = { categoryToRename = null },
            title = {
                Text(
                    "Rename category",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Category name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (renameText.isNotBlank()) {
                                scope.launch {
                                    repository.insertCategory(
                                        pendingRename.copy(name = renameText.trim())
                                    )
                                    categoryToRename = null
                                }
                            }
                        }
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.insertCategory(
                                pendingRename.copy(name = renameText.trim())
                            )
                            categoryToRename = null
                        }
                    },
                    enabled = renameText.isNotBlank() &&
                            renameText.trim() != pendingRename.name
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { categoryToRename = null }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.extraLarge
        )
    }

    // ── Content ──────────────────────────────────────────────────────
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            "Debt Categories",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Organize debts with labels. Renaming keeps history; " +
                    "deleting never touches your debts.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
        )

        // ── Category List ───────────────────────────────────────────
        if (categories.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CategoryIcon(name = "?", colorSeed = 0L, size = 48.dp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No categories yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Add your first category below",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            categories.forEachIndexed { index, category ->
                val usage = activeCountByCategory[category.id] ?: 0
                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(
                        index = index,
                        count = categories.size
                    ),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    leadingContent = {
                        CategoryIcon(
                            name = category.name,
                            colorSeed = category.colorSeed,
                            size = 40.dp
                        )
                    },
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (usage > 0) {
                                        "$usage active debt${if (usage != 1) "s" else ""}"
                                    } else {
                                        "Unused"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box {
                                IconButton(
                                    onClick = { menuForCategoryId = category.id }
                                ) {
                                    Icon(
                                        Icons.Filled.MoreVert,
                                        contentDescription = "Options for ${category.name}",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = menuForCategoryId == category.id,
                                    onDismissRequest = { menuForCategoryId = null }
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            menuForCategoryId = null
                                            categoryToRename = category
                                        },
                                        text = { Text("Rename") }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            menuForCategoryId = null
                                            categoryToDelete = category
                                        },
                                        text = {
                                            Text(
                                                "Delete",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Add Category Section ────────────────────────────────────
        AnimatedVisibility(
            visible = showAddField,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category name") },
                    placeholder = { Text("e.g. Food, Rent, Utilities") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newCategoryName.isNotBlank()) {
                                scope.launch {
                                    repository.insertCategory(
                                        Category(
                                            id = java.util.UUID.randomUUID().toString(),
                                            name = newCategoryName.trim()
                                        )
                                    )
                                    newCategoryName = ""
                                    showAddField = false
                                }
                            }
                        }
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        showAddField = false
                        newCategoryName = ""
                    }) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    FilledTonalButton(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                scope.launch {
                                    repository.insertCategory(
                                        Category(
                                            id = java.util.UUID.randomUUID().toString(),
                                            name = newCategoryName.trim()
                                        )
                                    )
                                    newCategoryName = ""
                                    showAddField = false
                                }
                            }
                        },
                        enabled = newCategoryName.isNotBlank(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Add Category")
                    }
                }
            }
        }

        // ── Add Button (when field is hidden) ───────────────────────
        if (!showAddField) {
            FilledTonalButton(
                onClick = { showAddField = true },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Add Category")
            }
        }
    }
}

/**
 * Circular category badge with the category's deterministic seed color
 * and its first letter — the same visual language as person avatars.
 */
@Composable
private fun CategoryIcon(
    name: String,
    colorSeed: Long,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val role = rememberSeedColorRole(colorSeed)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(role.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "?",
            color = role.content,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.45).sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriesSettingsPagePreview() {
    WhereIsMyMoneyTheme {
        CategoriesSettingsPage()
    }
}
