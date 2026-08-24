package dev.lciszewski27.whereismymoney.ui.person

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo
import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Person
import dev.lciszewski27.whereismymoney.ui.components.CurrencyBadge
import dev.lciszewski27.whereismymoney.ui.components.PersonAvatar
import dev.lciszewski27.whereismymoney.ui.theme.MoneySpacing
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    uiState: PersonDetailUiState,
    onEvent: (PersonDetailUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var splitPayoffDebtId by remember { mutableStateOf<String?>(null) }
    var showEditPersonDialog by remember { mutableStateOf(false) }

    // ── Edit Person Dialog ───────────────────────────────────────────
    if (showEditPersonDialog && uiState.person != null) {
        var name by remember { mutableStateOf(uiState.person.name) }
        var colorSeed by remember { mutableStateOf(uiState.person.colorSeed) }

        AlertDialog(
            onDismissRequest = { showEditPersonDialog = false },
            title = { Text("Edit Person") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(MoneySpacing.md)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column {
                        Text("Avatar Color", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(MoneySpacing.xs))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MoneySpacing.sm)
                        ) {
                            PersonAvatar(name = name, colorSeed = colorSeed, size = 56.dp)
                            Button(
                                onClick = { colorSeed = System.currentTimeMillis() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                shape = MaterialTheme.shapes.medium
                            ) { Text("Shuffle Color") }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(PersonDetailUiEvent.UpdatePerson(name, colorSeed))
                        showEditPersonDialog = false
                    },
                    enabled = name.isNotBlank()
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditPersonDialog = false }) { Text("Cancel") }
            }
        )
    }

    // ── Partial Payoff Dialog ────────────────────────────────────────
    if (splitPayoffDebtId != null) {
        val debt = uiState.debts.find { it.id == splitPayoffDebtId }
        if (debt != null) {
            val currencySymbol = CurrencyInfo.fromCode(debt.currency).symbol
            var payoffCents by remember { mutableStateOf(0L) }
            var amountText by remember { mutableStateOf("") }

            LaunchedEffect(payoffCents) {
                if (parseInputToCents(amountText) != payoffCents) {
                    amountText = if (payoffCents == 0L) "" else (payoffCents / 100.0).toString()
                }
            }

            AlertDialog(
                onDismissRequest = { splitPayoffDebtId = null },
                title = {
                    Text("Partial Payoff",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(MoneySpacing.md)) {
                        // ── Original Debt Card (clearly visible always) ──
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(MoneySpacing.md)) {
                                Text(
                                    text = debt.description.ifBlank {
                                        if (debt.type == DebtType.THEY_OWE_ME) "They Owe Me" else "I Owe Them"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(MoneySpacing.xxs))
                                Text(
                                    text = "Original amount: ${debt.amountCents / 100.0}$currencySymbol",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (debt.type == DebtType.THEY_OWE_ME)
                                        MaterialTheme.colorScheme.tertiary
                                    else
                                        MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        // ── Visual Slider ────────────────────────────────
                        Column {
                            val sliderValue = payoffCents.toFloat() / debt.amountCents.toFloat()
                            Slider(
                                value = sliderValue.coerceIn(0f, 1f),
                                onValueChange = { payoffCents = (it * debt.amountCents).toLong() },
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("0%", style = MaterialTheme.typography.labelSmall)
                                Text("50%", style = MaterialTheme.typography.labelSmall)
                                Text("100%", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MoneySpacing.xs)) {
                            listOf(0.25f, 0.5f, 0.75f, 1f).forEach { percent ->
                                val label = if (percent == 1f) "Full" else "${(percent * 100).toInt()}%"
                                AssistChip(
                                    onClick = { payoffCents = (debt.amountCents * percent).toLong() },
                                    label = { Text(label) },
                                    modifier = Modifier.weight(1f),
                                    colors = AssistChipDefaults.assistChipColors(
                                        labelColor = if (payoffCents == (debt.amountCents * percent).toLong())
                                            MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = amountText,
                            onValueChange = {
                                amountText = it.filter { c -> c.isDigit() || c == '.' || c == ',' }
                                payoffCents = parseInputToCents(amountText).coerceIn(0, debt.amountCents)
                            },
                            label = { Text("Amount paid") },
                            placeholder = { Text("0.00") },
                            suffix = { Text(currencySymbol) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(MoneySpacing.md).fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Paying", style = MaterialTheme.typography.labelSmall)
                                        Text("${payoffCents / 100.0}$currencySymbol",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.tertiary)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Remaining", style = MaterialTheme.typography.labelSmall)
                                        Text("${(debt.amountCents - payoffCents) / 100.0}$currencySymbol",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Spacer(Modifier.height(MoneySpacing.sm))
                                // ── Progress bar visual ──────────────────────
                                val progress = if (debt.amountCents > 0)
                                    payoffCents.toFloat() / debt.amountCents.toFloat() else 0f
                                androidx.compose.material3.LinearProgressIndicator(
                                    progress = { progress.coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = MoneySpacing.xxs),
                                    color = MaterialTheme.colorScheme.tertiary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (payoffCents > 0) {
                                onEvent(PersonDetailUiEvent.PartialSettle(debt.id, payoffCents))
                                splitPayoffDebtId = null
                            }
                        },
                        enabled = payoffCents > 0,
                        shape = MaterialTheme.shapes.medium
                    ) { Text("Confirm Payoff") }
                },
                dismissButton = {
                    TextButton(onClick = { splitPayoffDebtId = null }) { Text("Cancel") }
                },
                shape = MaterialTheme.shapes.extraLarge
            )
        }
    }

    // ── Delete Confirmation Dialog ───────────────────────────────────
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Person") },
            text = {
                Text("Are you sure you want to delete this person and all related transactions? " +
                        "This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(PersonDetailUiEvent.DeletePerson)
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    // ── Scaffold ─────────────────────────────────────────────────────
    Scaffold(
        modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    if (uiState.person != null) {
                        Column {
                            Text(uiState.person.name, fontWeight = FontWeight.Black)
                            Text("Person Overview",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(PersonDetailUiEvent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Delete Person") },
                            onClick = { showMenu = false; showDeleteConfirm = true },
                            leadingIcon = {
                                Icon(Icons.Default.DeleteOutline, null,
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(PersonDetailUiEvent.AddDebt) },
                shape = MaterialTheme.shapes.large,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) { Icon(Icons.Filled.Add, contentDescription = "Add debt") }
        }
    ) { innerPadding ->
        val person = uiState.person

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(MoneySpacing.md)
        ) {
            // Profile Header
            item {
                if (person != null) {
                    ProfileHeader(
                        person = person,
                        netCents = uiState.netCents,
                        netCurrency = uiState.netCurrency,
                        activeCount = uiState.debts.count { !it.isSettled },
                        onEditClick = { showEditPersonDialog = true }
                    )
                }
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = MoneySpacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(MoneySpacing.sm)
                ) {
                    Button(
                        onClick = { onEvent(PersonDetailUiEvent.SettleAll) },
                        modifier = Modifier.weight(1.2f),
                        shape = MaterialTheme.shapes.large,
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Filled.DoneAll, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(MoneySpacing.xs))
                        Text("Settle All", fontWeight = FontWeight.Bold)
                    }
                    FilledTonalButton(
                        onClick = { onEvent(PersonDetailUiEvent.SendReminder) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.large,
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Filled.Share, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(MoneySpacing.xs))
                        Text("Remind")
                    }
                }
            }

            // History Section Header
            item {
                Column(modifier = Modifier.padding(horizontal = MoneySpacing.lg)) {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = MoneySpacing.xs)
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }

            if (uiState.debts.isEmpty()) {
                item { EmptyDebtsPlaceholder() }
            } else {
                items(uiState.debts, key = { it.id }) { debt ->
                    DebtItem(
                        debt = debt,
                        onToggleSettled = { onEvent(PersonDetailUiEvent.ToggleSettled(debt.id)) },
                        onDelete = { onEvent(PersonDetailUiEvent.DeleteDebt(debt.id)) },
                        onSplitPayoff = { splitPayoffDebtId = debt.id },
                        onEditDebt = { onEvent(PersonDetailUiEvent.EditDebt(debt.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    person: Person,
    netCents: Long,
    netCurrency: String,
    activeCount: Int,
    onEditClick: () -> Unit
) {
    val currencySymbol = CurrencyInfo.fromCode(netCurrency).symbol
    val isPositive = netCents > 0
    val isNegative = netCents < 0

    val balanceColor = when {
        isPositive -> MaterialTheme.colorScheme.tertiary
        isNegative -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MoneySpacing.xs, bottom = MoneySpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            PersonAvatar(
                name = person.name,
                colorSeed = person.colorSeed,
                size = 120.dp
            )
            FilledTonalIconButton(
                onClick = { onEditClick() },
                modifier = Modifier.size(36.dp),
                shape = CircleShape
            ) { Icon(Icons.Default.Edit, null, Modifier.size(18.dp)) }
        }

        Spacer(Modifier.height(MoneySpacing.lg))

        Text(
            text = "Total Balance",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "${if (netCents < 0) "-" else ""}" +
                    "${abs(netCents) / 100}." +
                    "${(abs(netCents) % 100).toString().padStart(2, '0')}$currencySymbol",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = balanceColor
        )

        Spacer(Modifier.height(MoneySpacing.xs))

        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = CircleShape,
            modifier = Modifier.padding(horizontal = MoneySpacing.md)
        ) {
            Text(
                text = "$activeCount active transaction${if (activeCount != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = MoneySpacing.md, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun DebtItem(
    debt: Debt,
    onToggleSettled: () -> Unit,
    onDelete: () -> Unit,
    onSplitPayoff: () -> Unit,
    onEditDebt: () -> Unit
) {
    val currencySymbol = CurrencyInfo.fromCode(debt.currency).symbol
    val sign = if (debt.type == DebtType.THEY_OWE_ME) "+" else "-"

    val amountColor = when {
        debt.isSettled -> MaterialTheme.colorScheme.outline
        debt.type == DebtType.THEY_OWE_ME -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    var showItemMenu by remember { mutableStateOf(false) }

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showItemMenu = true }
            .padding(horizontal = MoneySpacing.xs),
        headlineContent = {
            Text(
                text = debt.description.ifBlank {
                    if (debt.type == DebtType.THEY_OWE_ME) "Received" else "Borrowed"
                },
                fontWeight = if (debt.isSettled) FontWeight.Normal else FontWeight.Bold,
                color = if (debt.isSettled) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, null, Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.width(MoneySpacing.xxs))
                Text(
                    text = formatTimestamp(debt.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!debt.isSettled) {
                    IconButton(onClick = onSplitPayoff) {
                        Icon(Icons.Default.Payments, contentDescription = "Partial Payoff",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$sign${debt.amountCents / 100}." +
                                "${(debt.amountCents % 100).toString().padStart(2, '0')}$currencySymbol",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = amountColor
                    )
                    if (debt.isSettled) {
                        Text("Settled",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold)
                    }
                }

                Box {
                    IconButton(onClick = { showItemMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Item actions",
                            modifier = Modifier.size(20.dp))
                    }
                    DropdownMenu(
                        expanded = showItemMenu,
                        onDismissRequest = { showItemMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = { showItemMenu = false; onEditDebt() },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = { showItemMenu = false; onDelete() },
                            leadingIcon = { Icon(Icons.Default.Delete, null,
                                tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            }
        },
        leadingContent = {
            IconButton(onClick = onToggleSettled) {
                Icon(
                    if (debt.isSettled) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = if (debt.isSettled) MaterialTheme.colorScheme.tertiary
                           else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
private fun EmptyDebtsPlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(MoneySpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.MoneyOff, contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(MoneySpacing.sm))
        Text("No transactions yet",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatTimestamp(epoch: Long): String {
    val fmt = SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    return fmt.format(Date(epoch))
}

private fun parseInputToCents(input: String): Long {
    val normalized = input.replace(',', '.')
    val parts = normalized.split(".")
    return when {
        parts.size == 1 -> (normalized.toLongOrNull() ?: 0L) * 100
        parts.size == 2 -> {
            val major = parts[0].toLongOrNull() ?: 0L
            val minor = parts[1].take(2).padEnd(2, '0').toLongOrNull() ?: 0L
            major * 100 + minor
        }
        else -> 0L
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonDetailPreview() {
    WhereIsMyMoneyTheme {
        PersonDetailScreen(
            uiState = PersonDetailUiState(
                person = Person("1", "Alice Johnson", 123456, System.currentTimeMillis()),
                debts = listOf(
                    Debt("d1", "1", 25000L, "USD", DebtType.THEY_OWE_ME, "Dinner",
                        System.currentTimeMillis(), null, false),
                    Debt("d2", "1", 10000L, "EUR", DebtType.I_OWE_THEM, "Books",
                        System.currentTimeMillis(), System.currentTimeMillis(), false),
                    Debt("d3", "1", 5000L, "PLN", DebtType.THEY_OWE_ME, "Coffee",
                        System.currentTimeMillis(), null, true)
                ),
                netCents = 15000L,
                netCurrency = "USD",
                isLoading = false
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PersonDetailPreviewDark() {
    WhereIsMyMoneyTheme(darkTheme = true) {
        PersonDetailScreen(
            uiState = PersonDetailUiState(
                person = Person("1", "Bob Smith", 789012, System.currentTimeMillis()),
                debts = emptyList(),
                netCents = 0L,
                netCurrency = "EUR",
                isLoading = false
            ),
            onEvent = {}
        )
    }
}