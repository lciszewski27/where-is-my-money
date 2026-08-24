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

import androidx.compose.material3.MediumTopAppBar
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars

import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.CircleShape

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
    var showNewPersonDialog by remember { mutableStateOf(false) }

    if (showNewPersonDialog) {
        var newPersonName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewPersonDialog = false },
            title = { Text("New Person", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter the name of the new person.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newPersonName,
                        onValueChange = { newPersonName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPersonName.isNotBlank()) {
                            onEvent(DashboardUiEvent.CreatePerson(newPersonName))
                            showNewPersonDialog = false
                        }
                    },
                    enabled = newPersonName.isNotBlank(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewPersonDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = MaterialTheme.shapes.extraLarge
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
                onClick = { showNewPersonDialog = true },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add person")
            }
        },

        bottomBar = {
            BottomSummaryBar(
                summary = uiState.summary
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        val currencySymbol = CurrencyInfo.fromCode(uiState.summary.primaryCurrency).symbol

        if (uiState.persons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    isFiltered = uiState.searchQuery.isNotEmpty() || uiState.filterType != DebtFilterType.ALL
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = 16.dp,
                    bottom = 100.dp
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
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val count = uiState.persons.size
                        Text(
                            text = "$count person${if (count != 1) "s" else ""}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(uiState.persons, key = { it.id }) { person ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 }
                    ) {
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

    Box {
        LargeTopAppBar(
            title = {
                if (!isSearchActive) {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text("Where is", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
                        Text("my money?", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.displaySmall)
                    }
                }
            },
            actions = {
                if (!isSearchActive) {
                    IconButton(onClick = { isSearchActive = true }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )

        if (isSearchActive) {
            Surface(
                modifier = Modifier.fillMaxWidth().height(64.dp), // Standard TopAppBar height
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = { keyboardController?.hide() },
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("Search people...") },
                    leadingIcon = { 
                        IconButton(onClick = { 
                            isSearchActive = false
                            onClearSearch()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = onClearSearch) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    colors = SearchBarDefaults.colors(
                        containerColor = Color.Transparent
                    )
                ) {}
            }
        }
    }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "Created ${formatTimestamp(person.createdAt)}",
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
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${if (person.balanceCents < 0) "-" else ""}${abs(person.balanceCents) / 100}.${(abs(person.balanceCents) % 100).toString().padStart(2, '0')}$currencySymbol",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = balanceColor
                )
            }
            
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
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
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    if (isFiltered) Icons.Default.Search else Icons.Outlined.PersonOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        Text(
            text = if (isFiltered) "No results found" else "Your list is empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = if (isFiltered) "Try adjusting your search or filters to find what you're looking for." 
                   else "Tap the + button to add your first person and start tracking debts.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
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
