package com.example.uthauth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {

    // Khai báo auth và googleClient ở cấp Activity
    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient

    // Sử dụng mutableState để cập nhật UI từ bên ngoài Composable
    private val messageState = mutableStateOf("")

    // Khởi tạo ActivityResultLauncher ở cấp Activity
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleGoogleSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo auth và googleClient một lần trong onCreate
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Lấy chuỗi ở đây
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, gso)

        setContent {
            // Truyền các dependencies cần thiết vào Composable
            LoginScreen(
                message = messageState.value,
                onLoginClick = {
                    launcher.launch(googleClient.signInIntent)
                }
            )
        }
    }

    private fun handleGoogleSignInResult(result: ActivityResult) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            // Google Sign In thành công, xác thực với Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d("MainActivity", "Firebase auth with Google:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In thất bại
            Log.w("MainActivity", "Google sign in failed", e)
            messageState.value = "Google Sign-in Failed: ${e.statusCode}"
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Đăng nhập thành công, cập nhật UI
                    val user = auth.currentUser
                    messageState.value = "Success!\nHello ${user?.email}"
                } else {
                    // Nếu đăng nhập thất bại, hiển thị thông báo
                    messageState.value = "Sign-in failed!"
                    Log.w("MainActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }
}

@Composable
fun LoginScreen(message: String, onLoginClick: () -> Unit) {
    // Giao diện Compose bây giờ chỉ chịu trách nhiệm hiển thị UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = onLoginClick) {
                Text("Login by Gmail")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = if (message.startsWith("Success")) Color(0xFF2E7D32) else Color(0xFFC62828),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
