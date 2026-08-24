# agents.md — AI Agent Context for "Where is my money?"

> This file is written **for AI coding agents and LLMs** to understand the project's architecture, conventions, and extension points. It is not a user-facing document.

---

## Project Overview

A single-activity, 100 % offline Android app for tracking IOUs and shared debts. Built with Kotlin + Jetpack Compose + Room. The app uses **manual dependency injection** via the `Application` subclass (no DI framework), keeping the dependency graph explicit and easy to follow for static analysis.

---

## Architecture

### Clean Architecture (3 layers)

```
┌─────────────────────────────────────────────┐
│  ui/ (Jetpack Compose screens, ViewModels)   │
│  └── ViewModel → emits UiEvent → UiState     │
├─────────────────────────────────────────────┤
│  domain/ (use cases, models, repository iface)│
│  └── Pure Kotlin — no Android dependencies    │
├─────────────────────────────────────────────┤
│  data/ (Room, DAOs, entities, repository impl)│
│  └── DebtRepositoryImpl bridges domain ↔ Room │
└─────────────────────────────────────────────┘
```

### Key data flows

1. **Room DAO** emits `Flow<List<Entity>>` → **RepositoryImpl** maps entity↔domain → **UseCase** combines flows → **ViewModel** collects into `StateFlow<UiState>` → **Composable** reads state and renders.

2. **User interaction** → Composable calls `onEvent(UiEvent)` → ViewModel handles event (launches coroutine, calls repo/usecase) → new UiState emitted → UI recomposes.

3. **Edit flows** (e.g. AddDebt) use a **ModalBottomSheet** in the NavHost rather than a separate route. The ViewModel exposes a `dismiss: SharedFlow<Unit>` collected by the host.

### Dependency injection

All wiring lives in `WhereIsMyMoneyApp.onCreate()`:

```kotlin
// data
database = AppDatabase.getInstance(this)
preferences = UserPreferencesDataStore(this)

// domain
currencyConversion = CurrencyConversionUseCase()

// repository
repository = DebtRepositoryImpl(
    personDao = database.personDao(),
    debtDao = database.debtDao(),
    currencyConversion = currencyConversion
)

// use cases
getDashboardSummaryUseCase = GetDashboardSummaryUseCase(repository)
getPersonDetailUseCase = GetPersonDetailUseCase(repository)

// services
backupService = BackupService(personDao, debtDao)
```

ViewModels receive their dependencies via `ViewModelProvider.Factory` lambdas in `AppNavHost.kt`. Each factory reads from `app.*`.

---

## Package Layout (app/src/main/java/.../whereismymoney/)

```
data/
├── local/
│   ├── AppDatabase.kt            # Room @Database (entities: PersonEntity, DebtEntity)
│   ├── BackupService.kt          # kotlinx.serialization export/import
│   ├── dao/
│   │   ├── PersonDao.kt          # Flow-based CRUD + search
│   │   └── DebtDao.kt            # Flow-based CRUD + aggregates + settle
│   ├── entity/
│   │   ├── PersonEntity.kt       # @Entity persons (id, name, colorSeed, createdAt)
│   │   └── DebtEntity.kt         # @Entity debts (FK→persons, amountCents, currency, type, ...)
│   └── preferences/
│       └── UserPreferencesDataStore.kt  # primaryCurrency, dynamicColor, darkTheme
└── repository/
    └── DebtRepositoryImpl.kt     # Maps entity↔domain, computes balances with currency conversion

domain/
├── model/
│   ├── Person.kt                 # Person(id, name, colorSeed, createdAt, balanceCents, currency)
│   ├── Debt.kt                   # Debt(id, personId, amountCents, currency, type, description, ...)
│   ├── DebtType.kt               # enum: THEY_OWE_ME, I_OWE_THEM
│   ├── CurrencyInfo.kt           # ISO code, symbol, minorPerMajor; AVAILABLE list
│   ├── ExchangeRate.kt           # fromCurrency → toCurrency : Double rate
│   └── DashboardSummary.kt       # receivables, payables, net, activeCount, activeCurrencies
├── repository/
│   └── DebtRepository.kt         # Interface: reactive and suspend functions
└── usecase/
    ├── GetDashboardSummaryUseCase.kt    # Wraps repository.observeDashboardSummary()
    ├── GetPersonDetailUseCase.kt       # combine(person, debts) → PersonDetailData
    └── CurrencyConversionUseCase.kt    # Pure engine: convert, setRate, formatAmount

ui/
├── components/
│   ├── PersonAvatar.kt           # Deterministic colour from seed, shows first initial
│   ├── CurrencyBadge.kt          # Small surface with currency symbol
│   ├── AnimatedCounter.kt        # AnimatedContent sliding number transitions
│   └── BottomSummaryBar.kt       # Dashboard floating bottom bar with 3 metrics
├── dashboard/                    # Main screen: person list + filter + search
├── adddebt/                      # Modal bottom sheet form (create/edit)
├── person/                       # Person detail with transaction history
├── settings/                     # Currency, appearance, exchange rates, backup
├── navigation/
│   ├── AppNavigation.kt          # Route sealed interface (@Serializable)
│   └── AppNavHost.kt             # NavHost wiring all screens + bottom sheet
└── theme/
    ├── Color.kt                  # Light + Dark palettes + semantic tone colours
    ├── Shape.kt                  # MoneyShapes: distinct corner radii
    ├── Type.kt                   # Full Typography spec
    └── Theme.kt                  # WhereIsMyMoneyTheme (dynamic color support)
```

---

## Conventions

### Naming
- **Package**: `dev.lciszewski27.whereismymoney`
- **Entities** (`data/`): `*Entity` suffix (e.g. `PersonEntity`, `DebtEntity`)
- **Domain models**: no suffix (e.g. `Person`, `Debt`)
- **UiState classes**: `*UiState` suffix, one per screen
- **Events**: `*UiEvent` sealed interface, one per screen
- **ViewModels**: `*ViewModel` suffix
- **Screens**: `*Screen` composable function
- **Theme**: `WhereIsMyMoneyTheme`

### State management
- Each screen has a **`data class UiState`** (immutable, single source of truth).
- Each ViewModel exposes `val uiState: StateFlow<UiState>` and private `_uiState: MutableStateFlow<UiState>`.
- Navigation events and one-shot effects use `SharedFlow` (not `Channel`).
- Events are defined as a **sealed interface** `UiEvent` inside the UiState file.
- ViewModel functions take `onEvent(event: UiEvent)`, routing via `when` expression.

### Currency model
- All amounts stored as **Long in minor units (cents)** — always.
- `Debt.majorAmount` computed property: `amountCents / 100.0`.
- `CurrencyConversionUseCase.formatAmount()` for display.

### Database
- Room entities are also `@Serializable` for the JSON backup feature.
- Room database uses `fallbackToDestructiveMigration()` — schema migrations are not handled (this is a local-only app).
- Foreign keys: `DebtEntity.personId → PersonEntity.id` with `CASCADE` on delete.

### Navigation
- Type-safe routes: `Route.Dashboard`, `Route.PersonDetail(personId)`, `Route.Settings`.
- Add/Edit debt is a **ModalBottomSheet**, not a route — controlled by a `showAddDebtSheet` boolean in `AppNavHost`.
- ViewModel instances for the sheet are created with unique `key` strings based on editing state.

### Material 3
- The theme uses **custom colour palettes** (defined in `Color.kt`) with a **teal-green primary**.
- **Dynamic colour** (Material You) is supported on Android 12+ and is user-toggleable.
- Custom shapes (`MoneyShapes`) with expressive rounding: `extraLarge = 28.dp`, `large = 20.dp`, etc.
- Full `Typography` definition — all text styles are customised.

---

## Testing

### Current status
- `ExampleUnitTest.kt` — placeholder JUnit test
- `ExampleInstrumentedTest.kt` — placeholder Espresso test

### Unit test patterns to follow
- **Domain layer** is pure Kotlin — easiest to unit test (e.g. `CurrencyConversionUseCase`).
- **Repository** can be tested with Room in-memory database (`Room.inMemoryDatabaseBuilder`).
- **ViewModels** can be tested by observing `uiState` and calling `onEvent()`.

### Instrumented test patterns
- Compose UI tests using `createComposeRule()` and `ComposeTestRule`.
- Navigation tests by starting at `Route.Dashboard`.

---

## Extension Points

### Add a new currency
1. Add a `CurrencyInfo` entry to `CurrencyInfo.AVAILABLE`.
2. No other changes needed — the currency selector and formatting use the list dynamically.

### Add a new screen
1. Define a new `Route` in `AppNavigation.kt`.
2. Create the package: `ui/yourscreen/` with `*UiState.kt`, `*ViewModel.kt`, `*Screen.kt`.
3. Add a `composable<Route.YourRoute>` block in `AppNavHost.kt`.
4. Wire the ViewModel factory using `app.*` dependencies.

### Add a new preference
1. Add a key to `UserPreferencesDataStore.Keys`.
2. Add the `Flow` accessor and `suspend` setter.
3. Surface it in `SettingsUiState` / `SettingsViewModel`.

### Add cloud sync (future)
- The repository interface (`DebtRepository`) would get a remote implementation.
- `BackupService` could be extended for cloud backup.
- Room entities are already `@Serializable`, making network transfer straightforward.

---

## Build Configuration

| Property       | Value                          |
|----------------|--------------------------------|
| `applicationId`| `dev.lciszewski27.whereismymoney` |
| `minSdk`       | 31                             |
| `targetSdk`    | 37                             |
| `compileSdk`   | 37                             |
| Kotlin JVM     | 17                             |
| Java source    | 17                             |
| Compose BOM    | Via version catalog            |

Key dependencies: Room (KSP), Navigation Compose, DataStore Preferences, Kotlinx Serialization.

---

## File Locations

| File                                      | Purpose                                 |
|-------------------------------------------|-----------------------------------------|
| `settings.gradle.kts`                     | Root project name: "Where is my money"  |
| `build.gradle.kts`                        | Root build script                       |
| `app/build.gradle.kts`                    | App module build script                 |
| `app/src/main/AndroidManifest.xml`        | Manifest (not read, presumed standard)  |
| `app/src/main/java/.../WhereIsMyMoneyApp.kt` | DI container                        |
| `app/src/main/java/.../MainActivity.kt`   | Single activity                         |
| `app/src/main/java/.../ui/navigation/AppNavHost.kt` | All screen wiring           |

---

## Notes for Agents

- **Do not** add Dagger/Hilt or Koin unless explicitly requested — the manual DI is intentional for traceability.
- **Do not** add network permissions or cloud features unless explicitly requested.
- **Use `edit` for targeted changes** and `read` first to understand the current code.
- **Room entities** are also data classes used for serialization — be careful changing fields as both Room schema and backup format are affected.
- **The currency conversion engine is purely local** — exchange rates are user-managed, not fetched.