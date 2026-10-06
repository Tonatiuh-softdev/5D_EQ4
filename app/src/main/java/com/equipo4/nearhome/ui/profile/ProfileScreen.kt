package com.equipo4.nearhome.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo4.nearhome.ui.common.NearHomeBottomBar

private val SectionBg = Color(0xFFF2F2F2)
private val SectionText = Color(0xFF4B4B4B)
private val DividerColor = Color(0xFFE5E7EB)
private val TextDark = Color(0xFF1F2937)
private val ArrowGray = Color(0xFF9CA3AF)
private val ChipBg = Color(0xFFE6F2EA)
private val ChipText = Color(0xFF2E7D4F)
private val AvatarBg = Color(0xFFE5E7EB)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onMyListingsClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onBottomTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { NearHomeBottomBar(selectedTab = 3, onTabSelected = onBottomTabSelected) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(uiState)

            SectionHeader("Perfil")
            if (uiState.canManageListings) {
                ProfileRow(Icons.Outlined.Verified, "Mis anuncios", showDivider = true, onClick = onMyListingsClick)
            }
            ProfileRow(Icons.Outlined.Settings, "Administración de la cuenta", onClick = onAccountClick)

            SectionHeader("Ajustes")
            SettingRow(
                icon = Icons.Outlined.Language,
                label = "Idioma",
                value = uiState.language,
                options = LanguageOptions,
                showDivider = true,
                onSelect = viewModel::onLanguageSelected
            )
            SettingRow(
                icon = Icons.Outlined.DarkMode,
                label = "Modo oscuro",
                value = uiState.darkModeLabel,
                options = listOf("Activado", "Desactivado"),
                onSelect = { viewModel.onDarkModeSelected(it == "Activado") }
            )

            SectionHeader("Información")
            ProfileRow(Icons.Outlined.Description, "Terminos y condiciones", showDivider = true, onClick = onTermsClick)
            ProfileRow(Icons.Outlined.MenuBook, "Politicas y privacidad", onClick = onPrivacyClick)

            SectionHeader("Sesión")
            ProfileRow(Icons.AutoMirrored.Outlined.Logout, "Cerrar sesión", showDivider = true, onClick = onLogoutClick)

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileHeader(uiState: ProfileUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sin foto de perfil real todavía: círculo con ícono de persona(se agregara de backend).
        Box(
            modifier = Modifier.size(60.dp).background(AvatarBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(34.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(uiState.userName, fontSize = 16.sp, color = TextDark)
            Spacer(Modifier.height(4.dp))
            Surface(color = ChipBg, shape = RoundedCornerShape(10.dp)) {
                Text(
                    uiState.role.label,
                    color = ChipText,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = SectionText,
        modifier = Modifier
            .fillMaxWidth()
            .background(SectionBg)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

@Composable
private fun ProfileRow(
    icon: ImageVector,
    label: String,
    showDivider: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .height(52.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = TextDark, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(14.dp))
            Text(label, fontSize = 14.sp, color = TextDark, modifier = Modifier.weight(1f))
            trailing?.invoke()
        }
        if (showDivider) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = DividerColor)
        }
    }
}

/** Fila de ajuste con valor y flechita; al tocarla despliega las opciones. */
@Composable
private fun SettingRow(
    icon: ImageVector,
    label: String,
    value: String,
    options: List<String>,
    showDivider: Boolean = false,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ProfileRow(
        icon = icon,
        label = label,
        showDivider = showDivider,
        onClick = { expanded = true },
        trailing = {
            Box {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = ArrowGray)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = Color.White
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    fontSize = 14.sp,
                                    fontWeight = if (option == value) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = { onSelect(option); expanded = false }
                        )
                    }
                }
            }
        }
    )
}