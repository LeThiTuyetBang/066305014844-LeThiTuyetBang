package com.example.baitap2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NumberInputScreen() }
    }
}

@Composable
fun NumberInputScreen() {
    var inputValue by remember { mutableStateOf("") }
    var numbers by remember { mutableStateOf(listOf<Int>()) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Thực hành 02", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                TextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    placeholder = { Text("Nhập vào số lượng") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        val n = inputValue.toIntOrNull()
                        if (n == null || n <= 0) {
                            errorMessage = "Dữ liệu bạn nhập không hợp lệ"
                            numbers = emptyList()
                        } else {
                            errorMessage = ""
                            numbers = (1..n).toList()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF2196F3)),
                    shape = RoundedCornerShape(50)
                ) { Text("Tạo") }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(errorMessage, color = Color.Red)
            }

            Spacer(Modifier.height(16.dp))

            numbers.forEach { num ->
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFE53935)),
                    shape = RoundedCornerShape(16.dp)
                ) { Text(num.toString(), fontSize = 18.sp, color = Color.White) }
            }
        }
    }
}
