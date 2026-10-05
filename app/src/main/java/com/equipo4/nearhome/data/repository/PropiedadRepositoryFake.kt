package com.equipo4.nearhome.data.repository

import com.equipo4.nearhome.domain.model.ListingType
import com.equipo4.nearhome.domain.model.Property
import com.equipo4.nearhome.domain.model.PropertyType
import com.equipo4.nearhome.domain.repository.PropiedadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** Implementación de prueba: datos fijos en memoria (se reinician al cerrar la app). */
class PropiedadRepositoryFake : PropiedadRepository {

    private val properties = MutableStateFlow(
        listOf(
            Property(
                id = "1", title = "DEPARTAMENTO AMUEBLADO EN RENTA", price = 18500.0,
                listingType = ListingType.RENTA,
                location = "Av. Elías Zamora Verduzco, Manzanillo, Colima",
                type = PropertyType.DEPARTAMENTO, areaSqM = 110,
                bedrooms = 3, bathrooms = 2, garages = 2,
                images = listOf(1, 2, 3, 4, 5), isSaved = true,
                latitude = 19.0985, longitude = -104.3120
            ),
            Property(
                id = "2", title = "DEPARTAMENTO AMUEBLADO EN RENTA", price = 890000.0,
                listingType = ListingType.VENTA,
                location = "Av. Elías Zamora Verduzco, Manzanillo, Colima",
                type = PropertyType.DEPARTAMENTO, areaSqM = 85,
                bedrooms = 2, bathrooms = 1, garages = 1,
                images = listOf(1, 2, 3, 4, 5), isSaved = true,
                latitude = 19.0991, longitude = -104.3112
            ),
            Property(
                id = "3", title = "CASA RESIDENCIAL REAL VISTA", price = 2450000.0,
                listingType = ListingType.VENTA,
                location = "Col. Real Vista, Manzanillo, Colima",
                type = PropertyType.CASA, areaSqM = 160,
                bedrooms = 3, bathrooms = 2, garages = 2,
                images = listOf(1, 2, 3), isSaved = false,
                latitude = 19.1210, longitude = -104.3450
            ),
            Property(
                id = "4", title = "CASA EN RENTA CERCA DE LA PLAYA", price = 14000.0,
                listingType = ListingType.RENTA,
                location = "Col. Las Brisas, Manzanillo, Colima",
                type = PropertyType.CASA, areaSqM = 120,
                bedrooms = 2, bathrooms = 2, garages = 1,
                images = listOf(1, 2, 3, 4), isSaved = true,
                latitude = 19.1000, longitude = -104.3300
            ),
            Property(
                id = "5", title = "TERRENO PLANO EN VENTA", price = 650000.0,
                listingType = ListingType.VENTA,
                location = "Col. Salagua, Manzanillo, Colima",
                type = PropertyType.TERRENO, areaSqM = 300,
                frontMeters = 10.0, depthMeters = 30.0,
                images = listOf(1, 2), isSaved = false,
                latitude = 19.1180, longitude = -104.3010
            ),
            Property(
                id = "6", title = "TERRENO COMERCIAL", price = 1200000.0,
                listingType = ListingType.VENTA,
                location = "Col. Valle de las Garzas, Manzanillo, Colima",
                type = PropertyType.TERRENO, areaSqM = 450,
                frontMeters = 15.0, depthMeters = 30.0,
                images = listOf(1, 2, 3), isSaved = false,
                latitude = 19.0830, longitude = -104.2980
            ),
            Property(
                id = "7", title = "DEPARTAMENTO NUEVO EN RENTA", price = 12000.0,
                listingType = ListingType.RENTA,
                location = "Col. Centro, Manzanillo, Colima",
                type = PropertyType.DEPARTAMENTO, areaSqM = 70,
                bedrooms = 2, bathrooms = 1, garages = 1,
                images = listOf(1, 2, 3), isSaved = false,
                latitude = 19.0531, longitude = -104.3188
            ),
            Property(
                id = "8", title = "CASA RESIDENCIAL EN VENTA", price = 1980000.0,
                listingType = ListingType.VENTA,
                location = "Col. Campos, Manzanillo, Colima",
                type = PropertyType.CASA, areaSqM = 140,
                bedrooms = 3, bathrooms = 2, garages = 2,
                images = listOf(1, 2, 3), isSaved = false,
                latitude = 19.1100, longitude = -104.3250
            )
        )
    )

    override fun observeAll(): Flow<List<Property>> = properties

    override fun observeSaved(): Flow<List<Property>> =
        properties.map { list -> list.filter { it.isSaved } }

    override suspend fun setSaved(propertyId: String, saved: Boolean) {
        properties.update { list ->
            list.map { if (it.id == propertyId) it.copy(isSaved = saved) else it }
        }
    }
}
