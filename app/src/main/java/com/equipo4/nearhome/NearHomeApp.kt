package com.equipo4.nearhome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.equipo4.nearhome.ui.auth.completeprofile.CompleteProfileScreen
import com.equipo4.nearhome.ui.auth.login.LoginScreen
import com.equipo4.nearhome.ui.auth.register.SignUpScreen
import com.equipo4.nearhome.ui.auth.verification.PhoneVerificationScreen
import com.equipo4.nearhome.ui.home.list.HomeScreen

object AppRoutes {
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val COMPLETE_PROFILE = "complete_profile"
    const val PHONE_VERIFICATION = "phone_verification" // 👈 Nueva ruta para Verificación de Teléfono
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
                },
                onSignUpSuccess = {
                    // Redirige a Completa tu Perfil eliminando el Registro de la pila
                    navController.navigate(AppRoutes.COMPLETE_PROFILE) {
                        popUpTo(AppRoutes.SIGN_UP) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla 3: Completa tu Perfil
        composable(AppRoutes.COMPLETE_PROFILE) {
            CompleteProfileScreen(
                onCompleteProfileSuccess = {
                    // Al completar el perfil, manda a la pantalla de Verificación de Teléfono
                    navController.navigate(AppRoutes.PHONE_VERIFICATION)
                }
            )
        }

        // Pantalla 4: Verificación de Teléfono
        composable(AppRoutes.PHONE_VERIFICATION) {
            PhoneVerificationScreen(
                onNavigateToLogin = {
                    // Al verificar exitosamente el número, manda a Iniciar Sesión limpiando el flujo
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla 5: Recuperación de contraseña (Pendiente)
        composable(AppRoutes.FORGOT_PASSWORD) {
            // Se implementará después
        }

        // Pantalla 6: Home (Lista de inmuebles)
        composable(AppRoutes.HOME) {
            HomeScreen(
                onPropertyClick = { propertyId ->
                    // Callback listo para cuando maquetemos el detalle de la propiedad
                }
            )
        }
    }
}