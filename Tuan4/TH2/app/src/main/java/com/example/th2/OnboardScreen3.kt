package com.example.th2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun OnboardScreen3(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.thongbao),
            contentDescription = "Reminder Notification",
            modifier = Modifier.size(350.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Tiêu đề
        Text(
            text = "Reminder Notification",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Nội dung
        Text(
            text = "The advantage of this application is that it also provides reminders for you " +
                    "so you don't forget to keep doing your assignments well and according to the time you have set.",
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { /* TODO: chuyển qua màn hình chính sau này */ }) {
                Text("Get Started")
            }
        }
    }
}
