package com.equipo4.nearhome.ui.auth.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhoneVerificationUiState(
    val phoneNumber: String = "+52 123-456-7890",
    val otpCode: String = "",
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val timerSeconds: Int = 0,
    val canResend: Boolean = true, // Inicia habilitado para que solo arranque al pulsarlo
    val isVerified: Boolean = false
)

class PhoneVerificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneVerificationUiState())
    val uiState: StateFlow<PhoneVerificationUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    // Asignar el número de teléfono recibido de la pantalla anterior
    fun setPhoneNumber(number: String) {
        if (number.isNotEmpty()) {
            _uiState.update { it.copy(phoneNumber = number) }
        }
    }

    fun onOtpCodeChanged(newCode: String) {
        if (newCode.length <= 6 && newCode.all { it.isDigit() }) {
            _uiState.update {
                it.copy(
                    otpCode = newCode,
                    isError = false,
                    errorMessage = null
                )
            }
        }
    }

    fun onVerifyClicked() {
        val currentCode = _uiState.value.otpCode

        // CÓDIGO SIMBÓLICO PARA PRUEBAS: "123456"
        if (currentCode == "123456") {
            _uiState.update { it.copy(isVerified = true, isError = false) }
        } else {
            _uiState.update {
                it.copy(
                    isError = true,
                    errorMessage = "El código es incorrecto"
                )
            }
        }
    }

    // Se activa ÚNICAMENTE cuando el usuario presiona "Reenviar SMS"
    fun onResendSmsClicked() {
        if (_uiState.value.canResend) {
            _uiState.update {
                it.copy(
                    otpCode = "",
                    isError = false,
                    errorMessage = null,
                    canResend = false,
                    timerSeconds = 30
                )
            }
            startResendTimer()
        }
    }

    private fun startResendTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSeconds > 0) {
                delay(1000L)
                _uiState.update { it.copy(timerSeconds = it.timerSeconds - 1) }
            }
            _uiState.update { it.copy(canResend = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}