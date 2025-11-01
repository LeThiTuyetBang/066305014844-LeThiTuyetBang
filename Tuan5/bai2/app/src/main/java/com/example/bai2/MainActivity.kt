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
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

// Data class lưu thông tin người dùng
data class UserProfile(
    val name: String?,
    val email: String?,
    val photoUrl: String?,
    val birthday: String? = "Chưa có"
)

class MainActivity : ComponentActivity() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- Facebook SDK ---
        AppEventsLogger.activateApp(application)
        callbackManager = CallbackManager.Factory.create()

        // --- Google Sign In ---
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            var userProfile by remember { mutableStateOf<UserProfile?>(null) }

            // Kiểm tra user hiện tại
            LaunchedEffect(Unit) {
                auth.currentUser?.let {
                    userProfile = UserProfile(it.displayName, it.email, it.photoUrl?.toString())
                }
            }

            if (userProfile != null) {
                // Đã đăng nhập
                NewProfileScreen(user = userProfile!!) {
                    auth.signOut()
                    googleSignInClient.signOut()
                    LoginManager.getInstance().logOut()
                    userProfile = null
                }
            } else {
                // Chưa đăng nhập
                val googleSignInLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        try {
                            val account = task.getResult(ApiException::class.java)!!
                            firebaseAuthWithGoogle(account.idToken!!) { profile ->
                                userProfile = profile
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
                        loginWithFacebook(this, callbackManager) { profile ->
                            userProfile = profile
                        }
                    },
                    onPhoneLogin = {
                        startActivity(Intent(this, PhoneLoginActivity::class.java))
                    }
                )
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onResult: (UserProfile?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    onResult(user?.let { UserProfile(it.displayName, it.email, it.photoUrl?.toString()) })
                } else {
                    Toast.makeText(this, "Xác thực Firebase thất bại", Toast.LENGTH_SHORT).show()
                    onResult(null)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}

// ============================= FACEBOOK LOGIN ==================================
fun loginWithFacebook(activity: Activity, callbackManager: CallbackManager, onResult: (UserProfile?) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile", "user_birthday"))
    LoginManager.getInstance().registerCallback(callbackManager,
        object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val request = GraphRequest.newMeRequest(result.accessToken) { obj, _ ->
                    val birthday = obj?.optString("birthday")
                    val picture = obj?.getJSONObject("picture")?.getJSONObject("data")?.getString("url")
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            Toast.makeText(activity, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                            onResult(user?.let {
                                UserProfile(it.displayName, it.email, picture, birthday)
                            })
                        } else {
                            Toast.makeText(activity, "Xác thực Firebase thất bại", Toast.LENGTH_SHORT).show()
                            onResult(null)
                        }
                    }
                }
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

// ============================= PROFILE SCREEN ==================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProfileScreen(user: UserProfile, onSignOut: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = Color(0xFF007AFF)) },
                navigationIcon = {
                    IconButton(onClick = onSignOut) {
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
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) {
                Text("Đăng xuất", fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
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
            Box {
                AsyncImage(
                    model = user.photoUrl,
                    fallback = painterResource(id = R.drawable.anhgaidep),
                    error = painterResource(id = R.drawable.anhgaidep),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            ProfileInfoField("Name", user.name ?: "N/A")
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInfoField("Email", user.email ?: "N/A")
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInfoField("Date of Birth", user.birthday ?: "N/A", isDropdown = true)
        }
    }
}

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

// ============================= LOGIN SCREEN ==================================
@Composable
fun LoginScreen(
    onGoogleLogin: () -> Unit,
    onFacebookLogin: () -> Unit,
    onPhoneLogin: () -> Unit
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
            Image(
                painter = painterResource(id = R.drawable.uth_logo),
                contentDescription = "Logo UTH",
                modifier = Modifier
                    .size(160.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("SmartTasks", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("A simple and efficient to-do app", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(40.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPhoneLogin,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_phone),
                        contentDescription = "Phone Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ĐĂNG NHẬP BẰNG SỐ ĐIỆN THOẠI", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
