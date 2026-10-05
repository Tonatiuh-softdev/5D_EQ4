package com.equipo4.nearhome.ui.auth.completeprofile

import androidx.lifecycle.ViewModel
import com.equipo4.nearhome.domain.model.Country
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    private var firstNameHadFocus = false
    private var firstLastNameHadFocus = false
    private var phoneHadFocus = false

    fun onFirstNameChanged(value: String) {
        val filteredValue = value.filter { it.isLetter() || it.isWhitespace() }
        _uiState.update { currentState ->
            validateForm(currentState.copy(firstName = filteredValue))
        }
    }

    fun onFirstNameFocusChanged(isFocused: Boolean) {
        if (isFocused) {
            firstNameHadFocus = true
        } else if (firstNameHadFocus) {
            _uiState.update { currentState ->
                validateForm(currentState.copy(firstNameTouched = true))
            }
        }
    }

    fun onFirstLastNameChanged(value: String) {
        val filteredValue = value.filter { it.isLetter() || it.isWhitespace() }
        _uiState.update { currentState ->
            validateForm(currentState.copy(firstLastName = filteredValue))
        }
    }

    fun onFirstLastNameFocusChanged(isFocused: Boolean) {
        if (isFocused) {
            firstLastNameHadFocus = true
        } else if (firstLastNameHadFocus) {
            _uiState.update { currentState ->
                validateForm(currentState.copy(firstLastNameTouched = true))
            }
        }
    }

    fun onSecondLastNameChanged(value: String) {
        val filteredValue = value.filter { it.isLetter() || it.isWhitespace() }
        _uiState.update { it.copy(secondLastName = filteredValue) }
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
        } else if (phoneHadFocus) {
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

        // Validaciones de fecha de nacimiento
        val birthDateErrText = getBirthDateErrorMessage(state.birthDate)
        val isBirthDateValid = birthDateErrText == null

        val showBirthDateErr = (state.birthDateTouched || state.isSubmittedAttempted) && !isBirthDateValid

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
                isBirthDateValid &&
                digitsOnly.length == 10

        val showGeneral = (state.isSubmittedAttempted) && !isOverallValid

        return state.copy(
            isFirstNameError = showFirstNameErr,
            isFirstLastNameError = showFirstLastNameErr,
            isBirthDateError = showBirthDateErr,
            birthDateErrorMessage = if (showBirthDateErr) birthDateErrText else null,
            isPhoneError = showPhoneErr,
            phoneErrorMessage = phoneErrText,
            showGeneralError = showGeneral,
            isFormValid = isOverallValid
        )
    }

    private fun getBirthDateErrorMessage(birthDate: String): String? {
        if (birthDate.isBlank()) return "La fecha de nacimiento es obligatoria"
        if (birthDate.length < 10) return "Formato de fecha incompleto (DD/MM/AAAA)"

        // 1. Validar que la fecha exista físicamente en el calendario (rechaza día 32, mes 13, etc.)
        if (!isValidCalendarDate(birthDate)) {
            return "La fecha ingresada no existe (día o mes inválido)"
        }

        // 2. Validar edad exacta contra el día actual (debe tener entre 18 y 80 años)
        val age = calculateExactAge(birthDate)
        if (age < 18 || age > 80) {
            return "Debes tener entre 18 y 80 años"
        }

        return null
    }

    private fun isValidCalendarDate(dateStr: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            sdf.isLenient = false
            sdf.parse(dateStr)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun calculateExactAge(birthDateStr: String): Int {
        val parts = birthDateStr.split("/")
        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()

        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val currentMonth = today.get(Calendar.MONTH) + 1
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        var age = currentYear - year
        if (currentMonth < month || (currentMonth == month && currentDay < day)) {
            age--
        }
        return age
    }
}