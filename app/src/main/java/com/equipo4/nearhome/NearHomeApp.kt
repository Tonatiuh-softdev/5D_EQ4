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
import com.equipo4.nearhome.ui.home.list.HomeScreen

object AppRoutes {
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val HOME = "home"
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
        // Pantalla inicial configurada en LOGIN
        startDestination = AppRoutes.LOGIN
    ) {
        // Pantalla 1: Login
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.SIGN_UP)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(AppRoutes.FORGOT_PASSWORD)
                },
                onLoginSuccess = {
                    // Navega a Home y elimina Login del historial para no regresar al presionar 'Atrás'
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla 2: Registro (Sign Up)
        composable(AppRoutes.SIGN_UP) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla 3: Recuperación de contraseña (Pendiente)
        composable(AppRoutes.FORGOT_PASSWORD) {
            // Se implementará después
        }

        // Pantalla 4: Home (Lista de inmuebles)
        composable(AppRoutes.HOME) {
            HomeScreen(
                onPropertyClick = { propertyId ->
                    // Callback listo para cuando maquetemos el detalle de la propiedad
                }
            )
        }
    }
}