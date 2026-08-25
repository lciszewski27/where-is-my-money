package dev.lciszewski27.whereismymoney.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lciszewski27.whereismymoney.ui.settings.components.SettingsMenuItem
import dev.lciszewski27.whereismymoney.ui.settings.pages.AppearanceSettingsPage
import dev.lciszewski27.whereismymoney.ui.settings.pages.BackupSettingsPage
import dev.lciszewski27.whereismymoney.ui.settings.pages.CategoriesSettingsPage
import dev.lciszewski27.whereismymoney.ui.settings.pages.AboutSettingsPage
import dev.lciszewski27.whereismymoney.ui.settings.pages.CurrencySettingsPage
import dev.lciszewski27.whereismymoney.ui.settings.pages.ExchangeRatesSettingsPage
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(SettingsPage.MAIN) }

    BackHandler(enabled = currentPage != SettingsPage.MAIN) {
        currentPage = SettingsPage.MAIN
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentPage) {
                            SettingsPage.MAIN -> "Settings"
                            SettingsPage.APPEARANCE -> "Appearance"
                            SettingsPage.CURRENCY -> "Currency"
                            SettingsPage.EXCHANGE_RATES -> "Exchange Rates"
                            SettingsPage.BACKUP -> "Backup & Data"
                            SettingsPage.ABOUT -> "About"
                            SettingsPage.CATEGORIES -> "Categories"
                        },
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentPage == SettingsPage.MAIN) {
                                onEvent(SettingsUiEvent.NavigateBack)
                            } else {
                                currentPage = SettingsPage.MAIN
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "settings_page_transition"
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                when (page) {
                    SettingsPage.MAIN -> MainSettingsPage(onNavigate = { currentPage = it })
                    SettingsPage.APPEARANCE -> AppearanceSettingsPage(uiState, onEvent)
                    SettingsPage.CURRENCY -> CurrencySettingsPage(uiState, onEvent)
                    SettingsPage.EXCHANGE_RATES -> ExchangeRatesSettingsPage(uiState, onEvent)
                    SettingsPage.BACKUP -> BackupSettingsPage(onEvent)
                    SettingsPage.ABOUT -> AboutSettingsPage(onEvent)
                    SettingsPage.CATEGORIES -> CategoriesSettingsPage()
                }
            }
        }
    }
}

@Composable
private fun MainSettingsPage(onNavigate: (SettingsPage) -> Unit) {
    val groupedSettings = remember {
        listOf(
            SettingsGroup(
                items = listOf(
                    SettingsItem(
                        "Categories",
                        "Manage debt categories",
                        Icons.Filled.Category,
                        SettingsPage.CATEGORIES
                    ),
                    SettingsItem(
                        "Appearance",
                        "Theme, colors, and animations",
                        Icons.Filled.Palette,
                        SettingsPage.APPEARANCE
                    )
                )
            ),
            SettingsGroup(
                items = listOf(
                    SettingsItem(
                        "Currency",
                        "Change primary currency",
                        Icons.Filled.Payments,
                        SettingsPage.CURRENCY
                    ),
                    SettingsItem(
                        "Exchange Rates",
                        "Set custom conversion rates",
                        Icons.Filled.CurrencyExchange,
                        SettingsPage.EXCHANGE_RATES
                    ),
                )
            ),
            SettingsGroup(
                items = listOf(
                    SettingsItem(
                        "Backup & Data",
                        "Export or import your data",
                        Icons.Filled.Backup,
                        SettingsPage.BACKUP
                    ),
                    SettingsItem(
                        "About",
                        "App info and contributor",
                        Icons.Filled.Favorite,
                        SettingsPage.ABOUT
                    )
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        groupedSettings.forEach { group ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                group.items.forEachIndexed { index, item ->
                    SettingsMenuItem(
                        title = item.title,
                        icon = item.icon,
                        subtitle = item.subtitle,
                        onClick = { onNavigate(item.page) },
                        index = index,
                        count = group.items.size
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Where is my money? v1.1",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    WhereIsMyMoneyTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                primaryCurrency = "USD",
                dynamicColorEnabled = true,
                darkThemeMode = ThemeMode.AUTO
            ),
            onEvent = {}
        )
    }
}
