# Refactor Settings Screen into multiple files

The current `SettingsScreen.kt` file is becoming quite large (over 700 lines). This refactoring will move each sub-menu (page) into its own file and extract reusable settings components to a dedicated file.

## Proposed Changes

### [Settings Components]

#### [NEW] [SettingsComponents.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/settings/components/SettingsComponents.kt)
Extract reusable UI elements used across different settings pages:
- `SettingsMenuItem`
- `SettingsToggle`
- `PrimaryCurrencySelector`
- `AddExchangeRateRow`
- `SimpleCurrencyDropdown`

### [Settings Pages]

Each sub-menu will be moved to its own file in the `pages` package.

#### [NEW] [AppearanceSettingsPage.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/settings/pages/AppearanceSettingsPage.kt)
Contains `AppearanceSettingsPage` composable.

#### [NEW] [CurrencySettingsPage.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/settings/pages/CurrencySettingsPage.kt)
Contains `CurrencySettingsPage` composable.

#### [NEW] [ExchangeRatesSettingsPage.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/settings/pages/ExchangeRatesSettingsPage.kt)
Contains `ExchangeRatesSettingsPage` composable.

#### [NEW] [BackupSettingsPage.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/settings/pages/BackupSettingsPage.kt)
Contains `BackupSettingsPage` composable.

#### [NEW] [ContributorsSettingsPage.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/settings/pages/ContributorsSettingsPage.kt)
Contains `ContributorsSettingsPage` composable.

#### [NEW] [CategoriesSettingsPage.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/settings/pages/CategoriesSettingsPage.kt)
Contains `CategoriesSettingsPage` composable.

### [Main Settings Screen]

#### [MODIFY] [SettingsScreen.kt](file:///home/lukasz/AndroidStudioProjects/Whereismymoney/app/src/main/java/dev/lciszewski27/whereismymoney/ui/settings/SettingsScreen.kt)
- Remove the extracted composables.
- Import the new composables from `dev.lciszewski27.whereismymoney.ui.settings.pages` and `dev.lciszewski27.whereismymoney.ui.settings.components`.
- Update `SettingsPage` enum, `SettingsGroup`, and `SettingsItem` to be visible to the new files (change to `internal`).

## Verification Plan

### Manual Verification
- Verify that the Settings screen still functions correctly.
- Check each sub-menu to ensure it displays and behaves as before (theme changes, currency selection, adding exchange rates, etc.).
- Ensure "Back" navigation still works.
