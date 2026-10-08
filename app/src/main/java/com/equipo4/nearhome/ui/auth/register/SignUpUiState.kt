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
    val isTermsAccepted: Boolean = false,
    val showTermsError: Boolean = false,
    val otpCode: List<String> = List(6) { "" },
    val isOtpError: Boolean = false,
    val otpErrorMessage: String? = null,
    val resendCountdown: Int = 30,
    val canResendCode: Boolean = false,
    val isLoading: Boolean = false
) {
    private val allowedDomains = listOf("gmail", "hotmail", "outlook", "ucol")
    private val allowedExtensions = listOf("com", "mx", "edu.mx", "org", "net")

    private fun isDomainBaseAllowed(domainPart: String): Boolean {
        return allowedDomains.any { domain ->
            domainPart == domain || domainPart.startsWith("$domain.")
        }
    }

    private fun hasValidDomainExtension(domainPart: String): Boolean {
        return allowedDomains.any { domain ->
            allowedExtensions.any { ext ->
                domainPart == "$domain.$ext"
            }
        }
    }

    // Validaciones de Correo
    val isEmailValid: Boolean
        get() {
            if (email.isBlank() || !email.contains("@")) return false
            val parts = email.split("@", limit = 2)
            if (parts.size != 2) return false

            val localPart = parts[0]
            val domainPart = parts[1].lowercase()

            if (localPart.isEmpty() || domainPart.isEmpty()) return false
            if (!hasValidDomainExtension(domainPart)) return false

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

            if (localPart.isEmpty()) {
                return "Debe ingresar el correo antes del '@'"
            }

            if (domainPart.isEmpty()) {
                return "Debe ingresar el dominio después del '@'"
            }

            if (!isDomainBaseAllowed(domainPart)) {
                return "Solo se permiten dominios gmail, hotmail, outlook o ucol"
            }

            if (!hasValidDomainExtension(domainPart)) {
                return "Debe incluir una extensión válida (ej: .com, .mx, .edu.mx)"
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return "Formato de correo electrónico inválido"
            }

            return null
        }

    // Validaciones de Contraseña
    private val hasMinLength: Boolean get() = password.length >= 8
    private val hasLowercase: Boolean get() = password.any { it.isLowerCase() }
    private val hasUppercase: Boolean get() = password.any { it.isUpperCase() }
    private val hasDigit: Boolean get() = password.any { it.isDigit() }
    private val hasNoRepeatedDigits: Boolean get() = !Regex("([0-9])\\1").containsMatchIn(password)

    val isPasswordValid: Boolean
        get() = hasMinLength && hasLowercase && hasUppercase && hasDigit && hasNoRepeatedDigits

    val showPasswordError: Boolean
        get() = password.isNotEmpty() && !isPasswordValid

    val passwordErrorMessage: String?
        get() {
            if (password.isEmpty()) return null
            if (!hasMinLength) return "La contraseña debe tener al menos 8 caracteres"
            if (!hasLowercase) return "Debe incluir al menos una letra minúscula"
            if (!hasUppercase) return "Debe incluir al menos una letra mayúscula"
            if (!hasDigit) return "Debe incluir al menos un número"
            if (!hasNoRepeatedDigits) return "No puede tener números repetidos seguidos (ej: 11, 22)"
            return null
        }

    val doPasswordsMatch: Boolean get() = password.isNotEmpty() && password == confirmPassword
    val areFieldsValid: Boolean get() = isEmailValid && isPasswordValid && doPasswordsMatch
    val isFormValid: Boolean get() = areFieldsValid && isTermsAccepted
    val isOtpComplete: Boolean get() = otpCode.all { it.isNotEmpty() }

    val showPasswordMismatchError: Boolean
        get() = confirmPassword.isNotEmpty() && !doPasswordsMatch
}