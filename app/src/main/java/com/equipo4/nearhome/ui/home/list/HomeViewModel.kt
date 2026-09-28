package com.equipo4.nearhome.ui.home.list

import androidx.lifecycle.ViewModel
import com.equipo4.nearhome.domain.model.ListingType
import com.equipo4.nearhome.domain.model.Property
import com.equipo4.nearhome.domain.model.PropertyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMockProperties()
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }

    fun toggleSaveProperty(propertyId: String) {
        _uiState.update { currentState ->
            val updatedList = currentState.properties.map { property ->
                if (property.id == propertyId) {
                    property.copy(isSaved = !property.isSaved)
                } else {
                    property
                }
            }
            currentState.copy(properties = updatedList)
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    private fun loadMockProperties() {
        val mockList = listOf(
            Property(
                id = "1",
                title = "DEPARTAMENTO AMUEBLADO EN RENTA",
                price = 18500.0,
                listingType = ListingType.RENTA,
                location = "Av. Elías Zamora Verduzco, Manzanillo, Colima",
                type = PropertyType.DEPARTAMENTO,
                areaSqM = 110,
                bedrooms = 3,
                bathrooms = 2,
                garages = 2,
                images = listOf(1, 2, 3, 4, 5),
                isSaved = false
            ),
            Property(
                id = "2",
                title = "DEPARTAMENTO AMUEBLADO EN RENTA",
                price = 890000.0,
                listingType = ListingType.VENTA,
                location = "Av. Elías Zamora Verduzco, Manzanillo, Colima",
                type = PropertyType.DEPARTAMENTO,
                areaSqM = 85,
                bedrooms = 2,
                bathrooms = 1,
                garages = 1,
                images = listOf(1, 2, 3, 4, 5),
                isSaved = true
            ),
            Property(
                id = "3",
                title = "CASA RESIDENCIAL REAL VISTA",
                price = 2450000.0,
                listingType = ListingType.VENTA,
                location = "Villa de Álvarez, Colima",
                type = PropertyType.CASA,
                areaSqM = 160,
                bedrooms = 3,
                bathrooms = 2,
                garages = 2,
                images = listOf(1, 2, 3),
                isSaved = false
            )
        )

        _uiState.update { it.copy(properties = mockList) }
    }
}