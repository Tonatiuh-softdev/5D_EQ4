package com.equipo4.nearhome.ui.profile

enum class UserRole(val label: String) {
    COMPRADOR("Comprador"),
    VENDEDOR("Vendedor"),
    ADMIN("Administrador")
}

val LanguageOptions = listOf("Español", "Inglés")

data class ProfileUiState(
    val userName: String = "Anthony Edward Stark",
    val role: UserRole = UserRole.VENDEDOR,
    val language: String = "Español",
    val darkModeEnabled: Boolean = false
) {
    val darkModeLabel: String get() = if (darkModeEnabled) "Activado" else "Desactivado"

    /** "Mis anuncios" solo lo ven vendedores y administradores. */
    val canManageListings: Boolean
        get() = role == UserRole.VENDEDOR || role == UserRole.ADMIN
}