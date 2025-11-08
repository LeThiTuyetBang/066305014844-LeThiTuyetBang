package com.example.bai1

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("theme_pref")

class ThemePreference(private val context: Context) {
    companion object {
        private val THEME_KEY = intPreferencesKey("theme_key")
    }

    suspend fun saveTheme(theme: Int) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme
        }
    }

    fun getTheme(): Flow<Int> {
        return context.dataStore.data.map { prefs ->
            prefs[THEME_KEY] ?: 0 // 0 = Default
        }
    }
}
