package com.equipo4.nearhome.ui.auth.completeprofile

import androidx.lifecycle.ViewModel
import com.equipo4.nearhome.domain.model.Country
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val MOCK_COUNTRIES = listOf(
    Country("Estados Unidos", "+1", "🇺🇸", "US"),
    Country("Canadá", "+1", "🇨🇦", "CA"),
    Country("Colombia", "+57", "🇨🇴", "CO"),
    Country("Argentina", "+54", "🇦🇷", "AR"),
    Country("México", "+52", "🇲🇽", "MX"),
    Country("Japón", "+81", "🇯🇵", "JP")
)

class CompleteProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        CompleteProfileUiState(filteredCountries = MOCK_COUNTRIES)
    )
    val uiState: StateFlow<CompleteProfileUiState> = _uiState.asStateFlow()

    // Banderas para saber si el usuario REALMENTE entró al campo al menos una vez
    private var firstNameHadFocus = false
    private var firstLastNameHadFocus = false
    private var phoneHadFocus = false

    fun onFirstNameChanged(value: String) {
        _uiState.update { currentState ->
            validateForm(currentState.copy(firstName = value))
        }
    }

    fun onFirstNameFocusChanged(isFocused: Boolean) {
        if (isFocused) {
            firstNameHadFocus = true
        } else if (firstNameHadFocus) { // Solo si ya había entrado previamente
            _uiState.update { currentState ->
                validateForm(currentState.copy(firstNameTouched = true))
            }
        }
    }

    fun onFirstLastNameChanged(value: String) {
        _uiState.update { currentState ->
            validateForm(currentState.copy(firstLastName = value))
        }
    }

    fun onFirstLastNameFocusChanged(isFocused: Boolean) {
        if (isFocused) {
            firstLastNameHadFocus = true
        } else if (firstLastNameHadFocus) { // Solo si ya había entrado previamente
            _uiState.update { currentState ->
                validateForm(currentState.copy(firstLastNameTouched = true))
            }
        }
    }

    fun onSecondLastNameChanged(value: String) {
        _uiState.update { it.copy(secondLastName = value) }
    }

    fun onBirthDateSelected(dateFormatted: String) {
        _uiState.update { currentState ->
            validateForm(currentState.copy(birthDate = dateFormatted, birthDateTouched = true))
        }
    }

    fun onPhoneNumberChanged(value: String) {
        val cleanNumber = value.filter { it.isDigit() || it == '-' }
        _uiState.update { currentState ->
            validateForm(currentState.copy(phoneNumber = cleanNumber))
        }
    }

    fun onPhoneFocusChanged(isFocused: Boolean) {
        if (isFocused) {
            phoneHadFocus = true
        } else if (phoneHadFocus) { // Solo si ya había entrado previamente
            _uiState.update { currentState ->
                validateForm(currentState.copy(phoneTouched = true))
            }
        }
    }

    fun onOpenBottomSheet() {
        _uiState.update {
            it.copy(
                isBottomSheetOpen = true,
                countrySearchQuery = "",
                filteredCountries = MOCK_COUNTRIES
            )
        }
    }

    fun onCloseBottomSheet() {
        _uiState.update { it.copy(isBottomSheetOpen = false) }
    }

    fun onCountrySearchQueryChanged(query: String) {
        val filtered = if (query.isBlank()) {
            MOCK_COUNTRIES
        } else {
            MOCK_COUNTRIES.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.code.contains(query, ignoreCase = true)
            }
        }
        _uiState.update {
            it.copy(
                countrySearchQuery = query,
                filteredCountries = filtered
            )
        }
    }

    fun onCountrySelected(country: Country) {
        _uiState.update { currentState ->
            validateForm(
                currentState.copy(
                    selectedCountry = country,
                    isBottomSheetOpen = false
                )
            )
        }
    }

    fun onContinueClicked(): Boolean {
        _uiState.update { currentState ->
            validateForm(
                currentState.copy(
                    firstNameTouched = true,
                    firstLastNameTouched = true,
                    birthDateTouched = true,
                    phoneTouched = true,
                    isSubmittedAttempted = true
                )
            )
        }
        return _uiState.value.isFormValid
    }

    private fun validateForm(state: CompleteProfileUiState): CompleteProfileUiState {
        val showFirstNameErr = (state.firstNameTouched || state.isSubmittedAttempted) && state.firstName.isBlank()
        val showFirstLastNameErr = (state.firstLastNameTouched || state.isSubmittedAttempted) && state.firstLastName.isBlank()
        val showBirthDateErr = (state.birthDateTouched || state.isSubmittedAttempted) && state.birthDate.isBlank()

        // Validación de teléfono (10 dígitos)
        val digitsOnly = state.phoneNumber.filter { it.isDigit() }
        val isPhoneEmpty = state.phoneNumber.isBlank()
        val isPhoneInvalidFormat = digitsOnly.length != 10 && !isPhoneEmpty

        val showPhoneErr = (state.phoneTouched || state.isSubmittedAttempted) && (isPhoneEmpty || isPhoneInvalidFormat)
        val phoneErrText = if (showPhoneErr) {
            if (isPhoneInvalidFormat) "Formato inválido (10 dígitos)" else "El teléfono es obligatorio"
        } else null

        val isOverallValid = state.firstName.isNotBlank() &&
                state.firstLastName.isNotBlank() &&
                state.birthDate.isNotBlank() &&
                digitsOnly.length == 10

        val showGeneral = (state.isSubmittedAttempted) && !isOverallValid

        return state.copy(
            isFirstNameError = showFirstNameErr,
            isFirstLastNameError = showFirstLastNameErr,
            isBirthDateError = showBirthDateErr,
            isPhoneError = showPhoneErr,
            phoneErrorMessage = phoneErrText,
            showGeneralError = showGeneral,
            isFormValid = isOverallValid
        )
    }
}