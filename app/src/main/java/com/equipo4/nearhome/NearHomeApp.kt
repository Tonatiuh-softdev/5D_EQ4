package com.equipo4.nearhome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.equipo4.nearhome.ui.auth.login.LoginScreen
import com.equipo4.nearhome.ui.auth.register.SignUpScreen

object AuthRoutes {
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NearHomeApp()
        }
    }
}

@Composable
fun NearHomeApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.LOGIN
    ) {
        // Pantalla 1: Login
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(AuthRoutes.SIGN_UP)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(AuthRoutes.FORGOT_PASSWORD)
                }
            )
        }

        // Pantalla 2: Registro (Sign Up)
        composable(AuthRoutes.SIGN_UP) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla 3: Recuperación de contraseña (Pendiente)
        composable(AuthRoutes.FORGOT_PASSWORD) {
            // Se implementará despues
        }
    }
}