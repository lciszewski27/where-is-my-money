package dev.lciszewski27.whereismymoney.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo
import dev.lciszewski27.whereismymoney.domain.model.DashboardSummary
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Person
import dev.lciszewski27.whereismymoney.ui.components.BottomSummaryBar
import dev.lciszewski27.whereismymoney.ui.components.PersonAvatar
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onEvent: (DashboardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        snapAnimationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
    )
    var showNewContactDialog by remember { mutableStateOf(false) }

    if (showNewContactDialog) {
        var newContactName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewContactDialog = false },
            title = { Text("New Contact") },
            text = {
                Column {
                    Text("Enter the name of the new contact.")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newContactName.isNotBlank()) {
                            onEvent(DashboardUiEvent.CreatePerson(newContactName))
                            showNewContactDialog = false
                        }
                    },
                    enabled = newContactName.isNotBlank()
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewContactDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DashboardTopAppBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { onEvent(DashboardUiEvent.Search(it)) },
                onClearSearch = { onEvent(DashboardUiEvent.ClearSearch) },
                onOpenSettings = { onEvent(DashboardUiEvent.OpenSettings) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewContactDialog = true },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add contact")
            }
        },
        bottomBar = {
            BottomSummaryBar(
                summary = uiState.summary
            )
        }
    ) { innerPadding ->

        val currencySymbol = CurrencyInfo.fromCode(uiState.summary.primaryCurrency).symbol

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = 16.dp,
                bottom = 88.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Debt type filter chips
            item {
                DebtFilterChips(
                    selected = uiState.filterType,
                    onSelect = { onEvent(DashboardUiEvent.SetFilter(it)) }
                )
            }

            // Person count header
            item {
                Text(
                    text = "${uiState.persons.size} contact${if (uiState.persons.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                )
            }

            if (uiState.persons.isEmpty()) {
                item {
                    EmptyState(
                        isFiltered = uiState.searchQuery.isNotEmpty() || uiState.filterType != DebtFilterType.ALL,
                        modifier = Modifier.fillParentMaxSize(0.7f)
                    )
                }
            } else {
                items(uiState.persons, key = { it.id }) { person ->
                    SwipeablePersonCard(
                        person = person,
                        currencySymbol = currencySymbol,
                        onClick = { onEvent(DashboardUiEvent.OpenPerson(person.id)) },
                        onQuickAdd = { type -> onEvent(DashboardUiEvent.QuickAddDebt(person.id, type)) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior
) {
    var isSearchActive by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LargeTopAppBar(
        title = {
            if (isSearchActive) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = { keyboardController?.hide() },
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("Search contacts...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = onClearSearch) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {}
            } else {
                Column {
                    Text("Where is", fontWeight = FontWeight.Black)
                    Text("my money?", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        actions = {
            IconButton(onClick = { isSearchActive = !isSearchActive }) {
                Icon(
                    if (isSearchActive) Icons.Filled.Close else Icons.Filled.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    )
}

@Composable
private fun DebtFilterChips(
    selected: DebtFilterType,
    onSelect: (DebtFilterType) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        SegmentedButton(
            selected = selected == DebtFilterType.ALL,
            onClick = { onSelect(DebtFilterType.ALL) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
            label = { Text("All") }
        )
        SegmentedButton(
            selected = selected == DebtFilterType.THEY_OWE_ME,
            onClick = { onSelect(DebtFilterType.THEY_OWE_ME) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
            icon = {
                SegmentedButtonDefaults.Icon(selected == DebtFilterType.THEY_OWE_ME) {
                    Icon(Icons.Outlined.ArrowDownward, null, Modifier.size(18.dp))
                }
            },
            label = { Text("They Owe") }
        )
        SegmentedButton(
            selected = selected == DebtFilterType.I_OWE_THEM,
            onClick = { onSelect(DebtFilterType.I_OWE_THEM) },
            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
            icon = {
                SegmentedButtonDefaults.Icon(selected == DebtFilterType.I_OWE_THEM) {
                    Icon(Icons.Outlined.ArrowUpward, null, Modifier.size(18.dp))
                }
            },
            label = { Text("I Owe") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeablePersonCard(
    person: Person,
    currencySymbol: String,
    onClick: () -> Unit,
    onQuickAdd: (DebtType) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd) {
                onQuickAdd(DebtType.THEY_OWE_ME)
            } else if (value == SwipeToDismissBoxValue.EndToStart) {
                onQuickAdd(DebtType.I_OWE_THEM)
            }
            false // Reset position
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Color(0xFF2E7D32).copy(alpha = 0.8f) // Green
                SwipeToDismissBoxValue.EndToStart -> Color(0xFFC62828).copy(alpha = 0.8f) // Red
                else -> Color.Transparent
            }
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Outlined.ArrowDownward
                SwipeToDismissBoxValue.EndToStart -> Icons.Outlined.ArrowUpward
                else -> null
            }
            val label = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> "They Owe Me"
                SwipeToDismissBoxValue.EndToStart -> "I Owe Them"
                else -> ""
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large)
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                if (icon != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (direction == SwipeToDismissBoxValue.StartToEnd) {
                            Icon(icon, null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text(label, color = Color.White, fontWeight = FontWeight.Bold)
                        } else {
                            Text(label, color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Icon(icon, null, tint = Color.White)
                        }
                    }
                }
            }
        },
        content = {
            PersonCard(
                person = person,
                currencySymbol = currencySymbol,
                onClick = onClick
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PersonCard(
    person: Person,
    currencySymbol: String,
    onClick: () -> Unit
) {
    val isPositive = person.balanceCents > 0
    val isNegative = person.balanceCents < 0
    val balanceColor = when {
        isPositive -> Color(0xFF2E7D32)
        isNegative -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersonAvatar(
                name = person.name,
                colorSeed = person.colorSeed,
                size = 56.dp
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = person.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Added ${formatTimestamp(person.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (person.balanceCents == 0L) "Settled" 
                           else if (isPositive) "Owes you" 
                           else "You owe",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "${if (person.balanceCents < 0) "-" else ""}${abs(person.balanceCents) / 100}.${(abs(person.balanceCents) % 100).toString().padStart(2, '0')}$currencySymbol",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = balanceColor
                )
            }
            
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(
    isFiltered: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (isFiltered) Icons.Default.Search else Icons.Outlined.PersonOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = if (isFiltered) "No results found" else "No contacts yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isFiltered) "Try adjusting your search or filters" 
                   else "Tap the + button to add your first transaction",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatTimestamp(epoch: Long): String {
    if (epoch == 0L) return "just now"
    val javaText = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    return javaText.format(Date(epoch))
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    WhereIsMyMoneyTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                persons = listOf(
                    Person("1", "Alice Johnson", 123, System.currentTimeMillis()),
                    Person("2", "Bob Smith", 456, System.currentTimeMillis()),
                    Person("3", "Charlie Brown", 789, System.currentTimeMillis())
                ),
                summary = DashboardSummary(
                    totalReceivablesCents = 12500L,
                    totalPayablesCents = 3400L,
                    netBalanceCents = 9100L,
                    primaryCurrency = "USD",
                    totalActiveDebts = 5
                )
            ),
            onEvent = {}
        )
    }
}
