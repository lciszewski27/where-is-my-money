package dev.lciszewski27.whereismymoney.ui.navigation

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.lciszewski27.whereismymoney.WhereIsMyMoneyApp
import dev.lciszewski27.whereismymoney.ui.adddebt.AddDebtSheetContent
import dev.lciszewski27.whereismymoney.ui.adddebt.AddDebtViewModel
import dev.lciszewski27.whereismymoney.ui.dashboard.DashboardScreen
import dev.lciszewski27.whereismymoney.ui.dashboard.DashboardViewModel
import dev.lciszewski27.whereismymoney.ui.person.PersonDetailScreen
import dev.lciszewski27.whereismymoney.ui.person.PersonDetailUiEvent
import dev.lciszewski27.whereismymoney.ui.person.PersonDetailViewModel
import dev.lciszewski27.whereismymoney.ui.settings.SettingsScreen
import dev.lciszewski27.whereismymoney.ui.settings.SettingsUiEvent
import dev.lciszewski27.whereismymoney.ui.settings.SettingsViewModel
import dev.lciszewski27.whereismymoney.ui.contributors.ContributorsScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val app = context.applicationContext as WhereIsMyMoneyApp
    val scope = rememberCoroutineScope()

    // ── Shared Dashboard ViewModel ───────────────────────────────────
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(
                    repository = app.repository,
                    preferences = app.preferences,
                    dashboardSummaryUseCase = app.getDashboardSummaryUseCase
                ) as T
            }
        }
    )
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()

    // ── Add Debt Bottom Sheet state ──────────────────────────────────
    var showAddDebtSheet by remember { mutableStateOf(false) }
    var editingDebtId by remember { mutableStateOf<String?>(null) }
    var initialPersonId by remember { mutableStateOf<String?>(null) }
    var initialDebtType by remember { mutableStateOf<dev.lciszewski27.whereismymoney.domain.model.DebtType?>(null) }

    // Collect navigation events
    LaunchedEffect(Unit) {
        dashboardViewModel.navigateToAddDebt.collect { params ->
            editingDebtId = null
            initialPersonId = params?.first
            initialDebtType = params?.second
            showAddDebtSheet = true
        }
    }
    LaunchedEffect(Unit) {
        dashboardViewModel.navigateToPerson.collect { personId ->
            navController.navigate(Route.PersonDetail(personId))
        }
    }
    LaunchedEffect(Unit) {
        dashboardViewModel.navigateToSettings.collect {
            navController.navigate(Route.Settings)
        }
    }

    // ── Add Debt ViewModel (only when sheet is shown) ────────────────
    val addDebtViewModel: AddDebtViewModel? = if (showAddDebtSheet) {
        viewModel(
            key = "add_debt_${editingDebtId ?: "new"}_${initialPersonId ?: "none"}_${initialDebtType ?: "none"}",
            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return AddDebtViewModel(
                        repository = app.repository,
                        preferences = app.preferences,
                        editDebtId = editingDebtId,
                        initialPersonId = initialPersonId,
                        initialDebtType = initialDebtType
                    ) as T
                }
            }
        )
    } else null

    // Modal Bottom Sheet for Add/Edit Debt
    if (showAddDebtSheet && addDebtViewModel != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val addDebtUiState by addDebtViewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            addDebtViewModel.dismiss.collect {
                sheetState.hide()
                showAddDebtSheet = false
            }
        }

        ModalBottomSheet(
            onDismissRequest = { showAddDebtSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            AddDebtSheetContent(
                uiState = addDebtUiState,
                onEvent = { addDebtViewModel.onEvent(it) }
            )
        }
    }

    // ── Backup/Export launchers ──────────────────────────────────────
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                app.backupService.exportToUri(context, uri)
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                if (json != null) {
                    app.backupService.importFromJson(json)
                }
            }
        }
    }

    // ── Navigation Host ──────────────────────────────────────────────
    NavHost(
        navController = navController,
        startDestination = Route.Dashboard,
        modifier = modifier.fillMaxSize(),
        enterTransition = {
            slideInHorizontally(
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
            ) { it } + fadeIn(animationSpec = spring())
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
            ) { -it / 3 } + fadeOut(animationSpec = spring())
        },
        popEnterTransition = {
            slideInHorizontally(
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
            ) { -it / 3 } + fadeIn(animationSpec = spring())
        },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
            ) { it } + fadeOut(animationSpec = spring())
        }
    ) {
        composable<Route.Dashboard> {
            DashboardScreen(
                uiState = dashboardUiState,
                onEvent = { dashboardViewModel.onEvent(it) }
            )
        }

        composable<Route.PersonDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.PersonDetail>()
            val personDetailViewModel: PersonDetailViewModel = viewModel(
                key = "person_${route.personId}",
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return PersonDetailViewModel(
                            personId = route.personId,
                            repository = app.repository,
                            getPersonDetail = app.getPersonDetailUseCase
                        ) as T
                    }
                }
            )
            val personDetailUiState by personDetailViewModel.uiState.collectAsState()

            // Handle share intent for reminders
            LaunchedEffect(Unit) {
                personDetailViewModel.shareIntent.collect { message ->
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Send Reminder"))
                }
            }

            LaunchedEffect(Unit) {
                personDetailViewModel.navigateBack.collect {
                    navController.popBackStack()
                }
            }

            LaunchedEffect(Unit) {
                personDetailViewModel.navigateToAddDebt.collect { personId ->
                    editingDebtId = null
                    initialPersonId = personId
                    initialDebtType = null
                    showAddDebtSheet = true
                }
            }

            LaunchedEffect(Unit) {
                personDetailViewModel.navigateToEditDebt.collect { debtId ->
                    editingDebtId = debtId
                    initialPersonId = null
                    initialDebtType = null
                    showAddDebtSheet = true
                }
            }

            PersonDetailScreen(
                uiState = personDetailUiState,
                onEvent = { event ->
                    when (event) {
                        PersonDetailUiEvent.NavigateBack -> navController.popBackStack()
                        else -> personDetailViewModel.onEvent(event)
                    }
                }
            )
        }

        composable<Route.Settings> {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return SettingsViewModel(
                            preferences = app.preferences,
                            currencyConversion = app.currencyConversion
                        ) as T
                    }
                }
            )
            val settingsUiState by settingsViewModel.uiState.collectAsState()

            SettingsScreen(
                uiState = settingsUiState,
                onEvent = { event ->
                    when (event) {
                        SettingsUiEvent.NavigateBack -> navController.popBackStack()
                        SettingsUiEvent.ExportBackup -> {
                            exportLauncher.launch("whereismymoney_backup.json")
                        }
                        SettingsUiEvent.ImportBackup -> {
                            importLauncher.launch(arrayOf("application/json"))
                        }
                        SettingsUiEvent.OpenContributors -> {
                            navController.navigate(Route.Contributors)
                        }
                        else -> settingsViewModel.onEvent(event)
                    }
                }
            )
        }

        composable<Route.Contributors> {
            ContributorsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}