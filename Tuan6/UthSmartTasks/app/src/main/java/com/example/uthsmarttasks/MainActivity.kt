package com.example.uthsmarttasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

// ==================== DATA MODELS ====================
data class Attachment(
    @SerializedName("id") val id: Int,
    @SerializedName("fileName") val fileName: String,
    @SerializedName("fileUrl") val fileUrl: String
)

data class SubTask(
    val id: Int,
    val title: String,
    val completed: Boolean
)

data class Task(
    val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("status") val status: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("dueDate") val dueDate: String? = null,
    @SerializedName("attachments") val attachments: List<Attachment>? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("subtasks") val subtasks: List<SubTask>? = null
)

data class TaskResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<Task>
)

data class SingleTaskResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Task
)

// ==================== API SERVICE ====================
interface TaskApi {
    @GET("api/researchUTH/tasks")
    suspend fun getTasks(): Response<TaskResponse>

    @GET("api/researchUTH/task/{id}")
    suspend fun getTask(@Path("id") id: Int): Response<SingleTaskResponse>

    @DELETE("api/researchUTH/task/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Unit>
}

object RetrofitClient {
    private const val BASE_URL = "https://amock.io/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder().addInterceptor(logging).build()

    val api: TaskApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TaskApi::class.java)
}

// ==================== STATE MANAGEMENT ====================
class TaskListState {
    var tasks by mutableStateOf<List<Task>>(emptyList())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    suspend fun fetch() {
        isLoading = true
        error = null
        try {
            val response = RetrofitClient.api.getTasks()
            if (response.isSuccessful) {
                tasks = response.body()?.data ?: emptyList()
            } else {
                error = "Lỗi tải danh sách: ${response.code()}"
            }
        } catch (e: Exception) {
            error = e.message ?: "Lỗi kết nối mạng"
        } finally {
            isLoading = false
        }
    }
}

class TaskDetailState(private val taskId: Int) {
    var task by mutableStateOf<Task?>(null)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    suspend fun fetch() {
        isLoading = true
        try {
            val response = RetrofitClient.api.getTask(taskId)
            if (response.isSuccessful) {
                task = response.body()?.data
            } else {
                error = "Lỗi tải chi tiết: ${response.code()}"
            }
        } catch (e: Exception) {
            error = e.message ?: "Lỗi kết nối mạng"
        } finally {
            isLoading = false
        }
    }

    suspend fun delete(onSuccess: () -> Unit) {
        try {
            val response = RetrofitClient.api.deleteTask(taskId)
            if (response.isSuccessful) {
                onSuccess()
            } else {
                error = "Xóa thất bại: ${response.code()}"
            }
        } catch (e: Exception) {
            error = e.message ?: "Lỗi kết nối mạng"
        }
    }
}

// ==================== UI COMPONENTS ====================

@Composable
fun ThemedStatusBadge(text: String, containerColor: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun EmptyListView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckBoxOutlineBlank,
            contentDescription = "No tasks",
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("No Tasks Yet!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Stay productive—add something to do.",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun TaskCard(task: Task, onClick: () -> Unit) {
    val cardColor = when (task.status.lowercase()) {
        "in progress" -> Color(0xFFFEF7E0) // Màu hồng nhạt cho "Complete" trong mẫu
        "pending" -> Color(0xFFE6F4EA)     // Màu xanh lá cây nhạt
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = task.status.lowercase() == "completed",
                onCheckedChange = null, // Read-only
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                if (!task.description.isNullOrEmpty()) {
                    Text(
                        text = task.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ThemedStatusBadge(
                        text = "Status: ${task.status}",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    if (task.dueDate != null) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Due Date", modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = task.dueDate.substringBefore(" "), // Chỉ lấy ngày
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String, icon: ImageVector) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Text(label, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SubtaskItem(subtask: SubTask) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = subtask.completed, onCheckedChange = null)
        Spacer(Modifier.width(8.dp))
        Text(subtask.title, fontSize = 15.sp, color = if (subtask.completed) Color.Gray else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun AttachmentItem(attachment: Attachment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Attachment, contentDescription = "Attachment", tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text(attachment.fileName, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}


// ==================== SCREENS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(navController: NavHostController) {
    val state = remember { TaskListState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { scope.launch { state.fetch() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // Title is handled by the content below
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navigate to Add Task Screen */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, "Home") }, label = {Text("Home")})
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.DateRange, "Calendar") }, label = {Text("Calendar")})
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.AutoMirrored.Filled.List, "Tasks") }, label = {Text("Tasks")})
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Settings, "Settings") }, label = {Text("Settings")})
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .background(MaterialTheme.colorScheme.background)) {
            // App Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = "App Logo",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("UTH", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground)
                    Text("SmartTasks", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                }
                IconButton(onClick = { /* TODO: Handle notifications */ }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    state.error != null -> Text(state.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                    state.tasks.isEmpty() -> EmptyListView()
                    else -> LazyColumn {
                        items(state.tasks, key = { it.id }) { task ->
                            TaskCard(task) { navController.navigate("detail/${task.id}") }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(navController: NavController, taskId: Int) {
    val state = remember { TaskDetailState(taskId) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { scope.launch { state.fetch() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            state.delete {
                                // On success, navigate back
                                navController.popBackStack()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> Text(state.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                state.task != null -> {
                    val task = state.task!!
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        // Title
                        item {
                            Text(task.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text(task.description ?: "No description provided.", fontSize = 16.sp, color = Color.Gray)
                            Spacer(Modifier.height(24.dp))
                        }

                        // Info cards
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                DetailInfoRow("Category", task.category ?: "N/A", Icons.Default.Category)
                                DetailInfoRow("Status", task.status, Icons.Default.HourglassTop)
                                DetailInfoRow("Priority", task.priority, Icons.Default.Flag)
                            }
                        }

                        // Subtasks
                        if (!task.subtasks.isNullOrEmpty()) {
                            item { SectionTitle("Subtasks") }
                            items(task.subtasks) { subtask ->
                                SubtaskItem(subtask)
                            }
                        }

                        // Attachments
                        if (!task.attachments.isNullOrEmpty()) {
                            item { SectionTitle("Attachments") }
                            items(task.attachments) { attachment ->
                                AttachmentItem(attachment)
                            }
                        }
                    }
                }
                else -> {
                    // This state can happen if the fetch completes with no task and no error
                    Text("Task not found.", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}


// ==================== THEME + MAIN ACTIVITY ====================
// Custom painter to use Vector as a Painter
@Composable
fun rememberVectorPainter(image: ImageVector) =
    androidx.compose.ui.graphics.vector.rememberVectorPainter(image)

@Composable
fun UTHSmartTasksTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = Color(0xFF6750A4),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFEADDFF),
        onPrimaryContainer = Color(0xFF21005D),
        secondary = Color(0xFF625B71),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE8DEF8),
        onSecondaryContainer = Color(0xFF1D192B),
        tertiary = Color(0xFF7D5260),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFFFD8E4),
        onTertiaryContainer = Color(0xFF31111D),
        error = Color(0xFFB3261E),
        onError = Color.White,
        errorContainer = Color(0xFFF9DEDC),
        onErrorContainer = Color(0xFF410E0B),
        background = Color(0xFFFEF7FF),
        onBackground = Color(0xFF1D1B20),
        surface = Color(0xFFFEF7FF),
        onSurface = Color(0xFF1D1B20),
        surfaceVariant = Color(0xFFE7E0EC),
        onSurfaceVariant = Color(0xFF49454F),
        outline = Color(0xFF79747E)
    )

    MaterialTheme(colorScheme = colorScheme, typography = Typography(), content = content)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UTHSmartTasksTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "list") {
                        composable("list") { TaskListScreen(navController) }
                        composable(
                            "detail/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("id") ?: 0
                            TaskDetailScreen(navController, id)
                        }
                    }
                }
            }
        }
    }
}
