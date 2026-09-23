# 💸 Where is my money?

**A private, offline-first Android app for tracking shared expenses, IOUs, and debts with friends and family.**

![Kotlin](https://img.shields.io/badge/kotlin-%230095D5.svg?style=flat-square&logo=kotlin&logoColor=white)
![Android API](https://img.shields.io/badge/API-31%2B-brightgreen.svg?style=flat-square&logo=android)
![Material Design 3](https://img.shields.io/badge/UI-Material_3_Expressive-blueviolet.svg?style=flat-square)
![License](https://img.shields.io/github/license/lciszewski27/where-is-my-money?style=flat-square)

Track who owes you, who you owe, and see the big picture — all stored locally on your device.
No accounts, no sync, no analytics.

| Dashboard | Add Debt | Details | Settings |
| :---: | :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/lciszewski27/where-is-my-money/refs/heads/main/assets/dashboard.png" width="200"/> | <img src="https://raw.githubusercontent.com/lciszewski27/where-is-my-money/refs/heads/main/assets/add_debt.png" width="200"/> | <img src="https://raw.githubusercontent.com/lciszewski27/where-is-my-money/refs/heads/main/assets/details.png" width="200"/> | <img src="https://raw.githubusercontent.com/lciszewski27/where-is-my-money/refs/heads/main/assets/settings.png" width="200"/> |

---

## ✨ Features

*   🔒 **Privacy first:** all data lives in a local Room database. No accounts, no sync, no analytics. The app's only network use is loading contributor avatars (via Coil) on the About screen.
*   📊 **Smart dashboard:** at-a-glance receivables, payables, and net balance. Search people instantly and filter by *All*, *They Owe Me*, or *I Owe*.
*   🗂️ **Expandable bottom drawer:** drag up from the summary bar to reveal upcoming repayments (sorted by due date, overdue flagged) and chronological activity history. Closes via drag, outside tap, top-bar tap, or system back.
*   👤 **Intuitive contacts:** swipe right for *They Owe Me*, swipe left for *I Owe Them*. Deterministic color avatars per person; rename or delete people (deleting cascades to their debts).
*   💱 **Multi-currency:** 17 built-in currencies, all amounts stored as `Long` in minor units. Default currency is taken from the system locale (USD fallback). Add custom local exchange rates for cross-currency totals.
*   📅 **Due dates & repayments:** optional due date per debt with overdue highlighting.
*   🏷️ **Categories:** organize debts with custom categories (name + color), manageable from Settings.
*   ✅ **Frictionless settlements:** toggle individual debts as paid, *Settle All* to clear a person's balance in one tap, or record a **partial payoff** via slider, quick-percentage chips, or exact amount — the original debt is marked settled and a new debt with the remaining amount is created, so history and stats stay intact.
*   📈 **Statistics screen:** overview cards, active-vs-settled distribution, and a monthly debt trend chart.
*   🔔 **Share reminders:** compose and send an Android share intent with an outstanding-balance reminder directly to your contact.
*   🎨 **Modern UI/UX:** Material 3 Expressive (morphing buttons, wavy progress, segmented lists) with Material You dynamic colors, light/dark/system themes, an AMOLED black mode, 4 color presets, Quicksand/system font choice, and an animations toggle.
*   💾 **Local backups:** export and import full JSON backups (people, debts, categories) via Android's Storage Access Framework (SAF).
*   📜 **About & open source:** contributor list plus a license screen generated at build time from the actual dependency graph (AboutLibraries plugin).

## 🔒 Privacy

* No accounts, no cloud sync, no analytics or crash reporting.
* Debts, people, categories, preferences, and backups never leave your device unless **you** export or share them.
* The manifest declares `INTERNET` solely so Coil can fetch contributor avatar images on the About screen.

## 🛠 Tech Stack & Architecture

Built with modern Android development principles, focusing on a lean APK and traceable dependencies.

*   **Architecture:** Clean Architecture (`data`, `domain`, `ui`) & single-activity.
*   **Dependency injection:** manual DI via the `Application` class (`WhereIsMyMoneyApp`) — no Dagger/Hilt/Koin.
*   **UI:** Jetpack Compose (Material 3 Expressive) with Coil image loading.
*   **Navigation:** Navigation Compose with type-safe `@Serializable` routes (`Dashboard`, `PersonDetail(personId)`, `Settings`, `Stats`). Add/edit debt is a `ModalBottomSheet` hosted by the NavHost, not a route.
*   **State:** per-screen immutable `UiState` + `StateFlow`, `UiEvent` sealed interfaces, one-shot effects via `SharedFlow`.
*   **Database:** Room (KSP) with reactive `Flow`-based DAOs. Entities: `Person`, `Debt` (FK → person, `CASCADE` on delete), `Category`. Entities are also `@Serializable` so the same models feed the JSON backup. `fallbackToDestructiveMigration()` — no schema migrations (local-only app).
*   **Storage & prefs:** Jetpack DataStore Preferences (`primaryCurrency`, `dynamicColor`, theme mode, AMOLED, animations, color preset, app font) & `kotlinx.serialization` (routes + backups).
*   **Licenses:** AboutLibraries Gradle plugin generates the in-app OSS license screen at build time.
*   **Build:** Gradle with Kotlin DSL & Version Catalog (`gradle/libs.versions.toml`).

| Property | Value |
|---|---|
| `applicationId` | `dev.lciszewski27.whereismymoney` |
| `minSdk` / `targetSdk` / `compileSdk` | 31 / 37 / 37 |
| Kotlin / JVM target | 2.4.20 / 17 (AGP 9.4.1) |
| Key deps | Room, Navigation Compose, DataStore, kotlinx.serialization, Coil, AboutLibraries |

### Project structure

```
app/src/main/java/.../whereismymoney/
├── data/        # Room (AppDatabase, DAOs, entities), BackupService,
│                # UserPreferencesDataStore, DebtRepositoryImpl
├── domain/      # Pure-Kotlin models (Person, Debt, Category, CurrencyInfo,
│                # ExchangeRate, DashboardSummary, StatsSummary, Contributor),
│                # repository interface, use cases
└── ui/          # Compose screens: dashboard/, adddebt/ (bottom sheet),
                 # person/, stats/, settings/ (+ pages/), components/,
                 # navigation/ (type-safe routes + NavHost), theme/
```

## 🚀 Build & Run

### Prerequisites

*   A recent Android Studio version (AGP 9.x is required)
*   JDK 17
*   An Android device/emulator running API 31+

### Quick start

```bash
# 1. Clone the repository
git clone https://github.com/lciszewski27/where-is-my-money.git
cd where-is-my-money

# 2. Build the debug APK
./gradlew assembleDebug

# 3. Run on a connected device
./gradlew installDebug
```

---

## 🤝 Contributing

Issues and pull requests are welcome. Please keep the manual-DI, offline-first design: don't add DI frameworks, network permissions, or cloud features unless they're explicitly part of the proposal, and be careful changing Room entity fields — they affect both the database schema and the JSON backup format.

## 📄 License

This project is licensed under the **GNU General Public License v3.0** — see [LICENSE](./LICENSE). The app's Settings → About screen additionally lists the licenses of all open source dependencies used at build time.
