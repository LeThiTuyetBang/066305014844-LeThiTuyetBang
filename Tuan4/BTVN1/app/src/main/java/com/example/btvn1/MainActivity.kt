package com.example.btvn1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.btvn1.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LibraryApp() }
    }
}

@Composable
fun LibraryApp() {
    val navController = rememberNavController()
    val current by navController.currentBackStackEntryAsState()
    val route = current?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = route == "manage",
                    onClick = { navController.navigate("manage") },
                    label = { Text("Quản lý") },
                    icon = { Icon(Icons.Filled.Home, null) }
                )
                NavigationBarItem(
                    selected = route == "books",
                    onClick = { navController.navigate("books") },
                    label = { Text("DS Sách") },
                    icon = { Icon(Icons.Filled.List, null) }
                )
                NavigationBarItem(
                    selected = route == "students",
                    onClick = { navController.navigate("students") },
                    label = { Text("Sinh viên") },
                    icon = { Icon(Icons.Filled.Person, null) }
                )
            }
        }
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = "manage",
            modifier = Modifier.padding(inner)
        ) {
            composable("manage") { ManageScreen() }
            composable("books") { BookListScreen() }
            composable("students") { StudentListScreen() }
        }
    }
}
