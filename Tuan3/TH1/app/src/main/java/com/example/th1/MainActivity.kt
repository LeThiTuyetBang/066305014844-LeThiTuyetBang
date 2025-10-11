package com.example.th1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.th1.ui.theme.TH1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TH1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyFirstAppScreen()
                }
            }
        }
    }
}

@Composable
fun MyFirstAppScreen() {
    // Biến trạng thái để thay đổi nội dung Text khi nhấn nút
    var showName by remember { mutableStateOf(false) }

    // Giao diện bố cục chính
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tiêu đề
        Text(
            text = "My First App",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(40.dp))

        // Text thay đổi khi nhấn nút
        if (!showName) {
            Text(
                text = "Hello",
                fontSize = 20.sp
            )
        } else {
            Text(
                text = buildAnnotatedString {
                    append("I’m ")
                    withStyle(
                        style = SpanStyle(
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Le Thi Tuyet Bang")
                    }
                },
                fontSize = 20.sp
            )
        }

        Spacer(Modifier.height(40.dp))

        // Nút bấm
        Button(
            onClick = { showName = true },
            modifier = Modifier
                .width(140.dp)
                .height(48.dp)
        ) {
            Text("Say Hi!")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyFirstAppPreview() {
    TH1Theme {
        MyFirstAppScreen()
    }
}
