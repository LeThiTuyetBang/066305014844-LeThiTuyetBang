package com.example.btvn2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.btvn2.R

@Composable
fun ConfirmScreen(
    navController: NavController,
    email: String,
    code: String,
    password: String,
    onSubmit: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Image(
            painter = painterResource(id = R.drawable.logo_uth),
            contentDescription = "UTH Logo",
            modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(10.dp))
        Text("SmartTasks", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(20.dp))
        Text("Confirm", style = MaterialTheme.typography.titleMedium)
        Text("We are here to help you!")
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(value = email, onValueChange = {}, label = { Text("Email") }, enabled = false)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = code, onValueChange = {}, label = { Text("Code") }, enabled = false)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = password, onValueChange = {}, label = { Text("Password") }, enabled = false)

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                onSubmit("Email: $email\nCode: $code\nPassword: $password")
                navController.navigate("forget_password") {
                    popUpTo("forget_password") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}
