package com.example.th2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.example.th2.ui.theme.TH2Theme
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TH2Theme {
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

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(nav) }
        composable("components") { ComponentListScreen(nav) }
        composable("text") { ScreenWithTopBar("Text Detail", nav) { TextDetailScreen() } }
        composable("image") { ScreenWithTopBar("Images", nav) { ImageScreen() } }
        composable("textfield") { ScreenWithTopBar("TextField", nav) { TextFieldScreen() } }
        composable("row") { ScreenWithTopBar("Row Layout", nav) { RowLayoutScreen() } }
        composable("buttons") { ScreenWithTopBar("Buttons", nav) { ButtonsSnackbarScreen() } }
        composable("toggles") { ScreenWithTopBar("Toggles", nav) { TogglesScreen() } }
        composable("slider") { ScreenWithTopBar("Slider & Progress", nav) { SliderProgressScreen() } }
        composable("dialog") { ScreenWithTopBar("Dialog", nav) { DialogScreen() } }
        composable("list") { ScreenWithTopBar("List & Card", nav) { ListCardScreen() } }
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

/* ======================== HOME ======================== */
@Composable
fun HomeScreen(nav: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.anh),
            contentDescription = null,
            modifier = Modifier.size(130.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text("Jetpack Compose", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(
            "Jetpack Compose is a modern UI toolkit for\nbuilding native Android applications using\na declarative programming approach.",
            color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = { nav.navigate("components") },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
        ) { Text("I’m Ready", color = Color.White) }
    }
}

/* ======================== COMPONENTS LIST ======================== */
@Composable
fun ComponentListScreen(nav: NavController) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("UI Components List", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
        Spacer(Modifier.height(16.dp))

        val items = listOf(
            Triple("Text", "Displays text", "text"),
            Triple("Image", "Displays an image", "image"),
            Triple("TextField", "Input field for text", "textfield"),
            Triple("Row Layout", "Arranges elements horizontally", "row"),
            Triple("Buttons", "Different button types & snackbar", "buttons"),
            Triple("Toggles", "Switch / Checkbox / Radio", "toggles"),
            Triple("Slider & Progress", "Adjust and reflect progress", "slider"),
            Triple("Dialog", "Alert dialog confirm/cancel", "dialog"),
            Triple("List & Card", "Simple list with cards", "list")
        )

        items.forEach { (title, desc, route) ->
            Button(
                onClick = { nav.navigate(route) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
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

/* ======================== TEXT DETAIL ======================== */
@Composable
fun TextDetailScreen() {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val styled = buildAnnotatedString {
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("The ") }
            withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append("quick ") }
            withStyle(SpanStyle(color = Color(0xFF8B4513), fontWeight = FontWeight.Bold)) { append("Brown\n") }
            append("fox j u m p s ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("over\n") }
            append("the ")
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append("lazy ") }
            append("dog.")
        }
        Text(styled, fontSize = 26.sp, lineHeight = 36.sp)
    }
}

/* ======================== IMAGE ======================== */
@Composable
fun ImageScreen() {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.anhtruong),
            contentDescription = null,
            modifier = Modifier.height(180.dp).clip(RoundedCornerShape(12.dp))
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "https://giaothongvantaihcm.edu.vn/wp-content/uploads/2025/01/Logo-GTVT.png",
            color = Color.Blue, fontSize = 12.sp, textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(R.drawable.uthlogo),
            contentDescription = null,
            modifier = Modifier.height(180.dp).clip(RoundedCornerShape(12.dp)))
        Text(
            "Logo UTH",
            color = Color.Green, fontSize = 12.sp, textAlign = TextAlign.Center
        )
    }
}

/* ======================== TEXTFIELD ======================== */
@Composable
fun TextFieldScreen() {
    var input by remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Thông tin nhập") },
            modifier = Modifier.fillMaxWidth(0.85f)
        )
        Spacer(Modifier.height(8.dp))
        Text("Tự động cập nhật dữ liệu theo textfield", color = Color(0xFFD32F2F))
    }
}

/* ======================== ROW LAYOUT ======================== */
@Composable
fun RowLayoutScreen() {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(4) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val colors = listOf(
                    Color(0xFFBBDEFB),
                    Color(0xFF64B5F6),
                    Color(0xFF1976D2)
                )
                colors.forEach { c ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(c, RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

/* ======================== BUTTONS ======================== */
@Composable
fun ButtonsSnackbarScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { p ->
        Column(
            Modifier.fillMaxSize().padding(p).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            Text("Button Showcase", fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
            Button(onClick = { }) { Text("Filled") }
            FilledTonalButton(onClick = { }) { Text("Tonal") }
            OutlinedButton(onClick = { }) { Text("Outlined") }
            Button(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Snackbar from Button!")
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF9C27B0))
            ) { Text("Show Snackbar") }
        }
    }
}


/* ======================== TOGGLES ======================== */
@Composable
fun TogglesScreen() {
    var checked by remember { mutableStateOf(true) }
    var accepted by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf("Male") }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF1E88E5))
            )
            Spacer(Modifier.width(8.dp)); Text(if (checked) "Switch: ON" else "Switch: OFF", fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = accepted,
                onCheckedChange = { accepted = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF8E24AA))
            )
            Spacer(Modifier.width(8.dp)); Text(if (accepted) "Accepted" else "Not accepted")
        }
        Text("Radio group:", fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = gender == "Male", onClick = { gender = "Male" })
            Text("Male"); Spacer(Modifier.width(16.dp))
            RadioButton(selected = gender == "Female", onClick = { gender = "Female" })
            Text("Female")
        }
        Text("Selected: $gender", color = Color.Gray)
    }
}

/* ======================== SLIDER & PROGRESS ======================== */
@Composable
fun SliderProgressScreen() {
    var value by remember { mutableStateOf(0.3f) }
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Value: ${(value * 100).toInt()}%", fontWeight = FontWeight.Bold)
        Slider(value = value, onValueChange = { value = it })
        LinearProgressIndicator(progress = value, modifier = Modifier.fillMaxWidth(0.9f))
        CircularProgressIndicator(progress = value)
    }
}

/* ======================== DIALOG ======================== */
@Composable
fun DialogScreen() {
    var show by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { show = true }, colors = ButtonDefaults.buttonColors(Color(0xFF1565C0))) {
            Text("Show Dialog", color = Color.White)
        }
    }
    if (show) {
        AlertDialog(
            onDismissRequest = { show = false },
            title = { Text("Delete item?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = { TextButton({ show = false }) { Text("Confirm", color = Color(0xFF1565C0)) } },
            dismissButton = { TextButton({ show = false }) { Text("Cancel", color = Color.Gray) } }
        )
    }
}

/* ======================== LIST & CARD ======================== */
@Composable
fun ListCardScreen() {
    val items = List(10) { i -> "Item #$i" }
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items.size) { i ->
            Card(
                shape = RoundedCornerShape(12.dp),
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(items[i], fontWeight = FontWeight.Bold)
                    Text("Tap", color = Color.Gray)
                }
            }
        }
    }
}
