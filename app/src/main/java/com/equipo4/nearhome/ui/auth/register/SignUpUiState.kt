package com.equipo4.nearhome.ui.auth.register

enum class SignUpStep {
    FORM,
    OTP_VERIFICATION,
    SUCCESS
}

data class SignUpUiState(
    val step: SignUpStep = SignUpStep.FORM,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val otpCode: List<String> = List(6) { "" },
    val isOtpError: Boolean = false,
    val otpErrorMessage: String? = null,
    val resendCountdown: Int = 30,
    val canResendCode: Boolean = false,
    val isLoading: Boolean = false
) {
    val isEmailValid: Boolean get() = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid: Boolean get() = password.length >= 6
    val doPasswordsMatch: Boolean get() = password.isNotEmpty() && password == confirmPassword
    val isFormValid: Boolean get() = isEmailValid && isPasswordValid && doPasswordsMatch
    val isOtpComplete: Boolean get() = otpCode.all { it.isNotEmpty() }

    val showPasswordMismatchError: Boolean
        get() = confirmPassword.isNotEmpty() && !doPasswordsMatch
}