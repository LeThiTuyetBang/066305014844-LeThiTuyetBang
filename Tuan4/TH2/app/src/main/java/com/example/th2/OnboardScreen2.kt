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
fun OnboardScreen2(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.tang),
            contentDescription = "Increase Work Effectiveness",
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Tiêu đề
        Text(
            text = "Increase Work Effectiveness",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Nội dung
        Text(
            text = "Time management and the determination of more important tasks will " +
                    "give your job statistics better and always improve.",
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
            Button(onClick = { navController.navigate("onboard3") }) {
                Text("Next")
            }
        }
    }
}
