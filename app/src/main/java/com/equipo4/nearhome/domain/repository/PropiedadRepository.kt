package com.equipo4.nearhome.domain.repository 

/**
 * Contrato de acceso a propiedades. Hoy lo implementa PropiedadRepositoryFake (datos en memoria);
 * cuando exista el backend solo hay que crear otra implementación y cambiar Repositories.propiedades.
 */
interface PropiedadRepository {
    /** Todas las propiedades (Lista y Mapa). Emite de nuevo cada vez que algo cambia, ej. al guardar. */
    fun observeAll(): Flow<List<Property>>

    /** Propiedades marcadas como guardadas. Emite de nuevo cada vez que cambian. */
    fun observeSaved(): Flow<List<Property>>

    suspend fun setSaved(propertyId: String, saved: Boolean)
}