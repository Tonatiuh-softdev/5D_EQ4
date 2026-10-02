package com.equipo4.nearhome.ui.home.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo4.nearhome.R
import com.equipo4.nearhome.domain.model.ListingType
import com.equipo4.nearhome.domain.model.Property
import com.equipo4.nearhome.domain.model.PropertyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private val NavyColor = Color(0xFF0B2C4D)
private val LightGrayBg = Color(0xFFF3F4F6)
private val SectionGrayBg = Color(0xFFF8F9FA)
private val BorderColor = Color(0xFFE5E7EB)
private val BadgeBlueBg = Color(0xFFEBF5FF)
private val BadgeBlueText = Color(0xFF1E88E5)
private val TextDark = Color(0xFF1F2937)
private val TextGray = Color(0xFF6B7280)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onPropertyClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::onTabSelected
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Encabezado superior
            HomeHeader(
                searchQuery = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )

            // Botones de filtro y ordenamiento
            FilterAndSortRow()

            Spacer(modifier = Modifier.height(4.dp))

            // SECCIÓN DE PROPIEDADES
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SectionGrayBg)
            ) {
                Text(
                    text = "${formatNumber(uiState.properties.size)} inmuebles",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.properties,
                        key = { it.id }
                    ) { property ->
                        PropertyCard(
                            property = property,
                            onToggleSave = { viewModel.toggleSaveProperty(property.id) },
                            onClick = { onPropertyClick(property.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_nearhouse_logo),
            contentDescription = "Logo NearHome",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .clip(RoundedCornerShape(23.dp))
                .background(LightGrayBg)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Busca por ubicación",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_localizacion),
                contentDescription = "Pin Ubicación",
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun FilterAndSortRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = { },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BorderColor),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filtrar),
                contentDescription = "Filtrar",
                tint = Color.Black,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Filtrar", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }

        OutlinedButton(
            onClick = { },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BorderColor),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_ordenar),
                contentDescription = "Ordenar",
                tint = Color.Black,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Ordenar", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PropertyCard(
    property: Property,
    onToggleSave: () -> Unit,
    onClick: () -> Unit
) {
    val totalPages = property.images.ifEmpty { listOf(1) }.size
    val pagerState = rememberPagerState(pageCount = { totalPages })
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isTablet = maxWidth >= 550.dp

            if (isTablet) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PropertyImageCarousel(
                        property = property,
                        pagerState = pagerState,
                        coroutineScope = coroutineScope,
                        totalPages = totalPages,
                        onToggleSave = onToggleSave,
                        modifier = Modifier
                            .weight(0.4f)
                            .aspectRatio(16f / 10f)
                    )

                    Column(modifier = Modifier.weight(0.6f)) {
                        PropertyInfoContent(property = property)
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PropertyImageCarousel(
                        property = property,
                        pagerState = pagerState,
                        coroutineScope = coroutineScope,
                        totalPages = totalPages,
                        onToggleSave = onToggleSave,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    )

                    PropertyInfoContent(property = property)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PropertyImageCarousel(
    property: Property,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    totalPages: Int,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE5E7EB))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (property.type == PropertyType.TERRENO) Color(0xFF53624E)
                        else Color(0xFF7A7265)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Imagen ${page + 1}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 15.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${pagerState.currentPage + 1} / $totalPages",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onToggleSave() },
            contentAlignment = Alignment.Center
        ) {
            val bookmarkIcon = if (property.isSaved) {
                R.drawable.ic_guardar_relleno_abajo
            } else {
                R.drawable.ic_guardar_abajo
            }
            Icon(
                painter = painterResource(id = bookmarkIcon),
                contentDescription = "Guardar propiedad",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        if (pagerState.currentPage > 0) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 4.dp)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Anterior",
                    tint = Color.White
                )
            }
        }

        if (pagerState.currentPage < totalPages - 1) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Siguiente",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun PropertyInfoContent(
    property: Property
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {
        // Fila 1: Precio + Tag RENTA / VENTA
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val formattedPrice = formatCurrency(property.price)

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "MX \$$formattedPrice",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                if (property.listingType == ListingType.RENTA) {
                    Text(
                        text = " / mes",
                        fontSize = 14.sp,
                        color = TextGray,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Surface(
                color = BadgeBlueBg,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = property.listingType.name,
                    color = BadgeBlueText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Fila 2: Título
        Text(
            text = property.title.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Fila 3: PIN DE LOCALIZACIÓN
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pin_localizacion),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = property.location,
                fontSize = 12.sp,
                color = TextGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Fila 4: Características dinámicas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (typeText, typeIcon) = when (property.type) {
                PropertyType.DEPARTAMENTO -> "Dept." to R.drawable.ic_departamento
                PropertyType.CASA -> "Casa" to R.drawable.ic_casa_relleno_abajo
                PropertyType.TERRENO -> "Terreno" to R.drawable.ic_terreno
            }

            FeatureItem(iconRes = typeIcon, text = typeText)

            if (property.type == PropertyType.TERRENO) {
                if (property.frontMeters != null && property.depthMeters != null) {
                    FeatureItem(
                        iconRes = R.drawable.ic_regla_medicion,
                        text = "${property.frontMeters.toInt()}x${property.depthMeters.toInt()} m"
                    )
                }
            } else {
                property.bedrooms?.let {
                    FeatureItem(
                        iconRes = R.drawable.ic_habitaciones,
                        text = "$it recamaras"
                    )
                }
                property.bathrooms?.let {
                    FeatureItem(
                        iconRes = R.drawable.ic_bathrooms,
                        text = "$it baños"
                    )
                }
                property.garages?.let {
                    FeatureItem(
                        iconRes = R.drawable.ic_cocheras,
                        text = "$it cocheras"
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    iconRes: Int,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = TextGray,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextGray
        )
    }
}

@Composable
private fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pares de íconos: (Icono Desactivado/Línea, Icono Activado/Relleno)
                val navIcons = listOf(
                    R.drawable.ic_casa_abajo to R.drawable.ic_casa_relleno_abajo,
                    R.drawable.ic_guardar_abajo to R.drawable.ic_guardar_relleno_abajo,
                    R.drawable.ic_campanita_abajo to R.drawable.ic_campanita_relleno_abajo,
                    R.drawable.ic_user_abajo to R.drawable.ic_user_relleno_abajo
                )

                navIcons.forEachIndexed { index, (unselectedIcon, selectedIcon) ->
                    val isSelected = selectedTab == index
                    val currentIcon = if (isSelected) selectedIcon else unselectedIcon

                    IconButton(onClick = { onTabSelected(index) }) {
                        Icon(
                            painter = painterResource(id = currentIcon),
                            contentDescription = null,
                            tint = if (isSelected) NavyColor else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return formatter.format(amount.toLong())
}

private fun formatNumber(number: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return formatter.format(number)
}