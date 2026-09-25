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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
private val SectionGrayBg = Color(0xFFEFEFEF)
private val BorderColor = Color(0xFFE5E7EB)

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

            Spacer(modifier = Modifier.height(8.dp))

            // SECCIÓN DE PROPIEDADES
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SectionGrayBg)
            ) {
                Text(
                    text = "${formatNumber(uiState.properties.size)} inmuebles",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Lista vertical que abarca el ancho
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
                .size(42.dp)
                .clip(CircleShape)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
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
            .padding(horizontal = 16.dp, vertical = 4.dp),
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
                modifier = Modifier.size(16.dp)
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
                modifier = Modifier.size(16.dp)
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        val isTablet = maxWidth >= 550.dp

        if (isTablet) {
            // DISEÑO TABLET / PANTALLA ANCHA (Carrusel a la izquierda, detalles a la derecha)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Carrusel a la izquierda (Ocupa ~40% del ancho)
                PropertyImageCarousel(
                    property = property,
                    pagerState = pagerState,
                    coroutineScope = coroutineScope,
                    totalPages = totalPages,
                    modifier = Modifier
                        .weight(0.4f)
                        .aspectRatio(16f / 10f)
                )

                // Detalles a la derecha (Ocupa el resto)
                Column(
                    modifier = Modifier.weight(0.6f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PropertyInfoContent(
                        property = property,
                        onToggleSave = onToggleSave
                    )
                }
            }
        } else {
            // DISEÑO CELULAR (Arriba carrusel, abajo detalles)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                PropertyImageCarousel(
                    property = property,
                    pagerState = pagerState,
                    coroutineScope = coroutineScope,
                    totalPages = totalPages,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                PropertyInfoContent(
                    property = property,
                    onToggleSave = onToggleSave
                )
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD9D9D9))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (property.type == PropertyType.TERRENO) Color(0xFF6E7E65)
                        else Color(0xFF8D8374)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Imagen ${page + 1}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            }
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

        Icon(
            imageVector = Icons.Default.MoreHoriz,
            contentDescription = "Opciones",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(22.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/$totalPages",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun PropertyInfoContent(
    property: Property,
    onToggleSave: () -> Unit
) {
    Column {
        // Precio y Guardar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val formattedPrice = formatCurrency(property.price)
            val priceText = if (property.listingType == ListingType.RENTA) {
                "MX $formattedPrice / mes"
            } else {
                "MX $formattedPrice"
            }

            Text(
                text = priceText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            IconButton(
                onClick = onToggleSave,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_guardar_relleno_abajo),
                    contentDescription = "Guardar inmueble",
                    tint = if (property.isSaved) NavyColor else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Ubicación
        Text(
            text = property.location,
            fontSize = 12.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 2.dp)
        )

        // Tipo de Propiedad
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            val typeText = when (property.type) {
                PropertyType.DEPARTAMENTO -> "Departamento"
                PropertyType.CASA -> "Casa"
                PropertyType.TERRENO -> "Terreno"
            }

            val iconRes = when (property.type) {
                PropertyType.DEPARTAMENTO -> R.drawable.ic_departamento
                PropertyType.TERRENO -> R.drawable.ic_terreno
                PropertyType.CASA -> R.drawable.ic_casa_abajo
            }

            Text(
                text = typeText,
                fontSize = 11.sp,
                color = Color.Gray
            )
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Características e Íconos
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        ) {
            FeatureItem(
                iconRes = R.drawable.ic_regla_medicion,
                text = "${property.areaSqM}m²"
            )

            if (property.type != PropertyType.TERRENO) {
                property.bedrooms?.let {
                    FeatureItem(
                        iconRes = R.drawable.ic_habitaciones,
                        text = "$it recámaras"
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
                        text = "$it cochera${if (it > 1) "s" else ""}"
                    )
                }
            } else {
                property.frontMeters?.let {
                    FeatureItem(
                        iconRes = R.drawable.ic_ancho_alto_terreno,
                        text = "${it.toInt()}m",
                        iconRotation = 90f
                    )
                }
                property.depthMeters?.let {
                    FeatureItem(
                        iconRes = R.drawable.ic_ancho_alto_terreno,
                        text = "${it.toInt()}m"
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    iconRes: Int,
    text: String,
    iconRotation: Float = 0f
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .size(16.dp)
                .rotate(iconRotation)
        )
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color.Black
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
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navIcons = listOf(
                R.drawable.ic_casa_abajo,
                R.drawable.ic_guardar_relleno_abajo,
                R.drawable.ic_campanita_relleno_abajo,
                R.drawable.ic_user_relleno_abajo
            )

            navIcons.forEachIndexed { index, resId ->
                val isSelected = selectedTab == index

                IconButton(onClick = { onTabSelected(index) }) {
                    Icon(
                        painter = painterResource(id = resId),
                        contentDescription = null,
                        tint = if (isSelected) NavyColor else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
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