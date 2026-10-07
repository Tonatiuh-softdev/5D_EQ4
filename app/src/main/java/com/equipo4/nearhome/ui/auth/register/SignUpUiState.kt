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
    private val allowedDomains = listOf("gmail", "hotmail", "outlook")

    val isEmailValid: Boolean
        get() {
            if (email.isBlank() || !email.contains("@")) return false
            val parts = email.split("@", limit = 2)
            if (parts.size != 2) return false

            val localPart = parts[0]
            val domainPart = parts[1].lowercase()

            // Debe tener más de 4 caracteres antes del '@'
            if (localPart.length <= 4) return false

            // Debe pertenecer a un dominio permitido (gmail, hotmail, outlook)
            val isAllowedDomain = allowedDomains.any { domain ->
                domainPart == domain || domainPart.startsWith("$domain.")
            }
            if (!isAllowedDomain) return false

            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

    val showEmailError: Boolean
        get() = email.isNotEmpty() && !isEmailValid

    val emailErrorMessage: String?
        get() {
            if (email.isEmpty()) return null
            if (!email.contains("@")) return "El correo debe incluir '@'"

            val parts = email.split("@", limit = 2)
            val localPart = parts[0]
            val domainPart = parts.getOrNull(1)?.lowercase().orEmpty()

            if (localPart.length <= 4) {
                return "Debe ingresar más de 4 caracteres antes del '@'"
            }

            val isAllowedDomain = allowedDomains.any { domain ->
                domainPart == domain || domainPart.startsWith("$domain.")
            }

            if (!isAllowedDomain) {
                return "Solo se permiten dominios gmail, hotmail u outlook"
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return "Formato de correo electrónico inválido"
            }

            return null
        }

    val isPasswordValid: Boolean get() = password.length >= 6
    val doPasswordsMatch: Boolean get() = password.isNotEmpty() && password == confirmPassword
    val isFormValid: Boolean get() = isEmailValid && isPasswordValid && doPasswordsMatch
    val isOtpComplete: Boolean get() = otpCode.all { it.isNotEmpty() }

    val showPasswordMismatchError: Boolean
        get() = confirmPassword.isNotEmpty() && !doPasswordsMatch
}