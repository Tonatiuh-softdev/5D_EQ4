package com.equipo4.nearhome

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.equipo4.nearhome.ui.auth.completeprofile.CompleteProfileScreen
import com.equipo4.nearhome.ui.auth.forgot_password.ForgotPasswordScreen
import com.equipo4.nearhome.ui.auth.forgot_password.ForgotPasswordViewModel
import com.equipo4.nearhome.ui.auth.login.LoginScreen
import com.equipo4.nearhome.ui.auth.register.SignUpScreen
import com.equipo4.nearhome.ui.auth.verification.PhoneVerificationScreen
import com.equipo4.nearhome.ui.home.list.HomeScreen
import com.equipo4.nearhome.ui.home.map.MapScreen
import com.equipo4.nearhome.ui.saved.SavedScreen
import com.equipo4.nearhome.ui.publicacion.detail.DetailPublicacionScreen
import com.equipo4.nearhome.ui.splash.SplashScreen
import com.equipo4.nearhome.util.SessionManager

object AppRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val COMPLETE_PROFILE = "complete_profile"
    const val PHONE_VERIFICATION = "phone_verification"
    const val HOME = "home"
    const val MAP = "home_map"
    const val SAVED = "saved"
    const val PUBLICACION_DETAIL = "publicacion_detail"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        // Mantiene Edge-to-Edge activo pero fuerza íconos oscuros en la barra de estado superior
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)
        setContent {
            NearHouseApp()
        }
    }
}

@Composable
fun NearHouseApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH
    ) {
        //Pantalla 0: Animacion
        composable(AppRoutes.SPLASH) {
            SplashScreen(
                onFinished = { isLoggedIn ->
                    val destination =
                        if (isLoggedIn) AppRoutes.HOME
                        else AppRoutes.LOGIN

                    navController.navigate(destination) {
                        popUpTo(AppRoutes.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            )
        }

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
                    sessionManager.setLoggedIn(true)
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
                onCompleteProfileSuccess = { phoneNumber ->
                    // Codificamos el teléfono para evitar errores con caracteres especiales (+ o espacios)
                    val encodedPhone = Uri.encode(phoneNumber)
                    navController.navigate("${AppRoutes.PHONE_VERIFICATION}/$encodedPhone")
                }
            )
        }

        // Pantalla 4: Verificación de Teléfono
        composable(
            route = "${AppRoutes.PHONE_VERIFICATION}/{phoneNumber}",
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            PhoneVerificationScreen(
                phoneNumber = phoneNumber,
                onNavigateToLogin = {
                    // Al verificar exitosamente el número, manda a Iniciar Sesión limpiando el flujo
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla 5: Recuperación de contraseña
        composable(AppRoutes.FORGOT_PASSWORD) {
            val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
            ForgotPasswordScreen(
                viewModel = forgotPasswordViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToSignUp = {
                    navController.navigate(AppRoutes.SIGN_UP) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = false }
                    }
                }
            )
        }

        // Pantalla 6: Home (Lista de inmuebles)
        // Barra inferior global: 0 Inicio, 1 Guardados (2 Notificaciones y 3 Perfil aún sin pantalla)
        val onBottomTab: (Int) -> Unit = { index ->
            when (index) {
                0 -> navController.navigate(AppRoutes.HOME) {
                    popUpTo(AppRoutes.HOME) { inclusive = false }
                    launchSingleTop = true
                }
                1 -> navController.navigate(AppRoutes.SAVED) {
                    popUpTo(AppRoutes.HOME) { inclusive = false }
                    launchSingleTop = true
                }
                else -> Unit
            }
        }

        composable(AppRoutes.HOME) {
            HomeScreen(
                onPropertyClick = { propertyId ->
                    navController.navigate("${AppRoutes.PUBLICACION_DETAIL}/$propertyId")
                },
                onToggleToMap = {
                    navController.navigate(AppRoutes.MAP) { launchSingleTop = true }
                },
                onBottomTabSelected = onBottomTab
            )
        }

        // Pantalla: Propiedades guardadas
        composable(AppRoutes.SAVED) {
            SavedScreen(
                onBack = { navController.popBackStack() },
                onPropertyClick = { propertyId ->
                    navController.navigate("${AppRoutes.PUBLICACION_DETAIL}/$propertyId")
                },
                onBottomTabSelected = onBottomTab
            )
        }

        // Pantalla 6b: Home (Mapa de inmuebles)
        composable(AppRoutes.MAP) {
            MapScreen(
                onToggleToList = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onPropertyClick = { propertyId ->
                    navController.navigate("${AppRoutes.PUBLICACION_DETAIL}/$propertyId")
                },
                onBottomTabSelected = onBottomTab
            )
        }

        // Pantalla 7: Detalle de Publicación
        composable(
            route = "${AppRoutes.PUBLICACION_DETAIL}/{publicacionId}",
            arguments = listOf(navArgument("publicacionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val publicacionId = backStackEntry.arguments?.getString("publicacionId") ?: ""

            DetailPublicacionScreen(
                publicacionId = publicacionId,
                onBackClick = {
                    navController.popBackStack()
                },
                onReportClick = {
                    // Aquí puedes navegar a la pantalla de reporte cuando esté lista
                },
                onEditClick = {
                    // Aquí puedes navegar a la pantalla de edición cuando esté lista
                }
            )
        }
    }
}