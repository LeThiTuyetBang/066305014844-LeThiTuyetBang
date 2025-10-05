package com.example.btvn

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BTVNWeek2()
            }
        }
    }
}

@Composable
fun BTVNWeek2() {
    var name by remember { mutableStateOf("") }
    var ageInput by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tiêu đề
        Text(
            text = "BÀI TẬP TUẦN 02",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Nhập họ tên
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Họ và tên") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Nhập tuổi
        OutlinedTextField(
            value = ageInput,
            onValueChange = { ageInput = it },
            label = { Text("Tuổi") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Kết quả
        if (message.isNotEmpty()) {
            val textColor = if (message.contains("Vui lòng")) Color.Red else Color(0xFF007BFF)

            Text(
                text = message,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút kiểm tra
        Button(
            onClick = {
                if (name.isBlank()) {
                    message = "Vui lòng nhập họ tên"
                    return@Button
                }
                if (ageInput.isBlank()) {
                    message = "Vui lòng nhập tuổi"
                    return@Button
                }

                val age = ageInput.toIntOrNull()
                if (age == null || age < 0) {
                    message = "Tuổi không hợp lệ"
                } else {
                    message = when {
                        age < 2 -> "$name là Em bé "
                        age in 2..6 -> "$name là Trẻ em "
                        age in 7..65 -> "$name là Người lớn "
                        else -> "$name là Người già "
                    }
                    Toast.makeText(context, "Kiểm tra thành công!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E35B1)),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Kiểm tra", color = Color.White, fontSize = 18.sp)
        }
    }
}
