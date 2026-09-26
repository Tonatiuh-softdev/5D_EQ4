package com.equipo4.nearhome.ui.auth.completeprofile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo4.nearhome.domain.model.Country

private val PrimaryNavy = Color(0xFF0B2C4D)
private val DisabledButtonBg = Color(0xFFB0BEC5)
private val GrayInputBorder = Color(0xFFE0E0E0)
private val ErrorRed = Color(0xFFE53935)
private val SubtitleColor = Color(0xFF212121)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    viewModel: CompleteProfileViewModel = viewModel(),
    onCompleteProfileSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Validaciones de Mayúscula Inicial
    val isFirstNameCapitalInvalid = uiState.firstName.isNotEmpty() && !uiState.firstName.first().isUpperCase()
    val isFirstLastNameCapitalInvalid = uiState.firstLastName.isNotEmpty() && !uiState.firstLastName.first().isUpperCase()
    val isSecondLastNameCapitalInvalid = uiState.secondLastName.isNotEmpty() && !uiState.secondLastName.first().isUpperCase()

    // Validación de formato de Fecha de Nacimiento (DD/MM/AAAA)
    val birthDateRegex = Regex("""^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\d{4}$""")
    val isBirthDateFormatInvalid = uiState.birthDate.isNotEmpty() && !uiState.birthDate.matches(birthDateRegex)

    // Formulario válido sólo si todo está correcto Y la fecha cumple el formato completo
    val isFormValid = uiState.isFormValid &&
            !isFirstNameCapitalInvalid &&
            !isFirstLastNameCapitalInvalid &&
            !isSecondLastNameCapitalInvalid &&
            !isBirthDateFormatInvalid &&
            uiState.birthDate.matches(birthDateRegex)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Título y Subtítulo
            Text(
                text = "Completa tu perfil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ingresa tus datos personales para continuar.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SubtitleColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo: Nombre(s)
            ProfileInputField(
                labelText = "Nombre(s):",
                isRequired = true,
                value = uiState.firstName,
                onValueChange = viewModel::onFirstNameChanged,
                placeholder = "Tu nombre",
                isError = uiState.isFirstNameError || isFirstNameCapitalInvalid,
                errorMessage = if (isFirstNameCapitalInvalid) {
                    "El nombre debe iniciar con mayúscula"
                } else {
                    "El nombre es obligatorio"
                },
                onFocusChanged = viewModel::onFirstNameFocusChanged
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Campo: Primer Apellido
            ProfileInputField(
                labelText = "Primer Apellido:",
                isRequired = true,
                value = uiState.firstLastName,
                onValueChange = viewModel::onFirstLastNameChanged,
                placeholder = "Tu primer apellido",
                isError = uiState.isFirstLastNameError || isFirstLastNameCapitalInvalid,
                errorMessage = if (isFirstLastNameCapitalInvalid) {
                    "El primer apellido debe iniciar con mayúscula"
                } else {
                    "El primer apellido es obligatorio"
                },
                onFocusChanged = viewModel::onFirstLastNameFocusChanged
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Campo: Segundo Apellido (Opcional)
            ProfileInputField(
                labelText = "Segundo Apellido:",
                isRequired = false,
                value = uiState.secondLastName,
                onValueChange = viewModel::onSecondLastNameChanged,
                placeholder = "(opcional)",
                isError = isSecondLastNameCapitalInvalid,
                errorMessage = if (isSecondLastNameCapitalInvalid) "El segundo apellido debe iniciar con mayúscula" else null
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Campo: Fecha de nacimiento
            Column(modifier = Modifier.fillMaxWidth()) {
                FormLabel(text = "Fecha de nacimiento:", isRequired = true)
                Spacer(modifier = Modifier.height(6.dp))

                CustomOutlinedDatePicker(
                    value = uiState.birthDate,
                    onDateSelected = viewModel::onBirthDateSelected,
                    isError = uiState.isBirthDateError || isBirthDateFormatInvalid,
                    placeholder = "DD/MM/AAAA"
                )

                if (uiState.isBirthDateError || isBirthDateFormatInvalid) {
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorIconTextMessage(
                        message = if (uiState.birthDate.isEmpty()) {
                            "La fecha de nacimiento es obligatoria"
                        } else {
                            "Formato de fecha inválido (DD/MM/AAAA)"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Campo: Teléfono
            Column(modifier = Modifier.fillMaxWidth()) {
                FormLabel(text = "Teléfono:", isRequired = true)
                Spacer(modifier = Modifier.height(6.dp))

                PhoneInputField(
                    countryFlag = uiState.selectedCountry.flag,
                    countryCode = uiState.selectedCountry.code,
                    phoneNumber = uiState.phoneNumber,
                    onPhoneNumberChange = viewModel::onPhoneNumberChanged,
                    onCountryClick = viewModel::onOpenBottomSheet,
                    isError = uiState.isPhoneError,
                    onFocusChanged = viewModel::onPhoneFocusChanged
                )

                if (uiState.isPhoneError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    ErrorIconTextMessage(
                        message = uiState.phoneErrorMessage ?: if (uiState.phoneNumber.isEmpty()) {
                            "El número telefónico es obligatorio"
                        } else {
                            "Formato inválido (10 dígitos)"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Continuar
            Button(
                onClick = {
                    val isValid = viewModel.onContinueClicked()
                    if (isValid && isFormValid) {
                        onCompleteProfileSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) PrimaryNavy else DisabledButtonBg,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Continuar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Mensaje de error en rojo debajo del botón
            if (uiState.showGeneralError && !isFormValid) {
                Spacer(modifier = Modifier.height(10.dp))
                ErrorIconTextMessage(message = "Por favor, complete los campos obligatorios.")
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }

    // Modal BottomSheet para países
    if (uiState.isBottomSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = viewModel::onCloseBottomSheet
        ) {
            CountrySelectionContent(
                searchQuery = uiState.countrySearchQuery,
                onSearchQueryChange = viewModel::onCountrySearchQueryChanged,
                countries = uiState.filteredCountries,
                onCountrySelected = viewModel::onCountrySelected
            )
        }
    }
}

@Composable
private fun FormLabel(text: String, isRequired: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        if (isRequired) {
            Text(
                text = " *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ErrorRed
            )
        }
    }
}

@Composable
private fun ErrorIconTextMessage(message: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = ErrorRed,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = message,
            color = ErrorRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ProfileInputField(
    labelText: String,
    isRequired: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    errorMessage: String? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FormLabel(text = labelText, isRequired = isRequired)
        Spacer(modifier = Modifier.height(6.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            decorationBox = { innerTextField ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isError) ErrorRed else GrayInputBorder
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(placeholder, color = Color.LightGray, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    onFocusChanged?.invoke(focusState.isFocused)
                }
        )

        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            ErrorIconTextMessage(message = errorMessage)
        }
    }
}

@Composable
private fun PhoneInputField(
    countryFlag: String,
    countryCode: String,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    onCountryClick: () -> Unit,
    isError: Boolean,
    onFocusChanged: (Boolean) -> Unit
) {
    val rawDigits = phoneNumber.filter { it.isDigit() }.take(10)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = BorderStroke(
            width = 1.dp,
            color = if (isError) ErrorRed else GrayInputBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable { onCountryClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = countryCode, fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = countryFlag, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar País",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            BasicTextField(
                value = rawDigits,
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(10)
                    onPhoneNumberChange(digits)
                },
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = PhoneVisualTransformation(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (phoneNumber.isEmpty()) {
                            Text("123-456-7890", color = Color.LightGray, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        onFocusChanged(focusState.isFocused)
                    }
            )
        }
    }
}

@Composable
private fun CustomOutlinedDatePicker(
    value: String,
    onDateSelected: (String) -> Unit,
    isError: Boolean,
    placeholder: String
) {
    val rawDigits = value.filter { it.isDigit() }.take(8)

    BasicTextField(
        value = rawDigits,
        onValueChange = { input ->
            val digits = input.filter { it.isDigit() }.take(8)
            val formattedDate = buildString {
                for (i in digits.indices) {
                    append(digits[i])
                    if ((i == 1 || i == 3) && i != digits.lastIndex) {
                        append('/')
                    }
                }
            }
            onDateSelected(formattedDate)
        },
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        visualTransformation = DateVisualTransformation(),
        decorationBox = { innerTextField ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isError) ErrorRed else GrayInputBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(placeholder, color = Color.LightGray, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Fecha",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

// Transformación visual para Fecha de Nacimiento (DD/MM/AAAA)
private class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text.filter { it.isDigit() }.take(8)

        val out = buildString {
            for (i in raw.indices) {
                append(raw[i])
                if (i == 1 || i == 3) {
                    append('/')
                }
            }
        }

        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val transformedOffset = when {
                    offset <= 1 -> offset
                    offset <= 3 -> offset + 1
                    else -> offset + 2
                }
                return transformedOffset.coerceIn(0, out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val originalOffset = when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset - 1
                    else -> offset - 2
                }
                return originalOffset.coerceIn(0, raw.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetTranslator)
    }
}

// Transformación visual para Teléfono (XXX-XXX-XXXX)
private class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text.filter { it.isDigit() }.take(10)

        val out = buildString {
            for (i in raw.indices) {
                append(raw[i])
                if (i == 2 || i == 5) {
                    append('-')
                }
            }
        }

        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val transformedOffset = when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset + 1
                    else -> offset + 2
                }
                return transformedOffset.coerceIn(0, out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val originalOffset = when {
                    offset <= 3 -> offset
                    offset <= 7 -> offset - 1
                    else -> offset - 2
                }
                return originalOffset.coerceIn(0, raw.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetTranslator)
    }
}

@Composable
private fun CountrySelectionContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    countries: List<Country>,
    onCountrySelected: (Country) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Seleccionar País",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Buscar país o código...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .verticalScroll(rememberScrollState())
        ) {
            countries.forEach { country ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCountrySelected(country) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = country.flag, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = country.name,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = country.code, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
            }
        }
    }
}