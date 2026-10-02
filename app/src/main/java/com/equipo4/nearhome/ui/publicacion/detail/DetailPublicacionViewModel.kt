package com.equipo4.nearhome.ui.publicacion.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailPublicacionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DetailPublicacionUiState>(DetailPublicacionUiState.Loading)
    val uiState: StateFlow<DetailPublicacionUiState> = _uiState.asStateFlow()

    init {
        cargarPublicacion("pub_123")
    }

    fun cargarPublicacion(id: String) {
        viewModelScope.launch {
            _uiState.value = DetailPublicacionUiState.Loading
            try {
                // Simulación de respuesta del backend/repositorio
                val mockDetail = PublicacionDetail(
                    id = id,
                    titulo = "DEPARTAMENTO AMUEBLADO EN RENTA",
                    descripcion = "Exclusivo departamento AMUEBLADO de 50m² que ofrece una inigualable vista al mar y una distribución ideal para disfrutar del confort y dar hospedaje a varios invitados. Situa...",
                    precio = 10090.0,
                    esRenta = true,
                    tipoPropiedad = "Departamento",
                    direccion = "Península de Santiago, Manzanillo, Colima",
                    imagenes = listOf("https://placeholder.com/1", "https://placeholder.com/2", "https://placeholder.com/3"),
                    metrosCuadrados = 50,
                    recamaras = 3,
                    banos = 2,
                    cocheras = 2,
                    caracteristicasColumna1 = listOf("Alberca", "Terraza"),
                    caracteristicasColumna2 = listOf("Estancia mínima en días: 1", "No se permiten mascotas"),
                    esPropietario = false, // Determinado por sesión o backend
                    estaGuardado = false,
                    telefonoVendedor = "523121234567",
                    emailVendedor = "vendedor@ejemplo.com"
                )
                _uiState.value = DetailPublicacionUiState.Success(mockDetail)
            } catch (e: Exception) {
                _uiState.value = DetailPublicacionUiState.Error("Error al cargar la publicación")
            }
        }
    }

    fun toggleGuardar() {
        val currentState = _uiState.value
        if (currentState is DetailPublicacionUiState.Success) {
            val nuevoEstadoGuardado = !currentState.publicacion.estaGuardado
            // Aquí se realiza la llamada al backend
            _uiState.value = DetailPublicacionUiState.Success(
                currentState.publicacion.copy(estaGuardado = nuevoEstadoGuardado)
            )
        }
    }
}