package com.equipo4.nearhome.ui.home.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipo4.nearhome.domain.model.ListingType
import com.equipo4.nearhome.domain.model.Property
import com.equipo4.nearhome.domain.model.PropertyType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadProperties()
    }

    fun loadProperties() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // TODO: reemplazar por el repositorio/backend cuando exista.
                delay(500)
                _uiState.update { it.copy(isLoading = false, allProperties = mockProperties()) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "No se pudieron cargar las propiedades")
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) = _uiState.update { it.copy(searchQuery = query) }

    /** "Todo" limpia la selección; seleccionar los tres tipos equivale a "Todo". */
    fun onTypeToggled(type: PropertyType?) {
        _uiState.update { state ->
            if (type == null) return@update state.copy(selectedTypes = emptySet())
            val next = if (type in state.selectedTypes) state.selectedTypes - type else state.selectedTypes + type
            state.copy(selectedTypes = if (next.size == PropertyType.entries.size) emptySet() else next)
        }
    }

    fun onSortSelected(sort: SortOption) = _uiState.update { it.copy(sort = sort) }

    fun onMarkerSelected(propertyId: String) = _uiState.update { it.copy(selectedPropertyId = propertyId) }

    fun onSheetDismissed() = _uiState.update { it.copy(selectedPropertyId = null) }

    fun toggleSaveProperty(propertyId: String) {
        _uiState.update { state ->
            state.copy(allProperties = state.allProperties.map {
                if (it.id == propertyId) it.copy(isSaved = !it.isSaved) else it
            })
        }
    }

    private fun mockProperties(): List<Property> = listOf(
        Property("1", "DEPARTAMENTO AMUEBLADO EN RENTA", 18500.0, ListingType.RENTA,
            "Av. Elías Zamora Verduzco, Manzanillo, Colima", PropertyType.DEPARTAMENTO, 110,
            bedrooms = 3, bathrooms = 2, garages = 2, images = listOf(1, 2, 3, 4, 5),
            latitude = 19.0985, longitude = -104.3120),
        Property("2", "DEPARTAMENTO VISTA AL MAR", 890000.0, ListingType.VENTA,
            "Av. Elías Zamora Verduzco, Manzanillo, Colima", PropertyType.DEPARTAMENTO, 85,
            bedrooms = 2, bathrooms = 1, garages = 1, images = listOf(1, 2, 3, 4, 5), isSaved = true,
            latitude = 19.0991, longitude = -104.3112),
        Property("3", "CASA RESIDENCIAL REAL VISTA", 2450000.0, ListingType.VENTA,
            "Col. Real Vista, Manzanillo, Colima", PropertyType.CASA, 160,
            bedrooms = 3, bathrooms = 2, garages = 2, images = listOf(1, 2, 3),
            latitude = 19.1210, longitude = -104.3450),
        Property("4", "CASA EN RENTA CERCA DE LA PLAYA", 14000.0, ListingType.RENTA,
            "Col. Las Brisas, Manzanillo, Colima", PropertyType.CASA, 120,
            bedrooms = 2, bathrooms = 2, garages = 1, images = listOf(1, 2, 3, 4),
            latitude = 19.1000, longitude = -104.3300),
        Property("5", "TERRENO PLANO EN VENTA", 650000.0, ListingType.VENTA,
            "Col. Salagua, Manzanillo, Colima", PropertyType.TERRENO, 300,
            frontMeters = 10.0, depthMeters = 30.0, images = listOf(1, 2),
            latitude = 19.1180, longitude = -104.3010),
        Property("6", "TERRENO COMERCIAL", 1200000.0, ListingType.VENTA,
            "Col. Valle de las Garzas, Manzanillo, Colima", PropertyType.TERRENO, 450,
            frontMeters = 15.0, depthMeters = 30.0, images = listOf(1, 2, 3),
            latitude = 19.0830, longitude = -104.2980),
        Property("7", "DEPARTAMENTO NUEVO EN RENTA", 12000.0, ListingType.RENTA,
            "Col. Centro, Manzanillo, Colima", PropertyType.DEPARTAMENTO, 70,
            bedrooms = 2, bathrooms = 1, garages = 1, images = listOf(1, 2, 3),
            latitude = 19.0531, longitude = -104.3188),
        Property("8", "CASA RESIDENCIAL EN VENTA", 1980000.0, ListingType.VENTA,
            "Col. Campos, Manzanillo, Colima", PropertyType.CASA, 140,
            bedrooms = 3, bathrooms = 2, garages = 2, images = listOf(1, 2, 3),
            latitude = 19.1100, longitude = -104.3250)
    )
}
