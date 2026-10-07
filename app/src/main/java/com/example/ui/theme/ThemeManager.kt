package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode(val id: String, val label: String) {
    SYSTEM("system", "Automático (Sistema)"),
    LIGHT("light", "Modo Claro"),
    DARK("dark", "Modo Escuro")
}

object ThemeManager {
    private const val PREFS_NAME = "ajusta_theme_prefs"
    private const val KEY_THEME_MODE = "app_theme_mode"

    private var sharedPreferences: SharedPreferences? = null

    private val _themeModeState = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeModeState: StateFlow<AppThemeMode> = _themeModeState.asStateFlow()

    fun init(context: Context) {
        if (sharedPreferences == null) {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            sharedPreferences = prefs
            val savedModeId = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.id)
            val initialMode = AppThemeMode.values().find { it.id == savedModeId } ?: AppThemeMode.SYSTEM
            _themeModeState.value = initialMode
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeModeState.value = mode
        sharedPreferences?.edit()?.putString(KEY_THEME_MODE, mode.id)?.apply()
    }

    fun toggleTheme() {
        val next = when (_themeModeState.value) {
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.SYSTEM
            AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
        }
        setThemeMode(next)
    }
}
