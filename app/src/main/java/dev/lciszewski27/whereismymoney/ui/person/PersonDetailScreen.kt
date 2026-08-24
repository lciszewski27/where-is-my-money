package dev.lciszewski27.whereismymoney.ui.person

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.domain.model.CurrencyInfo
import dev.lciszewski27.whereismymoney.domain.model.Debt
import dev.lciszewski27.whereismymoney.domain.model.DebtType
import dev.lciszewski27.whereismymoney.domain.model.Person
import dev.lciszewski27.whereismymoney.domain.usecase.PersonDetailData
import dev.lciszewski27.whereismymoney.ui.components.CurrencyBadge
import dev.lciszewski27.whereismymoney.ui.components.PersonAvatar
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.abs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    uiState: PersonDetailUiState,
    onEvent: (PersonDetailUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    if (uiState.person != null) {
                        Column {
                            Text(uiState.person.name, fontWeight = FontWeight.Black)
                            Text(
                                "Profile Details", 
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
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
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete Contact") },
                            onClick = {
                                showMenu = false
                                // onEvent(PersonDetailUiEvent.DeletePerson) // Need to add this event
                            },
                            leadingIcon = { Icon(Icons.Default.DeleteOutline, null, tint = MaterialTheme.colorScheme.error) }
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
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add debt")
            }
        }
    ) { innerPadding ->
        val person = uiState.person

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Profile Header ────────────────────────────────────────
            item {
                if (person != null) {
                    ProfileHeader(
                        person = person,
                        netCents = uiState.netCents,
                        netCurrency = uiState.netCurrency,
                        activeCount = uiState.debts.count { !it.isSettled }
                    )
                }
            }

            // ── Quick Actions ─────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onEvent(PersonDetailUiEvent.SettleAll) },
                        modifier = Modifier.weight(1.2f),
                        shape = MaterialTheme.shapes.extraLarge,
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Filled.DoneAll, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Settle All", fontWeight = FontWeight.Bold)
                    }
                    FilledTonalButton(
                        onClick = { onEvent(PersonDetailUiEvent.SendReminder) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.extraLarge,
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Filled.Share, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Remind")
                    }
                }
            }

            // ── History Section ───────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }

            if (uiState.debts.isEmpty()) {
                item {
                    EmptyDebtsPlaceholder()
                }
            } else {
                items(uiState.debts, key = { it.id }) { debt ->
                    DebtItem(
                        debt = debt,
                        onToggleSettled = { onEvent(PersonDetailUiEvent.ToggleSettled(debt.id)) },
                        onDelete = { onEvent(PersonDetailUiEvent.DeleteDebt(debt.id)) }
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
    activeCount: Int
) {
    val currencySymbol = CurrencyInfo.fromCode(netCurrency).symbol
    val isPositive = netCents > 0
    val isNegative = netCents < 0
    
    val balanceColor = when {
        isPositive -> Color(0xFF2E7D32)
        isNegative -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            PersonAvatar(
                name = person.name,
                colorSeed = person.colorSeed,
                size = 120.dp
            )
            FilledTonalIconButton(
                onClick = { /* TODO: Change avatar or color */ },
                modifier = Modifier.size(36.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
            }
        }
        
        Spacer(Modifier.height(20.dp))
        
        Text(
            text = "Total Balance",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "${if (netCents < 0) "-" else ""}${abs(netCents) / 100}.${(abs(netCents) % 100).toString().padStart(2, '0')}$currencySymbol",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = balanceColor
        )
        
        Spacer(Modifier.height(8.dp))
        
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = CircleShape,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "$activeCount active transaction${if (activeCount != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun DebtItem(
    debt: Debt,
    onToggleSettled: () -> Unit,
    onDelete: () -> Unit
) {
    val currencySymbol = CurrencyInfo.fromCode(debt.currency).symbol
    val sign = if (debt.type == DebtType.THEY_OWE_ME) "+" else "-"
    
    val amountColor = when {
        debt.isSettled -> MaterialTheme.colorScheme.outline
        debt.type == DebtType.THEY_OWE_ME -> Color(0xFF2E7D32)
        else -> MaterialTheme.colorScheme.error
    }

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        headlineContent = {
            Text(
                text = debt.description.ifBlank { if (debt.type == DebtType.THEY_OWE_ME) "Received" else "Borrowed" },
                fontWeight = if (debt.isSettled) FontWeight.Normal else FontWeight.Bold,
                color = if (debt.isSettled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = formatTimestamp(debt.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$sign${debt.amountCents / 100}.${(debt.amountCents % 100).toString().padStart(2, '0')}$currencySymbol",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = amountColor
                )
                if (debt.isSettled) {
                    Text(
                        "Settled",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        leadingContent = {
            IconButton(onClick = onToggleSettled) {
                Icon(
                    if (debt.isSettled) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = if (debt.isSettled) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun EmptyDebtsPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.MoneyOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No transactions yet",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatTimestamp(epoch: Long): String {
    val fmt = SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    return fmt.format(Date(epoch))
}

@Preview(showBackground = true)
@Composable
private fun PersonDetailPreview() {
    WhereIsMyMoneyTheme {
        PersonDetailScreen(
            uiState = PersonDetailUiState(
                person = Person("1", "Alice Johnson", 123456, System.currentTimeMillis()),
                debts = listOf(
                    Debt("d1", "1", 25000L, "USD", DebtType.THEY_OWE_ME, "Dinner", System.currentTimeMillis(), null, false),
                    Debt("d2", "1", 10000L, "EUR", DebtType.I_OWE_THEM, "Books", System.currentTimeMillis(), System.currentTimeMillis(), false),
                    Debt("d3", "1", 5000L, "PLN", DebtType.THEY_OWE_ME, "Coffee", System.currentTimeMillis(), null, true)
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