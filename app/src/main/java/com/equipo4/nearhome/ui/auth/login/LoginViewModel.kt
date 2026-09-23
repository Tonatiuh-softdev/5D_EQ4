package com.equipo4.nearhome.ui.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        val isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        _uiState.update {
            it.copy(
                email = email,
                isEmailValid = isValid
            )
        }
    }

    fun onPasswordChanged(password: String) {
        val isValid = password.length >= 6
        _uiState.update {
            it.copy(
                password = password,
                isPasswordValid = isValid
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun onLoginClicked() {
        if (_uiState.value.isFormValid) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Lógica futura con Repository / Backend
        }
    }

    fun onGoogleLoginClicked() {
        // Lógica de integración con Google Auth
    }
}