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
fun OnboardScreen1(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.quanly),
            contentDescription = "Time Management",
            modifier = Modifier.size(350.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Tiêu đề
        Text(
            text = "Easy Time Management",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Nội dung
        Text(
            text = "With management based on priority and daily tasks, it will give you convenience " +
                    "in managing and determining the tasks that must be done first.",
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.navigate("onboard2") }) {
            Text("Next")
        }
    }
}
