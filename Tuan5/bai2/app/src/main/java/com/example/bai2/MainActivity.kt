package com.example.bai2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

// Data class để lưu thông tin người dùng từ cả Google và Facebook
data class UserProfile(
    val name: String?,
    val email: String?,
    val photoUrl: String?,
    val birthday: String? = "Chưa có" // Thêm ngày sinh
)

class MainActivity : ComponentActivity() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- Cấu hình Facebook ---
        AppEventsLogger.activateApp(application)
        callbackManager = CallbackManager.Factory.create()

        // --- Cấu hình Google ---
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            var userProfile by remember { mutableStateOf<UserProfile?>(null) }

            // Kiểm tra khi khởi động ứng dụng xem đã đăng nhập chưa
            LaunchedEffect(Unit) {
                auth.currentUser?.let {
                    // Khi khởi động, ta chỉ có thông tin cơ bản từ Firebase
                    userProfile = UserProfile(it.displayName, it.email, it.photoUrl?.toString())
                }
            }

            if (userProfile != null) {
                // ĐÃ ĐĂNG NHẬP: Hiển thị màn hình Profile mới
                NewProfileScreen(user = userProfile!!) {
                    // Xử lý đăng xuất
                    auth.signOut()
                    googleSignInClient.signOut()
                    LoginManager.getInstance().logOut() // Đăng xuất luôn cả Facebook nếu có
                    userProfile = null // Cập nhật state để quay về màn hình Login
                }
            } else {
                // CHƯA ĐĂNG NHẬP: Hiển thị màn hình Login
                val googleSignInLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        try {
                            val account = task.getResult(ApiException::class.java)!!
                            firebaseAuthWithGoogle(account.idToken!!) { profile ->
                                userProfile = profile // Cập nhật state để chuyển màn hình
                            }
                        } catch (e: ApiException) {
                            Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                LoginScreen(
                    onGoogleLogin = {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    },
                    onFacebookLogin = {
                        // ✅ Kết nối lại logic đăng nhập Facebook
                        loginWithFacebook(this, callbackManager) { profile ->
                            userProfile = profile
                        }
                    }
                )
            }
        }
    }

    // Hàm xác thực Google với Firebase và trả về UserProfile
    private fun firebaseAuthWithGoogle(idToken: String, onResult: (UserProfile?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    // Tạo đối tượng UserProfile từ thông tin lấy được
                    onResult(user?.let { UserProfile(it.displayName, it.email, it.photoUrl?.toString()) })
                } else {
                    Toast.makeText(this, "Xác thực Firebase thất bại", Toast.LENGTH_SHORT).show()
                    onResult(null)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Chuyển kết quả về cho Facebook SDK xử lý
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}

// ✅ HÀM LOGIN FACEBOOK ĐÃ ĐƯỢC NÂNG CẤP HOÀN CHỈNH
fun loginWithFacebook(activity: Activity, callbackManager: CallbackManager, onResult: (UserProfile?) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    // Yêu cầu thêm quyền user_birthday
    LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile", "user_birthday"))

    LoginManager.getInstance().registerCallback(callbackManager,
        object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                // Khi Facebook login thành công, dùng GraphRequest để lấy thêm thông tin chi tiết
                val request = GraphRequest.newMeRequest(result.accessToken) { obj, _ ->
                    val birthday = obj?.optString("birthday") // Định dạng "MM/DD/YYYY"
                    val picture = obj?.getJSONObject("picture")?.getJSONObject("data")?.getString("url")

                    // Sau khi có đủ thông tin, tiến hành xác thực với Firebase
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            Toast.makeText(activity, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                            // Tạo đối tượng UserProfile với đầy đủ thông tin
                            onResult(user?.let {
                                UserProfile(it.displayName, it.email, picture, birthday)
                            })
                        } else {
                            Toast.makeText(activity, "Xác thực Firebase thất bại", Toast.LENGTH_SHORT).show()
                            onResult(null)
                        }
                    }
                }
                // Khai báo các trường thông tin cần lấy từ Facebook
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,birthday,picture.type(large)")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                Toast.makeText(activity, "Đã hủy đăng nhập", Toast.LENGTH_SHORT).show()
                onResult(null)
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(activity, "Lỗi Facebook: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("FacebookLogin", "Error: ", error)
                onResult(null)
            }
        })
}

// Màn hình ProfileScreen mới, giống với ảnh mẫu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProfileScreen(user: UserProfile, onSignOut: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = Color(0xFF007AFF)) },
                navigationIcon = {
                    IconButton(onClick = onSignOut) { // Nút back cũng là nút đăng xuất
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF007AFF))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(50), // Bo tròn mạnh
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) {
                Text("Back", fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Ảnh đại diện
            Box {
                // ✅ Dùng AsyncImage để tải ảnh từ URL của Google/Facebook
                AsyncImage(
                    model = user.photoUrl,
                    // Nếu không có ảnh, hiển thị ảnh mặc định
                    fallback = painterResource(id = R.drawable.anhgaidep),
                    error = painterResource(id = R.drawable.anhgaidep),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )
                // Icon camera nhỏ
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Change Picture",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .background(Color(0xFF4A90E2), CircleShape)
                        .padding(8.dp)
                        .size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Các trường thông tin
            ProfileInfoField("Name", user.name ?: "N/A")
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInfoField("Email", user.email ?: "N/A")
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInfoField("Date of Birth", user.birthday ?: "N/A", isDropdown = true)
        }
    }
}

// Composable cho một trường thông tin
@Composable
fun ProfileInfoField(label: String, value: String, isDropdown: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (isDropdown) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dropdown),
                        contentDescription = "Dropdown"
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                disabledBorderColor = Color.LightGray,
                unfocusedContainerColor = Color(0xFFF7F8F9),
                disabledContainerColor = Color(0xFFF7F8F9)
            )
        )
    }
}


// LoginScreen giữ nguyên, không cần sửa
@Composable
fun LoginScreen(
    onGoogleLogin: () -> Unit,
    onFacebookLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(
            text = "© UTHSmartTasks",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.uth_logo),
                    contentDescription = "Logo UTH",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("SmartTasks", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("A simple and efficient to-do app", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(48.dp))
            Text("Welcome", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "Ready to explore? Log in to get started.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Gắn sự kiện onGoogleLogin vào nút
            Button(
                onClick = onGoogleLogin,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.g),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIGN IN WITH GOOGLE", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onFacebookLogin,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.f),
                        contentDescription = "Facebook Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ĐĂNG NHẬP BẰNG FACEBOOK", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
