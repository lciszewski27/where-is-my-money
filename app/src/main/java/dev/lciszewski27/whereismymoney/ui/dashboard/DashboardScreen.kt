package dev.lciszewski27.whereismymoney.ui.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ripple
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo
import dev.lciszewski27.whereismymoney.domain.model.DashboardSummary
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Person
import dev.lciszewski27.whereismymoney.ui.components.ExpandableBottomDrawer
import dev.lciszewski27.whereismymoney.ui.components.PersonAvatar
import dev.lciszewski27.whereismymoney.ui.theme.LocalAnimationsEnabled
import dev.lciszewski27.whereismymoney.ui.theme.MoneySpacing
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
    val animationsEnabled = LocalAnimationsEnabled.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        snapAnimationSpec = if (animationsEnabled) spring(
            dampingRatio = 0.7f,
            stiffness = 300f
        ) else snap()
    )
    var showNewPersonDialog by remember { mutableStateOf(false) }
    var isDrawerExpanded by remember { mutableStateOf(false) }

    // System back collapses the open drawer instead of leaving the screen.
    // Drag, header tap, and outside-tap (scrim) keep working as before.
    BackHandler(enabled = isDrawerExpanded) { isDrawerExpanded = false }

    // ── New Person Dialog ────────────────────────────────────────────
    if (showNewPersonDialog) {
        var newPersonName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewPersonDialog = false },
            title = {
                Text(
                    "New Person",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "Enter the name of the new person.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(MoneySpacing.md))
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

    // ── Scaffold ─────────────────────────────────────────────────────
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (!isDrawerExpanded) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                else Modifier
            ),
        topBar = {
            Box {
                DashboardTopAppBar(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { onEvent(DashboardUiEvent.Search(it)) },
                    onClearSearch = { onEvent(DashboardUiEvent.ClearSearch) },
                    onOpenSettings = { onEvent(DashboardUiEvent.OpenSettings) },
                    onOpenStats = { onEvent(DashboardUiEvent.OpenStats) },
                    scrollBehavior = scrollBehavior,
                    isDrawerExpanded = isDrawerExpanded,
                    onCloseDrawer = { isDrawerExpanded = false }
                )
                // Same dim as the content scrim while the drawer is open.
                // No click handling here on purpose: taps fall through to
                // the bar's own tap-to-close.
                AnimatedVisibility(
                    visible = isDrawerExpanded,
                    enter = if (animationsEnabled) fadeIn() else EnterTransition.None,
                    exit = if (animationsEnabled) fadeOut() else ExitTransition.None,
                    modifier = Modifier.matchParentSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isDrawerExpanded,
                enter = if (animationsEnabled) fadeIn() else EnterTransition.None,
                exit = if (animationsEnabled) fadeOut() else ExitTransition.None
            ) {
                // M3 Expressive: the FAB squishes its corners while pressed.
                val fabInteractionSource = remember { MutableInteractionSource() }
                val fabPressed by fabInteractionSource.collectIsPressedAsState()
                val fabCorner by animateDpAsState(
                    targetValue = if (fabPressed && animationsEnabled) MoneySpacing.xs
                    else MoneySpacing.md,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
                    label = "fab_corner",
                )
                FloatingActionButton(
                    onClick = { showNewPersonDialog = true },
                    modifier = Modifier.padding(bottom = 120.dp),
                    shape = RoundedCornerShape(fabCorner),
                    interactionSource = fabInteractionSource,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add person")
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        val currencySymbol = CurrencyInfo.fromCode(uiState.summary.primaryCurrency).symbol

        // ── Box overlay: content + floating drawer ─────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Content layer
            Column(modifier = Modifier.fillMaxSize()) {
                // Filter Chips — always visible
                DebtFilterChips(
                    selected = uiState.filterType,
                    onSelect = { onEvent(DashboardUiEvent.SetFilter(it)) },
                    modifier = Modifier.padding(
                        start = MoneySpacing.md, end = MoneySpacing.md, top = MoneySpacing.md
                    )
                )

                if (uiState.persons.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            isFiltered = (uiState.searchQuery.isNotEmpty() ||
                                    uiState.filterType != DebtFilterType.ALL)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        userScrollEnabled = !isDrawerExpanded,
                        contentPadding = PaddingValues(
                            start = MoneySpacing.md, end = MoneySpacing.md,
                            top = MoneySpacing.sm, bottom = 140.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(MoneySpacing.sm)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = MoneySpacing.xxs),
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
                                enter = if (animationsEnabled) {
                                    fadeIn(animationSpec = tween(400)) +
                                            slideInVertically(animationSpec = tween(400)) { it / 2 }
                                } else EnterTransition.None
                            ) {
                                SwipeablePersonCard(
                                    person = person,
                                    currencySymbol = currencySymbol,
                                    onClick = { onEvent(DashboardUiEvent.OpenPerson(person.id)) }
                                ) { type ->
                                    onEvent(
                                        DashboardUiEvent.QuickAddDebt(
                                            person.id,
                                            type
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Scrim overlay
            AnimatedVisibility(
                visible = isDrawerExpanded,
                enter = if (animationsEnabled) fadeIn() else EnterTransition.None,
                exit = if (animationsEnabled) fadeOut() else ExitTransition.None,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { isDrawerExpanded = false }
                )
            }

            // Drawer layer — aligned to bottom, floats over content
            ExpandableBottomDrawer(
                summary = uiState.summary,
                upcomingRepayments = uiState.upcomingRepayments,
                recentActivity = uiState.recentActivity,
                isExpanded = isDrawerExpanded,
                onExpandedChange = { isDrawerExpanded = it },
                onPersonClick = { personId -> onEvent(DashboardUiEvent.OpenPerson(personId)) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// ── Top App Bar ──────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior,
    isDrawerExpanded: Boolean,
    onCloseDrawer: () -> Unit = {}
) {
    var isSearchActive by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState(initialText = searchQuery)

    LaunchedEffect(searchQuery) {
        if (textFieldState.text.toString() != searchQuery) {
            textFieldState.setTextAndPlaceCursorAtEnd(searchQuery)
        }
    }

    LaunchedEffect(textFieldState.text) {
        onSearchQueryChange(textFieldState.text.toString())
    }

    // The top bar sits above the scrim, so while the drawer is open a tap
    // anywhere on the bar (e.g. the big title) collapses the drawer.
    val barTapSource = remember { MutableInteractionSource() }
    val barTapModifier = Modifier.clickable(
        enabled = isDrawerExpanded,
        interactionSource = barTapSource,
        indication = null,
        onClick = onCloseDrawer,
    )

    if (isSearchActive) {
        // ── Search mode: show a compact TopAppBar with the SearchBar inside ──
        TopAppBar(
            modifier = barTapModifier,
            title = {
                SearchBar(
                    state = searchBarState,
                    inputField = {
                        SearchBarDefaults.InputField(
                            textFieldState = textFieldState,
                            searchBarState = searchBarState,
                            onSearch = { keyboardController?.hide() },
                            placeholder = { Text("Search people...") },
                            leadingIcon = {
                                IconButton(
                                    onClick = {
                                        isSearchActive = false
                                        onClearSearch()
                                    },
                                    enabled = !isDrawerExpanded
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            trailingIcon = {
                                if (textFieldState.text.isNotEmpty()) {
                                    IconButton(
                                        onClick = onClearSearch,
                                        enabled = !isDrawerExpanded
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            enabled = !isDrawerExpanded
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    } else {
        // ── Normal mode: LargeTopAppBar with title and action icons ──
        LargeTopAppBar(
            modifier = barTapModifier,
            title = {
                Column {
                    Text(
                        "Where is",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        "my money?",
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            },
            actions = {
                IconButton(onClick = { isSearchActive = true }, enabled = !isDrawerExpanded) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
                IconButton(onClick = onOpenSettings, enabled = !isDrawerExpanded) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
                IconButton(
                    onClick = onOpenStats,
                    enabled = !isDrawerExpanded
                ) {
                    Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Stats")
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )
    }
}

// ── Filter Chips ─────────────────────────────────────────────────────
@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun DebtFilterChips(
    selected: DebtFilterType,
    onSelect: (DebtFilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    // Note: no horizontal padding here — the caller already supplies it.
    // Tight inner padding so icon + label always fit on one line.
    val chipContentPadding = PaddingValues(horizontal = MoneySpacing.xxs)
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // 1. ALL
        ToggleButton(
            modifier = Modifier
                .semantics {
                    role = Role.RadioButton
                }
                .weight(1f),
            checked = selected == DebtFilterType.ALL,
            onCheckedChange = { checked ->
                if (checked) onSelect(DebtFilterType.ALL)
            },
            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
            contentPadding = chipContentPadding,
        ) {
            Text(
                text = "All",
                maxLines = 1
            )
        }

        // 2. THEY_OWE_ME
        ToggleButton(
            modifier = Modifier
                .semantics {
                    role = Role.RadioButton
                }
                .weight(1f),
            checked = selected == DebtFilterType.THEY_OWE_ME,
            onCheckedChange = { checked ->
                if (checked) onSelect(DebtFilterType.THEY_OWE_ME)
            },
            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
            contentPadding = chipContentPadding,
        ) {
            Icon(Icons.Outlined.ArrowDownward, null, Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(ToggleButtonDefaults.IconSpacing))
            Text(
                text = "They Owe",
                maxLines = 1
            )
        }

        // 3. I_OWE_THEM
        ToggleButton(
            modifier = Modifier
                .semantics {
                    role = Role.RadioButton
                }
                .weight(1f),
            checked = selected == DebtFilterType.I_OWE_THEM,
            onCheckedChange = { checked ->
                if (checked) onSelect(DebtFilterType.I_OWE_THEM)
            },
            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
            contentPadding = chipContentPadding,
        ) {
            Icon(Icons.Outlined.ArrowUpward, null, Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(ToggleButtonDefaults.IconSpacing))
            Text(
                text = "I Owe",
                maxLines = 1
            )
        }
    }
}

// ── Swipeable Person Card ────────────────────────────────────────────
@Suppress("DEPRECATION")
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
            false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color = when (direction) {
                SwipeToDismissBoxValue.StartToEnd ->
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)

                SwipeToDismissBoxValue.EndToStart ->
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)

                else -> MaterialTheme.colorScheme.surface
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
                    .padding(horizontal = MoneySpacing.xl),
                contentAlignment = alignment
            ) {
                if (icon != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (direction == SwipeToDismissBoxValue.StartToEnd) {
                            Icon(icon, null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                            Spacer(Modifier.width(MoneySpacing.xs))
                            Text(
                                label, color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                label, color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(MoneySpacing.xs))
                            Icon(icon, null, tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }
        },
        content = {
            PersonCard(person = person, currencySymbol = currencySymbol, onClick = onClick)
        },
        modifier = Modifier.fillMaxWidth()
    )
}

// ── Person Card ──────────────────────────────────────────────────────
@Composable
private fun PersonCard(
    person: Person,
    currencySymbol: String,
    onClick: () -> Unit
) {
    val isPositive = person.balanceCents > 0
    val isNegative = person.balanceCents < 0
    val balanceColor = when {
        isPositive -> MaterialTheme.colorScheme.tertiary
        isNegative -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    // M3 Expressive: the card eases to a tighter corner while pressed.
    val animationsEnabled = LocalAnimationsEnabled.current
    val cardInteractionSource = remember { MutableInteractionSource() }
    val cardPressed by cardInteractionSource.collectIsPressedAsState()
    val cardCorner by animateDpAsState(
        targetValue = if (cardPressed && animationsEnabled) MoneySpacing.sm
        else MoneySpacing.md,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "card_corner",
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = cardInteractionSource,
                indication = ripple(),
                onClick = onClick,
            ),
        shape = RoundedCornerShape(cardCorner),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MoneySpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersonAvatar(
                name = person.name,
                colorSeed = person.colorSeed,
                size = 56.dp
            )
            Spacer(Modifier.width(MoneySpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = person.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                    text = if (person.balanceCents < 0) "-${abs(person.balanceCents) / 100}.${
                        (abs(
                            person.balanceCents
                        ) % 100).toString().padStart(2, '0')
                    }$currencySymbol"
                    else "${abs(person.balanceCents) / 100}.${
                        (abs(person.balanceCents) % 100).toString().padStart(2, '0')
                    }$currencySymbol",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = balanceColor
                )
            }

            Spacer(Modifier.width(MoneySpacing.xs))
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
            .padding(MoneySpacing.xxl),
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
        Spacer(Modifier.height(MoneySpacing.xxl))
        Text(
            text = if (isFiltered) "No results found" else "Your list is empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(MoneySpacing.sm))
        Text(
            text = if (isFiltered) "Try adjusting your search or filters to find what you're looking for."
            else "Tap the + button to add your first person and start tracking debts.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = MoneySpacing.md)
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
                ),
                upcomingRepayments = emptyList(),
                recentActivity = emptyList()
            ),
            onEvent = {}
        )
    }
}