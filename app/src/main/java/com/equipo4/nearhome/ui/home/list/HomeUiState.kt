package com.equipo4.nearhome.ui.home.list

import com.equipo4.nearhome.domain.model.Property

data class HomeUiState(
    val searchQuery: String = "",
    val properties: List<Property> = emptyList(),
    val selectedTab: Int = 0
)