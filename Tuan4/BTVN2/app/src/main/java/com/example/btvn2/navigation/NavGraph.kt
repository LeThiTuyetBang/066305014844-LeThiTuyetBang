package com.example.btvn2.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.btvn2.ui.*

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    var submittedData by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "forget_password") {
        composable("forget_password") {
            ForgetPasswordScreen(navController, submittedData)
        }
        composable(
            route = "verify_code/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) {
            val email = it.arguments?.getString("email") ?: ""
            VerifyCodeScreen(navController, email)
        }
        composable(
            route = "reset_password/{email}/{code}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType }
            )
        ) {
            val email = it.arguments?.getString("email") ?: ""
            val code = it.arguments?.getString("code") ?: ""
            ResetPasswordScreen(navController, email, code)
        }
        composable(
            route = "confirm/{email}/{code}/{password}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("code") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ) {
            val email = it.arguments?.getString("email") ?: ""
            val code = it.arguments?.getString("code") ?: ""
            val password = it.arguments?.getString("password") ?: ""
            ConfirmScreen(navController, email, code, password) { result ->
                submittedData = result
            }
        }
    }
}
