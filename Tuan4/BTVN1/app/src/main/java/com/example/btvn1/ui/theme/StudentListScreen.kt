package com.example.btvn1.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.btvn1.model.LibraryManager

@Composable
fun StudentListScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Danh sách sinh viên", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        LibraryManager.students.forEach { Text("• ${it.name}") }
    }
}
