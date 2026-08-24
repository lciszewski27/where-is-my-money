<div align="center">

# 💸 Where is my money?

**A private, 100% offline Android app for tracking shared expenses, IOUs, and debts with friends and family.**

![Kotlin](https://img.shields.io/badge/kotlin-%230095D5.svg?style=flat-square&logo=kotlin&logoColor=white)
![Android API](https://img.shields.io/badge/API-31%2B-brightgreen.svg?style=flat-square&logo=android)
![Material Design 3](https://img.shields.io/badge/UI-Material_3-blueviolet.svg?style=flat-square)
![License](https://img.shields.io/github/license/lciszewski27/where-is-my-money?style=flat-square)

Track who owes you, who you owe, and see the big picture — all stored locally on your device. <br> **No accounts, no cloud, no tracking.**

| Dashboard | Add Debt | Details | Settings |
| :---: | :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/lciszewski27/where-is-my-money/refs/heads/main/assets/dashboard.png" width="200"/> | <img src="https://raw.githubusercontent.com/lciszewski27/where-is-my-money/refs/heads/main/assets/add_debt.png" width="200"/> | <img src="https://raw.githubusercontent.com/lciszewski27/where-is-my-money/refs/heads/main/assets/details.png" width="200"/> | <img src="https://raw.githubusercontent.com/lciszewski27/where-is-my-money/refs/heads/main/assets/settings.png" width="200"/> |

</div>

---

## ✨ Features

*   🔒 **Privacy First:** Data never leaves your device. No cloud sync, no network permission, and no account requirement.
*   📊 **Smart Dashboard:** At-a-glance summaries for total receivables, payables, and net balance. Bottom summary bar features animated live counters.
*   👤 **Intuitive Contacts:** Swipe right for "They Owe Me", swipe left for "I Owe Them". Features deterministic color avatars.
*   💱 **Multi-Currency:** 17 built-in currencies (stored as minor units). Add custom local exchange rates for cross-currency tracking.
*   ✅ **Frictionless Settlements:** Toggle individual debts as paid, or use "Settle All" to clear a person's balance in one tap.
*   🔔 **Share Reminders:** Compose and send an Android share intent with an outstanding-balance reminder directly to your contact.
*   🎨 **Modern UI/UX:** Built with Material You dynamic colors, expressive shapes, and support for light/dark/system themes.
*   💾 **Local Backups:** Export and import full JSON backups via Android's Storage Access Framework (SAF).

## 🛠 Tech Stack & Architecture

Built with modern Android development principles, focusing on a lean APK and traceable dependencies. 

*   **Architecture:** Clean Architecture (`data`, `domain`, `ui`) & Single-Activity.
*   **Dependency Injection:** Manual DI via the `Application` class (No Dagger/Hilt).
*   **UI:** Jetpack Compose (Material 3).
*   **Navigation:** Navigation Compose with type-safe `@Serializable` routes.
*   **Database:** Room (KSP) with reactive `Flow`-based DAOs.
*   **Storage & Prefs:** Jetpack DataStore Preferences & `kotlinx.serialization` (JSON routes + backups).
*   **Build:** Gradle with Kotlin DSL & Version Catalog (`libs.versions.toml`).

## 🚀 Build & Run

### Prerequisites
*   Android Studio Hedgehog (2023.1.1) or newer
*   JDK 17
*   An Android device/emulator running API 31+

### Quick Start

```bash
# 1. Clone the repository
git clone [https://github.com/your-username/whereismymoney.git](https://github.com/your-username/whereismymoney.git)
cd whereismymoney

# 2. Build the debug APK
./gradlew assembleDebug

# 3. Run on a connected device
./gradlew installDebug
