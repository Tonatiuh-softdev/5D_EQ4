package com.equipo4.nearhome.ui.publicacion.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipo4.nearhome.data.repository.Repositories
import com.equipo4.nearhome.domain.model.ListingType
import com.equipo4.nearhome.domain.model.Property
import com.equipo4.nearhome.domain.model.PropertyType
import com.equipo4.nearhome.domain.repository.PropiedadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailPublicacionViewModel(
    private val repository: PropiedadRepository = Repositories.propiedades
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailPublicacionUiState>(DetailPublicacionUiState.Loading)
    val uiState: StateFlow<DetailPublicacionUiState> = _uiState.asStateFlow()

    fun cargarPublicacion(id: String) {
        viewModelScope.launch {
            _uiState.value = DetailPublicacionUiState.Loading
            try {
                val property = repository.getById(id)
                if (property == null) {
                    _uiState.value = DetailPublicacionUiState.Error("No se encontró la publicación")
                } else {
                    _uiState.value = DetailPublicacionUiState.Success(property.toDetail())
                }
            } catch (e: Exception) {
                _uiState.value = DetailPublicacionUiState.Error("Error al cargar la publicación")
            }
        }
    }

    fun toggleGuardar() {
        val currentState = _uiState.value
        if (currentState is DetailPublicacionUiState.Success) {
            val pub = currentState.publicacion
            val nuevoEstadoGuardado = !pub.estaGuardado
            _uiState.value = DetailPublicacionUiState.Success(pub.copy(estaGuardado = nuevoEstadoGuardado))
            // Se guarda en el repositorio para que Lista, Mapa y Guardados lo reflejen.
            viewModelScope.launch { repository.setSaved(pub.id, nuevoEstadoGuardado) }
        }
    }
}

/** Adapta una propiedad del repositorio al modelo del detalle (descripción y vendedor son de prueba). */
private fun Property.toDetail(): PublicacionDetail {
    val tipoLabel = when (type) {
        PropertyType.CASA -> "Casa"
        PropertyType.DEPARTAMENTO -> "Departamento"
        PropertyType.TERRENO -> "Terreno"
    }
    return PublicacionDetail(
        id = id,
        titulo = title,
        descripcion = "Exclusivo inmueble de ${areaSqM}m² ubicado en una de las mejores zonas de Manzanillo. " +
                "Cuenta con una distribución ideal para disfrutar del confort y recibir invitados. " +
                "Cerca de comercios, escuelas y servicios. Agenda una visita y conócelo.",
        precio = price,
        esRenta = listingType == ListingType.RENTA,
        tipoPropiedad = tipoLabel,
        direccion = location,
        imagenes = images,
        metrosCuadrados = areaSqM,
        recamaras = bedrooms ?: 0,
        banos = bathrooms ?: 0,
        cocheras = garages ?: 0,
        caracteristicasColumna1 = listOf("Alberca", "Terraza"),
        caracteristicasColumna2 = listOf("Estancia mínima en días: 1", "No se permiten mascotas"),
        esPropietario = false,
        estaGuardado = isSaved,
        telefonoVendedor = "523121234567",
        emailVendedor = "vendedor@ejemplo.com"
    )
}
