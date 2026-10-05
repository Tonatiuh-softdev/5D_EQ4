package com.equipo4.nearhome.ui.legal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LegalDocumentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LegalDocumentUiState())
    val uiState: StateFlow<LegalDocumentUiState> = _uiState.asStateFlow()

    fun loadTermsAndConditions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val termsData = LegalDocumentData(
                title = "Términos y condiciones",
                lastUpdated = "3 de octubre de 2026",
                subtitle = "Términos/Condiciones Generales - Todos los Usuarios",
                sections = listOf(
                    LegalSection(
                        id = 1,
                        title = "Edad mínima (18 años)",
                        content = "Se establece considerando que la plataforma gestiona transacciones económicas (renta/venta) y contacto directo entre personas desconocidas, un contexto donde no es recomendable permitir el acceso a menores de edad, a diferencia de una red social genérica."
                    ),
                    LegalSection(
                        id = 2,
                        title = "Reglas de conducta",
                        content = "Se derivan directamente de los riesgos ya identificados en el RF25 (información falsa, contenido ofensivo, fraude), y se agrega una regla adicional: contar con autorización para publicar el inmueble, para evitar que un usuario publique una propiedad sin permiso del propietario."
                    ),
                    LegalSection(
                        id = 3,
                        title = "Suspensión o eliminación de cuentas",
                        content = "Se conecta con el flujo de reportes ya diseñado (Reportar anuncio -> Reportes del Administrador) y con el flujo de eliminación de cuenta con plazo de 30 días, manteniendo coherencia entre los distintos módulos del proyecto."
                    ),
                    LegalSection(
                        id = 4,
                        title = "NearHouse como intermediario",
                        content = "Esta es la cláusula que brinda mayor protección a la plataforma frente a disputas legales. Sin ella, NearHome podría percibirse como responsable de una transacción fallida entre dos usuarios (por ejemplo, un fraude en una renta). Por ello se establece explícitamente que la plataforma no es parte de la transacción ni garantiza la veracidad de las publicaciones. Es una cláusula estándar en plataformas tipo marketplace (Mercado Libre, Airbnb, entre otras, usan cláusulas equivalentes)."
                    ),
                    LegalSection(
                        id = 5,
                        title = "Pagos",
                        content = "Se establecen dos reglas: los pagos por publicar o promocionar un anuncio no son reembolsables salvo error atribuible a la plataforma, y si una publicación pagada es eliminada por incumplir las reglas, tampoco procede reembolso. Esto evita que un usuario publique contenido indebido, pague, y luego solicite reembolso al ser removido."
                    ),
                    LegalSection(
                        id = 6,
                        title = "Límite de responsabilidad",
                        content = "Establece que la plataforma no es responsable de fraudes entre usuarios. Complementa la cláusula de intermediario, pero con un alcance más general, al cubrir no solo fraudes sino cualquier mala experiencia derivada del uso de la app."
                    ),
                    LegalSection(
                        id = 7,
                        title = "Modificación de términos y condiciones",
                        content = "Se define un plazo de 15 días de aviso previo, por ser un periodo razonable (ni demasiado corto ni excesivamente largo), y se establece que la notificación se realiza dentro de la app, consistente con el funcionamiento real de NearHome."
                    )
                )
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    documentData = termsData
                )
            }
        }
    }

    fun loadPrivacyPolicy() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val privacyData = LegalDocumentData(
                title = "Política de Privacidad",
                lastUpdated = "3 de octubre de 2026",
                subtitle = "Tratamiento de Datos Personales - NearHome",
                sections = listOf(
                    LegalSection(
                        id = 1,
                        title = "Recopilación de información",
                        content = "Recopilamos la información personal necesaria para la prestación del servicio de búsqueda y publicación de inmuebles..."
                    ),
                    LegalSection(
                        id = 2,
                        title = "Uso de los datos",
                        content = "Sus datos son utilizados para verificar identidades, facilitar el contacto entre usuarios y mejorar la plataforma..."
                    )
                )
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    documentData = privacyData
                )
            }
        }
    }

    fun onSectionChanged(index: Int) {
        _uiState.update { it.copy(activeSectionIndex = index) }
    }

    fun toggleDropdown(expanded: Boolean? = null) {
        _uiState.update {
            it.copy(isDropdownExpanded = expanded ?: !it.isDropdownExpanded)
        }
    }
}