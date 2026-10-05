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
                        content = "Esta es la cláusula que brinda mayor protección a la plataforma frente a disputas legales. Sin ella, NearHouse podría percibirse como responsable de una transacción fallida entre dos usuarios (por ejemplo, un fraude en una renta). Por ello se establece explícitamente que la plataforma no es parte de la transacción ni garantiza la veracidad de las publicaciones. Es una cláusula estándar en plataformas tipo marketplace (Mercado Libre, Airbnb, entre otras, usan cláusulas equivalentes)."
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
                        content = "Se define un plazo de 15 días de aviso previo, por ser un periodo razonable (ni demasiado corto ni excesivamente largo), y se establece que la notificación se realiza dentro de la app, consistente con el funcionamiento real de NearHouse."
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
                title = "Política de privacidad",
                lastUpdated = "3 de octubre de 2026",
                subtitle = "Bienvenido a NearHouse. Este Aviso de Privacidad aplica a la aplicación NearHouse y a los servicios relacionados con ella (la \"Plataforma\"), desarrollada como proyecto académico (\"NearHouse\", \"nosotros\" o \"la app\").\n\nNos comprometemos a proteger tu privacidad. Este aviso explica cómo recolectamos, usamos, compartimos y protegemos tu información personal dentro de la app. Si no estás de acuerdo con este aviso, te recomendamos no usar la Plataforma.",
                sections = listOf(
                    LegalSection(
                        id = 1,
                        title = "Qué datos recolecta la app",
                        content = "La lista parte de lo que NearHouse necesita funcionalmente para operar, no de una lista genérica tomada de otra app:\n\n• Nombre, apellidos, fecha de nacimiento: identifican a la persona y permiten confirmar que es mayor de edad (requisito establecido en los Términos).\n• Correo y teléfono: son los medios de contacto y recuperación de cuenta; el teléfono además se usa para verificación por SMS.\n• Ubicación: es un dato central en un portal inmobiliario, ya que sin ella no es posible mostrar propiedades cercanas ni ubicar un inmueble en el mapa.\n• Fotos de perfil e historial de publicaciones/favoritos/conversaciones: son datos generados por el uso de la app (no provienen de un formulario, se crean mientras se usa NearHouse), por lo que deben declararse de forma separada."
                    ),
                    LegalSection(
                        id = 2,
                        title = "Para qué se usa cada dato",
                        content = "La norma aplicable exige que cada dato tenga una finalidad específica y no una justificación vaga como \"para mejorar la experiencia\". Por eso, en el documento cada dato está ligado a una función concreta de la app (por ejemplo: fecha de nacimiento → verificación de edad mínima, no \"personalización\")."
                    ),
                    LegalSection(
                        id = 3,
                        title = "Con quién se comparte",
                        content = "No se trata de que NearHouse comparta datos de forma general, sino de los servicios externos necesarios para el funcionamiento de la app (autenticación con Google, verificación por SMS, pasarela de pagos, almacenamiento en la nube). Esto debe declararse de forma explícita porque la normativa exige señalar con qué terceros se comparte información y con qué fin, sin dejarlo implícito."
                    ),
                    LegalSection(
                        id = 4,
                        title = "Protección y tiempo de conservación",
                        content = "Se abordan dos aspectos: cómo se protege la información (cifrado de contraseña, acceso restringido) y durante cuánto tiempo se conserva (mientras la cuenta esté activa, más 30 días adicionales en caso de solicitud de eliminación). Este plazo es consistente con el flujo de \"eliminar cuenta\" definido previamente en el proyecto."
                    ),
                    LegalSection(
                        id = 5,
                        title = "Derechos ARCO",
                        content = "• A – Acceso: el usuario puede solicitar conocer qué datos se tienen sobre él.\n• R – Rectificación: puede corregir datos desactualizados o incorrectos (por ejemplo, su número de teléfono).\n• C – Cancelación: puede solicitar la eliminación de sus datos, lo cual corresponde a la eliminación de cuenta.\n• O – Oposición: puede solicitar que un dato específico no se use para cierta finalidad, sin necesidad de eliminar toda la cuenta.\n\nEstos derechos son de inclusión obligatoria conforme a la Ley Federal de Protección de Datos Personales en Posesión de los Particulares (LFPDPPP) en México."
                    ),
                    LegalSection(
                        id = 6,
                        title = "Menores de edad",
                        content = "Dado que el proyecto establece 18 años como edad mínima, la postura adoptada es que no se recolectan datos de menores de forma intencional. Permitir menores implicaría requisitos adicionales (consentimiento de los padres, verificación de edad), lo cual excede el alcance del proyecto."
                    ),
                    LegalSection(
                        id = 7,
                        title = "Analítica (Firebase)",
                        content = "Se declara su uso aunque se limite a estadísticas de uso, ya que cualquier herramienta que registre el comportamiento del usuario dentro de la app (pantallas visitadas, tiempo de uso) constituye tratamiento de datos y debe mencionarse, incluso si no identifica a la persona por nombre."
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