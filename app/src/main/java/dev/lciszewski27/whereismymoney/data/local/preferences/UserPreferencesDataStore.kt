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
        val DARK_THEME_ENABLED = stringPreferencesKey("dark_theme_enabled") // "auto" | "light" | "dark"
    }

    val primaryCurrency: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.PRIMARY_CURRENCY] ?: "PLN"
    }

    val dynamicColorEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DYNAMIC_COLOR_ENABLED] ?: true
    }

    val darkThemeEnabled: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.DARK_THEME_ENABLED] ?: "auto"
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
}