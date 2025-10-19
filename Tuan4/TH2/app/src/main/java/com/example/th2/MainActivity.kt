package com.example.th2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.th2.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UTHNavigationApp()
        }
    }
}

@Composable
fun UTHNavigationApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("onboard1") { OnboardScreen1(navController) }
        composable("onboard2") { OnboardScreen2(navController) }
        composable("onboard3") { OnboardScreen3(navController) }
    }
}
