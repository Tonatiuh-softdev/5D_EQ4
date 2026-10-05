package com.equipo4.nearhome.data.repository

import com.equipo4.nearhome.domain.repository.PropiedadRepository

/**
 * Punto único para elegir la implementación. Al tener backend, cambiar SOLO esta línea
 * (ej. PropiedadRepositoryRemote()); ni la pantalla ni el ViewModel se modifican.
 */
object Repositories {
    val propiedades: PropiedadRepository by lazy { PropiedadRepositoryFake() }
}
