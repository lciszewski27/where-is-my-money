# Where is my money?

> A private, 100 % offline Android app for tracking shared expenses, IOUs, and debts with friends and family.

Track who owes you, who you owe, and see the big picture — all stored locally on your device. No accounts, no cloud, no tracking.

---

## Features

### Dashboard
- **At-a-glance summary** — total receivables, payables, and net balance in your primary currency.
- **Contact list** with per-person balance and quick-swipe actions (swipe right → "They Owe Me", swipe left → "I Owe Them").
- **Search & filter** — find contacts by name; filter by "All", "They Owe", or "I Owe".
- **Bottom summary bar** with animated counters showing live totals.

### Person Detail
- **Profile view** with deterministic colour avatar and net balance.
- **Transaction history** — every debt listed with amount, date, description, and settled status.
- **Toggle settled** — check off individual debts as paid.
- **Settle All** — mark every debt with a person as settled in one tap.
- **Share Reminder** — compose and send an Android share intent with an outstanding-balance reminder.

### Add / Edit Debt
- **Debt type selector** — "They Owe Me" or "I Owe Them".
- **Amount input** with real-time parsing to cents and validation.
- **Multi-currency support** — 17 currencies built in, all stored as minor units.
- **Contact picker** — search existing contacts or create a new one inline.
- **Optional description & due date** — Material 3 date picker.

### Settings
- **Primary currency** — choose the currency all totals are displayed in.
- **Appearance** — Material You dynamic colour toggle, light/ dark/ system theme.
- **Exchange rates** — add and manage custom local exchange rates for cross-currency debt tracking.
- **Backup & Data** — export/ import full JSON backup (no cloud, no sync — fully offline).

### Architecture & Design
- **Clean Architecture** with three layers: `data`, `domain`, `ui`.
- **Single-Activity** with Jetpack Compose Navigation (type-safe `@Serializable` routes).
- **Room** for local persistence with reactive `Flow`-based DAOs.
- **Jetpack DataStore** for user preferences.
- **Manual DI** via the `Application` class (no Dagger/ Hilt — keeps the APK lean and the dependency graph traceable).
- **M3 Design System** — custom colour palette, expressive shapes, and rich typography.

---

## Tech Stack

| Layer         | Technology                                                   |
|---------------|--------------------------------------------------------------|
| Language      | Kotlin                                                       |
| UI            | Jetpack Compose (Material 3)                                 |
| Navigation    | Navigation Compose (type-safe routes with `kotlinx.serialization`) |
| Database      | Room (KSP)                                                   |
| Preferences   | Jetpack DataStore Preferences                                |
| Serialization | `kotlinx.serialization` (JSON routes + backup export)        |
| Build         | Gradle with Kotlin DSL & Version Catalog (`libs.versions.toml`) |
| Min SDK       | 31                                                           |
| Target SDK    | 37                                                           |

---

## Project Structure

```
app/src/main/java/dev/lciszewski27/whereismymoney/
├── WhereIsMyMoneyApp.kt          # Application class — manual DI container
├── MainActivity.kt               # Single Activity — edge-to-edge, theme
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt        # Room database definition
│   │   ├── BackupService.kt      # JSON export/ import
│   │   ├── dao/
│   │   │   ├── PersonDao.kt      # Person CRUD + reactive queries
│   │   │   └── DebtDao.kt        # Debt CRUD + aggregate queries
│   │   ├── entity/
│   │   │   ├── PersonEntity.kt   # Room entity: persons table
│   │   │   └── DebtEntity.kt     # Room entity: debts table (FK → persons)
│   │   └── preferences/
│   │       └── UserPreferencesDataStore.kt
│   └── repository/
│       └── DebtRepositoryImpl.kt # Repository → domain mapping
├── domain/
│   ├── model/
│   │   ├── Person.kt
│   │   ├── Debt.kt
│   │   ├── DebtType.kt           # THEY_OWE_ME / I_OWE_THEM
│   │   ├── CurrencyInfo.kt       # 17 supported currencies
│   │   ├── ExchangeRate.kt
│   │   └── DashboardSummary.kt   # Aggregate presentation model
│   ├── repository/
│   │   └── DebtRepository.kt     # Repository interface
│   └── usecase/
│       ├── GetDashboardSummaryUseCase.kt
│       ├── GetPersonDetailUseCase.kt
│       └── CurrencyConversionUseCase.kt
└── ui/
    ├── components/
    │   ├── PersonAvatar.kt        # Deterministic colour circle avatar
    │   ├── CurrencyBadge.kt       # Small currency symbol badge
    │   ├── AnimatedCounter.kt     # Spring-animated amount display
    │   └── BottomSummaryBar.kt    # Dashboard bottom bar: 3 metrics
    ├── dashboard/
    │   ├── DashboardUiState.kt    # UI state + event sealed interface
    │   ├── DashboardViewModel.kt
    │   └── DashboardScreen.kt
    ├── adddebt/
    │   ├── AddDebtUiState.kt
    │   ├── AddDebtViewModel.kt
    │   └── AddDebtScreen.kt       # Bottom-sheet form
    ├── person/
    │   ├── PersonDetailUiState.kt
    │   ├── PersonDetailViewModel.kt
    │   └── PersonDetailScreen.kt
    ├── settings/
    │   ├── SettingsUiState.kt     # ThemeMode enum
    │   ├── SettingsViewModel.kt
    │   └── SettingsScreen.kt
    ├── navigation/
    │   ├── AppNavigation.kt       # Type-safe Route sealed interface
    │   └── AppNavHost.kt          # NavHost + screen wiring + DI factory
    └── theme/
        ├── Color.kt               # Light/dark palettes + semantic tones
        ├── Shape.kt               # Expressive M3 shapes
        ├── Type.kt                # Full Typography definition
        └── Theme.kt               # WhereIsMyMoneyTheme composable
```

---

## Build & Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- An Android device/ emulator running API 31+

### Steps

```bash
# Clone the repository
git clone https://github.com/your-username/whereismymoney.git
cd Whereismymoney

# Build the debug APK
./gradlew assembleDebug

# Run on a connected device
./gradlew installDebug
```

Or open the project in Android Studio and press **Run** (Shift+F10).

---

## Configuration

### Exchange Rates
Currency conversion is fully local. Add custom rates in **Settings → Exchange Rates**. If no rate is configured for a pair, the engine falls back to 1:1.

### Supported Currencies
PLN, EUR, USD, GBP, CHF, CZK, JPY, CNY, SEK, NOK, DKK, HUF, RON, BGN, TRY, AUD, CAD.

---

## Backups

All data is stored in a local Room database (`whereismymoney.db`). Use **Settings → Backup & Data** to export a JSON backup (saved via Android's `SAF` file picker) or import a previously exported file.

**Data never leaves your device** — there is no cloud sync, no network permission, and no account requirement.

---

## License

```
MIT License

Copyright (c) 2024 dev.lciszewski27

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```