package com.equipo4.nearhome.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun onOtpDigitChanged(index: Int, digit: String) {
        if (digit.length <= 1) {
            val updatedOtp = _uiState.value.otpCode.toMutableList()
            updatedOtp[index] = digit
            _uiState.update { it.copy(otpCode = updatedOtp, isOtpError = false, otpErrorMessage = null) }
        }
    }

    fun onSubmitForm() {
        if (_uiState.value.isFormValid) {
            _uiState.update { it.copy(step = SignUpStep.OTP_VERIFICATION) }
            startResendTimer()
        }
    }

    fun onVerifyOtp() {
        val code = _uiState.value.otpCode.joinToString("")
        if (code == "123456") { // Código de ejemplo correcto
            _uiState.update { it.copy(step = SignUpStep.SUCCESS, isOtpError = false) }
        } else {
            _uiState.update {
                it.copy(
                    isOtpError = true,
                    otpErrorMessage = "El codigo es incorrecto"
                )
            }
        }
    }

    fun onResendCode() {
        if (_uiState.value.canResendCode) {
            _uiState.update { it.copy(canResendCode = false, resendCountdown = 30) }
            startResendTimer()
        }
    }

    fun onBackToForm() {
        _uiState.update { it.copy(step = SignUpStep.FORM) }
    }

    private fun startResendTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (i in 30 downTo 1) {
                _uiState.update { it.copy(resendCountdown = i) }
                delay(1000)
            }
            _uiState.update { it.copy(canResendCode = true) }
        }
    }
}