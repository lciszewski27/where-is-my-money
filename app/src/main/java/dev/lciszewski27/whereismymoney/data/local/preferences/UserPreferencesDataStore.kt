package dev.lciszewski27.whereismymoney.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

/**
 * Encapsulates all user-facing preferences persisted via Jetpack DataStore.
 *
 * - [primaryCurrency]: 3-letter ISO code, e.g. "PLN", "EUR", "USD"
 * - [dynamicColorEnabled]: user toggle to enable/disable Material You dynamic color
 * - [darkThemeEnabled]: user preference for dark/light/auto mode
 */
class UserPreferencesDataStore(private val context: Context) {

    private object Keys {
        val PRIMARY_CURRENCY = stringPreferencesKey("primary_currency")
        val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
        val DARK_THEME_ENABLED =
            stringPreferencesKey("dark_theme_enabled") // "auto" | "light" | "dark"
        val AMOLED_MODE_ENABLED = booleanPreferencesKey("amoled_mode_enabled")
        val ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val COLOR_PRESET = stringPreferencesKey("color_preset")
        val APP_FONT = stringPreferencesKey("app_font") // "quicksand" | "system"
        val DEFAULT_DEBT_TYPE = stringPreferencesKey("default_debt_type") // DebtType.name
        val DEFAULT_CATEGORY_ID = stringPreferencesKey("default_category_id") // "" = none
        val PERSON_SORT_ORDER = stringPreferencesKey("person_sort_order") // "name"|"balance"|"recent"
        val CONFIRM_BEFORE_SETTLE = booleanPreferencesKey("confirm_before_settle")
        val START_SCREEN = stringPreferencesKey("start_screen") // "dashboard" | "stats"
    }

    val primaryCurrency: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.PRIMARY_CURRENCY] ?: defaultCurrencyCode()
    }

    val dynamicColorEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DYNAMIC_COLOR_ENABLED] ?: true
    }

    val darkThemeEnabled: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.DARK_THEME_ENABLED] ?: "auto"
    }

    val amoledModeEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.AMOLED_MODE_ENABLED] ?: false
    }

    val animationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ANIMATIONS_ENABLED] ?: true
    }

    val colorPreset: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.COLOR_PRESET] ?: "DEFAULT"
    }

    val appFont: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.APP_FONT] ?: "quicksand"
    }

    val defaultDebtType: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_DEBT_TYPE] ?: "THEY_OWE_ME"
    }

    val defaultCategoryId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_CATEGORY_ID] ?: ""
    }

    val personSortOrder: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.PERSON_SORT_ORDER] ?: "name"
    }

    val confirmBeforeSettle: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.CONFIRM_BEFORE_SETTLE] ?: true
    }

    val startScreen: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.START_SCREEN] ?: "dashboard"
    }

    suspend fun setPrimaryCurrency(code: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.PRIMARY_CURRENCY] = code
        }
    }

    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DYNAMIC_COLOR_ENABLED] = enabled
        }
    }

    suspend fun setDarkThemeEnabled(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_THEME_ENABLED] = mode // "auto", "light", "dark"
        }
    }

    suspend fun setAmoledModeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AMOLED_MODE_ENABLED] = enabled
        }
    }

    suspend fun setAnimationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ANIMATIONS_ENABLED] = enabled
        }
    }

    suspend fun setColorPreset(preset: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.COLOR_PRESET] = preset
        }
    }

    suspend fun setAppFont(font: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.APP_FONT] = font
        }
    }

    suspend fun setDefaultDebtType(typeName: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_DEBT_TYPE] = typeName
        }
    }

    suspend fun setDefaultCategoryId(categoryId: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_CATEGORY_ID] = categoryId
        }
    }

    suspend fun setPersonSortOrder(order: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.PERSON_SORT_ORDER] = order
        }
    }

    suspend fun setConfirmBeforeSettle(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CONFIRM_BEFORE_SETTLE] = enabled
        }
    }

    suspend fun setStartScreen(screen: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.START_SCREEN] = screen
        }
    }

    companion object {
        /** Used when the system locale has no usable currency. */
        const val FALLBACK_CURRENCY_CODE = "USD"

        /**
         * Default currency derived from the system locale (e.g. EUR on a
         * German device), falling back to USD when the locale has no
         * currency (e.g. no country is set).
         * Only applies until the user picks a currency themselves.
         */
        fun defaultCurrencyCode(): String {
            return try {
                java.util.Currency.getInstance(java.util.Locale.getDefault())
                    ?.currencyCode
                    ?.takeIf { it.isNotBlank() }
                    ?: FALLBACK_CURRENCY_CODE
            } catch (e: Exception) {
                FALLBACK_CURRENCY_CODE
            }
        }
    }
}
