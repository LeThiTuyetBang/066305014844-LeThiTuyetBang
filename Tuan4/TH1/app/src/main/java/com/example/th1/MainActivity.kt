package com.example.th1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.th1.ui.theme.TH1Theme
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TH1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

/* ======================== NAVIGATION ======================== */
@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(nav) }
        composable("components") { ComponentListScreen(nav) }
        composable("million") { ScreenWithTopBar("1 Triệu phần tử", nav) { MillionItemsScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWithTopBar(title: String, nav: NavController, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E88E5)
                        )
                    }
                }
            )
        }
    ) { padding -> Box(modifier = Modifier.padding(padding)) { content() } }
}

/* ======================== HOME SCREEN ======================== */
@Composable
fun HomeScreen(nav: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.anh),
            contentDescription = "Logo hoặc ảnh minh họa",     // Mô tả ảnh cho người dùng khiếm thị
            modifier = Modifier
                .size(150.dp) // Chỉnh kích thước ảnh theo ý muốn
                .clip(RoundedCornerShape(16.dp)) // Bo tròn góc ảnh cho đẹp hơn
        )
        Text("Jetpack Compose", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(
            "Jetpack Compose is a modern UI toolkit for\nbuilding native Android apps declaratively.",
            color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = { nav.navigate("components") },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
        ) {
            Text("I’m Ready", color = Color.White)
        }
    }
}

/* ======================== COMPONENT LIST SCREEN ======================== */
@Composable
fun ComponentListScreen(nav: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("UI Components List", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
        Spacer(Modifier.height(16.dp))

        val items = listOf(
            Triple("1M List Test", "So sánh Column vs LazyColumn", "million")
        )

        items.forEach { (title, desc, route) ->
            Button(
                onClick = { nav.navigate(route) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text(desc, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

/* ======================== MILLION ITEMS TEST ======================== */
@Composable
fun MillionItemsScreen() {
    var showLazy by remember { mutableStateOf(false) }
    var showColumn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("So sánh Column vs LazyColumn", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                showLazy = false
                showColumn = true
            }) { Text("Load Column") }

            Button(onClick = {
                showColumn = false
                showLazy = true
            }) { Text("Load LazyColumn") }
        }

        Spacer(Modifier.height(16.dp))

        when {
            showColumn -> ColumnList()
            showLazy -> LazyList()
            else -> Text("Chọn 1 kiểu hiển thị để bắt đầu", color = Color.Gray)
        }
    }
}

@Composable
fun ColumnList() {
    val items = List(1_000_000) { "Column Item #$it" }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        items.forEach {
            Text(it, modifier = Modifier.padding(4.dp))
        }
    }
}

@Composable
fun LazyList() {
    val items = List(1_000_000) { "Lazy Item #$it" }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(items.size) { i ->
            Text(items[i], modifier = Modifier.padding(4.dp))
        }
    }
}
