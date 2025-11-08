package com.example.bai1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(private val pref: ThemePreference) : ViewModel() {
    private val _selectedTheme = MutableStateFlow(0)
    val selectedTheme = _selectedTheme.asStateFlow()

    init {
        viewModelScope.launch {
            pref.getTheme().collect { theme ->
                _selectedTheme.value = theme
            }
        }
    }

    fun setTheme(theme: Int) {
        viewModelScope.launch {
            pref.saveTheme(theme)
        }
    }
}
