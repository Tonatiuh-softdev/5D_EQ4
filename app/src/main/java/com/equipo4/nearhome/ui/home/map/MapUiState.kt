package com.equipo4.nearhome.ui.home.map

import com.equipo4.nearhome.domain.model.Property
import com.equipo4.nearhome.domain.model.PropertyType

// TODO(confirmar con el equipo): opciones de "Ordenar". Supuesto razonable, aislado aquí.
enum class SortOption(val label: String) {
    NONE("Ordenar"),
    PRICE_ASC("Precio: menor a mayor"),
    PRICE_DESC("Precio: mayor a menor")
}

data class MapUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val allProperties: List<Property> = emptyList(),
    val searchQuery: String = "",
    /** Vacío = "Todo". */
    val selectedTypes: Set<PropertyType> = emptySet(),
    val sort: SortOption = SortOption.NONE,
    val selectedPropertyId: String? = null
) {
    /** Propiedades que se muestran como marcadores según búsqueda + filtros + orden. */
    val visibleProperties: List<Property>
        get() {
            val query = searchQuery.trim()
            return allProperties
                .asSequence()
                .filter { it.latitude != null && it.longitude != null }
                .filter { selectedTypes.isEmpty() || it.type in selectedTypes }
                .filter { query.isEmpty() || it.location.contains(query, ignoreCase = true) }
                .toList()
                .let {
                    when (sort) {
                        SortOption.NONE -> it
                        SortOption.PRICE_ASC -> it.sortedBy { p -> p.price }
                        SortOption.PRICE_DESC -> it.sortedByDescending { p -> p.price }
                    }
                }
        }

    val selectedProperty: Property?
        get() = allProperties.firstOrNull { it.id == selectedPropertyId }
}
