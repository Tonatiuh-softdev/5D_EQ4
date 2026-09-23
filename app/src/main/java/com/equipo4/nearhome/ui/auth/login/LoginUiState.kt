package com.equipo4.nearhome.ui.auth.login

data class LoginUiState(
    val email: String = "",
    val isEmailValid: Boolean = false,
    val password: String = "",
    val isPasswordValid: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isFormValid: Boolean
        get() = isEmailValid && isPasswordValid && email.isNotBlank() && password.isNotBlank()
}