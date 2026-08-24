package dev.lciszewski27.whereismymoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.lciszewski27.whereismymoney.ui.navigation.AppNavHost
import dev.lciszewski27.whereismymoney.ui.theme.WhereIsMyMoneyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as WhereIsMyMoneyApp

        setContent {
            val dynamicColorEnabled by app.preferences.dynamicColorEnabled.collectAsState(initial = true)
            val darkThemeMode by app.preferences.darkThemeEnabled.collectAsState(initial = "auto")

            val isDarkTheme = when (darkThemeMode) {
                "light" -> false
                "dark" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            WhereIsMyMoneyTheme(
                darkTheme = isDarkTheme,
                dynamicColor = dynamicColorEnabled
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost()
                }
            }
        }
    }
}