package com.example.bai1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = ThemePreference(this)
        val factory = viewModelFactory {
            initializer { ThemeViewModel(pref) }
        }
        val viewModel = ViewModelProvider(this, factory)[ThemeViewModel::class.java]

        setContent {
            val selectedTheme by viewModel.selectedTheme.collectAsState()
            val backgroundColor = when (selectedTheme) {
                1 -> Color.Black
                2 -> Color.Magenta
                3 -> Color(0xFF42A5F5)
                else -> Color.White
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = backgroundColor
            ) {
                ThemeScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ThemeScreen(viewModel: ThemeViewModel) {
    var tempTheme by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Setting", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ColorOption(Color.White, 0, tempTheme) { tempTheme = it }
            ColorOption(Color.Black, 1, tempTheme) { tempTheme = it }
            ColorOption(Color.Magenta, 2, tempTheme) { tempTheme = it }
            ColorOption(Color(0xFF42A5F5), 3, tempTheme) { tempTheme = it }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = { viewModel.setTheme(tempTheme) }) {
            Text("Apply")
        }
    }
}

@Composable
fun ColorOption(color: Color, index: Int, selected: Int, onSelect: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(50)
            )
            .clickable { onSelect(index) }
            .border(
                width = if (selected == index) 4.dp else 1.dp,
                color = if (selected == index) Color.Gray else Color.LightGray,
                shape = RoundedCornerShape(50)
            )
    )
}
