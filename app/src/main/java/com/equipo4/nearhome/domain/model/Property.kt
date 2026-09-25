package com.equipo4.nearhome.domain.model

enum class ListingType {
    RENTA,
    VENTA
}

enum class PropertyType {
    DEPARTAMENTO,
    CASA,
    TERRENO
}

data class Property(
    val id: String,
    val title: String,
    val price: Double,
    val listingType: ListingType,
    val location: String,
    val type: PropertyType,
    val areaSqM: Int,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val garages: Int? = null,
    val frontMeters: Double? = null,
    val depthMeters: Double? = null,
    val images: List<Int> = emptyList(),
    val isSaved: Boolean = false
)