package com.example.btvn2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.btvn2.R

@Composable
fun ForgetPasswordScreen(navController: NavController, submittedData: String) {
    var email by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_uth),
            contentDescription = "UTH Logo",
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text("SmartTasks", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(20.dp))
        Text("Forget Password?", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        Text("Enter your Email, we will send you a verification code.")
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Your Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { navController.navigate("verify_code/${email.text}") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Next")
        }

        if (submittedData.isNotEmpty()) {
            Spacer(Modifier.height(30.dp))
            Text("📝 Last submission:")
            Text(submittedData)
        }
    }
}
