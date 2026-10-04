package com.equipo4.nearhome.ui.home.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo4.nearhome.R
import com.equipo4.nearhome.domain.model.ListingType
import com.equipo4.nearhome.domain.model.Property
import com.equipo4.nearhome.domain.model.PropertyType
import com.equipo4.nearhome.ui.common.NearHomeBottomBar
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.hypot
import kotlin.math.roundToInt

private val NavyColor = Color(0xFF0B2C4D)
private val LightGrayBg = Color(0xFFF3F4F6)
private val BorderColor = Color(0xFFE5E7EB)
private val BadgeBlueBg = Color(0xFFEBF5FF)
private val BadgeBlueText = Color(0xFF1E88E5)
private val TextDark = Color(0xFF1F2937)
private val TextGray = Color(0xFF6B7280)

/**
 * Fuente única de color por tipo de propiedad. Los indicadores del filtro usan estos colores,
 * que fueron muestreados del cuerpo de cada pin (ic_pin_*.png) para que coincidan con el mapa.
 */
object PinColors {
    val Todo = Color(0xFF000000)
    val Casa = Color(0xFFE30B0C)
    val Terreno = Color(0xFF94A945)
    val Departamento = Color(0xFF3136BD)

    fun of(type: PropertyType) = when (type) {
        PropertyType.CASA -> Casa
        PropertyType.TERRENO -> Terreno
        PropertyType.DEPARTAMENTO -> Departamento
    }
}

private fun pinRes(type: PropertyType) = when (type) {
    PropertyType.CASA -> R.drawable.ic_pin_casa
    PropertyType.TERRENO -> R.drawable.ic_pin_terreno
    PropertyType.DEPARTAMENTO -> R.drawable.ic_pin_departamento
}

private fun typeLabel(type: PropertyType) = when (type) {
    PropertyType.CASA -> "Casas"
    PropertyType.TERRENO -> "Terrenos"
    PropertyType.DEPARTAMENTO -> "Departamentos"
}

// ---- Mapa ficticio -------------------------------------------------------------------------
// Mundo de 800x1000 "dp". Las coordenadas lat/lng de las propiedades se proyectan a este plano.
private const val WORLD_W = 800f
private const val WORLD_H = 1000f
private const val LON_MIN = -104.36
private const val LON_MAX = -104.28
private const val LAT_MIN = 19.04
private const val LAT_MAX = 19.13

private fun Property.worldPos(): Offset {
    val x = ((longitude!! - LON_MIN) / (LON_MAX - LON_MIN) * WORLD_W).toFloat()
    val y = ((LAT_MAX - latitude!!) / (LAT_MAX - LAT_MIN) * WORLD_H).toFloat()
    return Offset(x, y)
}

private class MapCluster(val items: MutableList<Property>, var sx: Float, var sy: Float)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    onToggleToList: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onPropertyClick: (String) -> Unit = {},
    onBottomTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val visibleProperties = uiState.visibleProperties

    Scaffold(
        bottomBar = { NearHomeBottomBar(selectedTab = 0, onTabSelected = onBottomTabSelected) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            MapHeader(
                searchQuery = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onToggleToList = onToggleToList
            )
            FilterRow(
                uiState = uiState,
                onFilterClick = onFilterClick,
                onTypeToggled = viewModel::onTypeToggled,
                onSortSelected = viewModel::onSortSelected
            )
            Spacer(Modifier.height(6.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                FakeMap(
                    properties = visibleProperties,
                    onPropertyClick = viewModel::onMarkerSelected
                )

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = NavyColor) }
                }

                uiState.errorMessage?.let { message ->
                    Surface(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 6.dp
                    ) {
                        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(message, color = TextDark, fontSize = 14.sp, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = viewModel::loadProperties,
                                colors = ButtonDefaults.buttonColors(containerColor = NavyColor)
                            ) { Text("Reintentar") }
                        }
                    }
                }
            }
        }
    }

    uiState.selectedProperty?.let { property ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = viewModel::onSheetDismissed,
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            PropertyPreviewSheet(
                property = property,
                onClose = viewModel::onSheetDismissed,
                onToggleSave = { viewModel.toggleSaveProperty(property.id) },
                onClick = {
                    viewModel.onSheetDismissed()
                    onPropertyClick(property.id)
                }
            )
        }
    }
}

/** Mapa dibujado con Canvas (sin Google Maps): arrastrar, pellizcar para zoom y clustering. */
@Composable
private fun FakeMap(
    properties: List<Property>,
    onPropertyClick: (String) -> Unit
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(0.dp))) {
        val viewW = with(density) { maxWidth.toPx() }
        val viewH = with(density) { maxHeight.toPx() }
        val d = density.density

        // Zoom mínimo = que el mundo llene la pantalla; el inicial es un poco más cercano.
        val minScale = maxOf(viewW / (WORLD_W * d), viewH / (WORLD_H * d))
        val maxScale = minScale * 4f
        var mapScale by remember(viewW, viewH) { mutableFloatStateOf(minScale * 1.3f) }
        var offset by remember(viewW, viewH) {
            mutableStateOf(Offset(-(WORLD_W * d * minScale * 1.3f - viewW) * 0.45f, -(WORLD_H * d * minScale * 1.3f - viewH) * 0.55f))
        }

        fun clampOffset(o: Offset, s: Float): Offset {
            val minX = viewW - WORLD_W * d * s
            val minY = viewH - WORLD_H * d * s
            return Offset(o.x.coerceIn(minX, 0f), o.y.coerceIn(minY, 0f))
        }

        fun zoomAround(focus: Offset, factor: Float) {
            val newScale = (mapScale * factor).coerceIn(minScale, maxScale)
            val ratio = newScale / mapScale
            offset = clampOffset((offset - focus) * ratio + focus, newScale)
            mapScale = newScale
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(viewW, viewH) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val newScale = (mapScale * zoom).coerceIn(minScale, maxScale)
                        val ratio = newScale / mapScale
                        offset = clampOffset((offset - centroid) * ratio + centroid + pan, newScale)
                        mapScale = newScale
                    }
                }
        ) {
            drawRect(Color(0xFFEDE9E1))
            withTransform({
                translate(offset.x, offset.y)
                scale(mapScale * d, mapScale * d, pivot = Offset.Zero)
            }) {
                drawFakeCity(textMeasurer)
            }
        }

        // Clustering por distancia en pantalla.
        val clusters = remember(properties, mapScale, offset, viewW) {
            val thresholdPx = 46f * d
            val list = mutableListOf<MapCluster>()
            properties.forEach { p ->
                val w = p.worldPos()
                val sx = w.x * d * mapScale + offset.x
                val sy = w.y * d * mapScale + offset.y
                val near = list.firstOrNull { hypot(it.sx - sx, it.sy - sy) < thresholdPx }
                if (near == null) list.add(MapCluster(mutableListOf(p), sx, sy))
                else {
                    near.items.add(p)
                    near.sx = (near.sx * (near.items.size - 1) + sx) / near.items.size
                    near.sy = (near.sy * (near.items.size - 1) + sy) / near.items.size
                }
            }
            list
        }

        val pinW = with(density) { 36.dp.roundToPx() }
        val pinH = with(density) { 50.dp.roundToPx() }
        val bubble = with(density) { 44.dp.roundToPx() }

        clusters.forEach { cluster ->
            if (cluster.items.size == 1) {
                val p = cluster.items.first()
                Image(
                    painter = painterResource(pinRes(p.type)),
                    contentDescription = p.title,
                    modifier = Modifier
                        .offset { IntOffset((cluster.sx - pinW / 2f).roundToInt(), (cluster.sy - pinH).roundToInt()) }
                        .size(36.dp, 50.dp)
                        .noRippleClickable { onPropertyClick(p.id) }
                )
            } else {
                Box(
                    modifier = Modifier
                        .offset { IntOffset((cluster.sx - bubble / 2f).roundToInt(), (cluster.sy - bubble / 2f).roundToInt()) }
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NavyColor)
                        .border(3.dp, Color.White, CircleShape)
                        .noRippleClickable { zoomAround(Offset(cluster.sx, cluster.sy), 1.9f) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("${cluster.items.size}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        // Controles de zoom (útiles en emulador sin multitouch).
        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ZoomButton(Icons.Default.Add, "Acercar") { zoomAround(Offset(viewW / 2, viewH / 2), 1.5f) }
            ZoomButton(Icons.Default.Remove, "Alejar") { zoomAround(Offset(viewW / 2, viewH / 2), 1f / 1.5f) }
        }
    }
}

@Composable
private fun ZoomButton(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier.size(40.dp).noRippleClickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = description, tint = TextDark, modifier = Modifier.size(22.dp))
        }
    }
}

/** Dibuja la ciudad ficticia en coordenadas de mundo (800x1000). */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFakeCity(
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val water = Color(0xFFA9D3F0)
    val park = Color(0xFFC8E6B0)
    val roadCasing = Color(0xFFD5D0C6)
    val roadFill = Color.White
    val block = Color(0xFFE4DFD5)

    // Manzanas urbanas (cuadrícula suave)
    for (gx in 0 until 8) for (gy in 0 until 10) {
        drawRoundRect(
            color = block,
            topLeft = Offset(gx * 100f + 14f, gy * 100f + 14f),
            size = Size(72f, 72f),
            cornerRadius = CornerRadius(6f)
        )
    }

    // Parques y laguna
    drawRoundRect(park, Offset(520f, 330f), Size(150f, 110f), CornerRadius(18f))
    drawRoundRect(park, Offset(180f, 90f), Size(120f, 90f), CornerRadius(18f))
    drawRoundRect(park, Offset(610f, 760f), Size(130f, 130f), CornerRadius(18f))
    drawOval(water, Offset(540f, 90f), Size(200f, 90f))

    // Mar (esquina inferior izquierda)
    val sea = Path().apply {
        moveTo(0f, 560f)
        cubicTo(180f, 600f, 300f, 700f, 330f, 820f)
        cubicTo(350f, 900f, 380f, 960f, 420f, 1000f)
        lineTo(0f, 1000f)
        close()
    }
    drawPath(sea, water)
    drawPath(sea, Color(0xFF8FC2E6), style = Stroke(width = 5f))

    // Calles: contorno gris + relleno blanco
    fun road(width: Float, pts: List<Offset>) {
        val path = Path().apply {
            moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y)
        }
        drawPath(path, roadCasing, style = Stroke(width + 5f, cap = StrokeCap.Round))
        drawPath(path, roadFill, style = Stroke(width, cap = StrokeCap.Round))
    }
    // Avenida principal costera
    road(20f, listOf(Offset(0f, 500f), Offset(220f, 540f), Offset(430f, 640f), Offset(560f, 800f), Offset(640f, 1000f)))
    // Avenidas
    road(14f, listOf(Offset(0f, 300f), Offset(800f, 300f)))
    road(14f, listOf(Offset(0f, 700f), Offset(300f, 700f), Offset(800f, 700f)))
    road(14f, listOf(Offset(400f, 0f), Offset(400f, 640f)))
    road(14f, listOf(Offset(700f, 0f), Offset(700f, 1000f)))
    road(14f, listOf(Offset(200f, 0f), Offset(200f, 520f)))
    road(14f, listOf(Offset(0f, 100f), Offset(800f, 100f)))
    road(14f, listOf(Offset(0f, 900f), Offset(330f, 900f), Offset(800f, 900f)))
    // Calles secundarias
    road(9f, listOf(Offset(0f, 200f), Offset(800f, 200f)))
    road(9f, listOf(Offset(0f, 400f), Offset(800f, 400f)))
    road(9f, listOf(Offset(0f, 600f), Offset(260f, 600f), Offset(800f, 600f)))
    road(9f, listOf(Offset(0f, 800f), Offset(800f, 800f)))
    road(9f, listOf(Offset(100f, 0f), Offset(100f, 520f)))
    road(9f, listOf(Offset(300f, 0f), Offset(300f, 680f)))
    road(9f, listOf(Offset(500f, 0f), Offset(500f, 1000f)))
    road(9f, listOf(Offset(600f, 0f), Offset(600f, 1000f)))
    road(9f, listOf(Offset(760f, 0f), Offset(760f, 1000f)))
    // Etiquetas
    fun label(text: String, at: Offset, color: Color = Color(0xFF6B7280), size: Float = 16f) {
        val layout = textMeasurer.measure(text, TextStyle(fontSize = size.sp, color = color, fontWeight = FontWeight.Medium))
        drawText(layout, topLeft = at)
    }
    label("Océano Pacífico", Offset(40f, 800f), Color(0xFF4E8FBF), 20f)
    label("Av. Elías Zamora Verduzco", Offset(60f, 470f), size = 14f)
    label("MANZANILLO", Offset(420f, 330f), Color(0xFF374151), 22f)
    label("Laguna", Offset(610f, 115f), Color(0xFF4E8FBF), 14f)
    label("Parque", Offset(555f, 370f), Color(0xFF5B8C3A), 14f)
}

@Composable
private fun MapHeader(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onToggleToList: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_nearhouse_logo),
            contentDescription = "Logo NearHome",
            modifier = Modifier.size(40.dp).clip(CircleShape)
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
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (searchQuery.isEmpty()) {
                    Text("Busca por ubicación", fontSize = 13.sp, color = Color.Gray, maxLines = 1)
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 13.sp, color = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.width(8.dp))
            // En Mapa el ícono muestra "lista" para regresar a la vista de Lista.
            Icon(
                imageVector = Icons.Default.FormatListBulleted,
                contentDescription = "Ver lista",
                tint = Color.Black,
                modifier = Modifier.size(20.dp).noRippleClickable { onToggleToList() }
            )
        }
    }
}

@Composable
private fun FilterRow(
    uiState: MapUiState,
    onFilterClick: () -> Unit,
    onTypeToggled: (PropertyType?) -> Unit,
    onSortSelected: (SortOption) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Filtrar: no despliega nada, redirige a su propia pantalla (pendiente de conectar en NearHouseApp)
        OutlinedButton(
            onClick = onFilterClick,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BorderColor),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(painterResource(R.drawable.ic_filtrar), contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text("Filtrar", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        // Ordenar
        DropdownChip(label = "Ordenar", iconRes = R.drawable.ic_ordenar) { dismiss ->
            SortOption.entries.filter { it != SortOption.NONE }.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option.label,
                            fontSize = 13.sp,
                            fontWeight = if (uiState.sort == option) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = { onSortSelected(option); dismiss() }
                )
            }
        }

        // Tipo de propiedad: "Todo" por defecto, multi-selección con indicador de color
        val typeLabelText = when (uiState.selectedTypes.size) {
            0 -> "Todo"
            1 -> typeLabel(uiState.selectedTypes.first())
            else -> "${uiState.selectedTypes.size} tipos"
        }
        TypeFilterChip(
            label = typeLabelText,
            selectedTypes = uiState.selectedTypes,
            onTypeToggled = onTypeToggled
        )
    }
}

@Composable
private fun DropdownChip(
    label: String,
    iconRes: Int?,
    menuContent: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BorderColor),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            if (iconRes != null) {
                Icon(painterResource(iconRes), contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
            }
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White
        ) { menuContent { expanded = false } }
    }
}

/**
 * Chip "Todo" con panel como el mockup: el panel se abre sobre el chip, la primera fila es "Todo"
 * con chevron, y cada opción lleva un checkbox cuadrado con borde del color del tipo
 * (relleno cuando está seleccionado).
 */
@Composable
private fun TypeFilterChip(
    label: String,
    selectedTypes: Set<PropertyType>,
    onTypeToggled: (PropertyType?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var chipHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(modifier = Modifier.onSizeChanged { chipHeight = with(density) { it.height.toDp() } }) {
        // Ancho FIJO: si el chip cambiara de ancho con la etiqueta ("Todo", "Casas", "2 tipos"...),
        // el panel se reposicionaría cada vez que se cambia el filtro.
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(120.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BorderColor),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(0.dp, -chipHeight),
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.width(210.dp)
        ) {
            // Fila "Todo" (con chevron, como el chip abierto del mockup)
            TypeCheckRow(
                label = "Todo",
                color = PinColors.Todo,
                checked = selectedTypes.isEmpty(),
                trailing = {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Cerrar",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(22.dp).noRippleClickable { expanded = false }
                    )
                }
            ) { onTypeToggled(null) }

            listOf(PropertyType.CASA, PropertyType.TERRENO, PropertyType.DEPARTAMENTO).forEach { type ->
                TypeCheckRow(
                    label = typeLabel(type),
                    color = PinColors.of(type),
                    checked = type in selectedTypes
                ) { onTypeToggled(type) }
            }
        }
    }
}

@Composable
private fun TypeCheckRow(
    label: String,
    color: Color,
    checked: Boolean,
    trailing: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (checked) color else Color.White)
                .border(2.dp, color, RoundedCornerShape(6.dp))
        )
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 16.sp, color = TextDark, modifier = Modifier.weight(1f))
        trailing?.invoke()
    }
}

@Composable
private fun PropertyPreviewSheet(
    property: Property,
    onClose: () -> Unit,
    onToggleSave: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() }
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            .navigationBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Text(
                "Detalle de la propiedad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.align(Alignment.Center)
            )
            Icon(
                Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = TextDark,
                modifier = Modifier.align(Alignment.CenterEnd).size(24.dp).noRippleClickable { onClose() }
            )
        }

        val totalPages = property.images.ifEmpty { listOf(1) }.size
        val pagerState = rememberPagerState(pageCount = { totalPages })
        Box(modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(12.dp))) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        if (property.type == PropertyType.TERRENO) Color(0xFF53624E) else Color(0xFF7A7265)
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(
                                when (property.type) {
                                    PropertyType.CASA -> R.drawable.ic_casa_relleno_abajo
                                    PropertyType.DEPARTAMENTO -> R.drawable.ic_departamento
                                    PropertyType.TERRENO -> R.drawable.ic_terreno
                                }
                            ),
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text("Imagen ${page + 1}", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart).padding(12.dp)
                    .clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("${pagerState.currentPage + 1}/$totalPages", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd).padding(12.dp).size(36.dp)
                    .clip(CircleShape).background(Color.Black.copy(alpha = 0.4f))
                    .noRippleClickable { onToggleSave() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(if (property.isSaved) R.drawable.ic_guardar_relleno_abajo else R.drawable.ic_guardar_abajo),
                    contentDescription = "Guardar propiedad",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp).scale(if (property.isSaved) 1.45f else 1.0f)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("MX \$${formatCurrency(property.price)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                if (property.listingType == ListingType.RENTA) {
                    Text(" / mes", fontSize = 14.sp, color = TextGray, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
            Surface(color = BadgeBlueBg, shape = RoundedCornerShape(6.dp)) {
                Text(
                    property.listingType.name,
                    color = BadgeBlueText, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(property.title.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_pin_localizacion), contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(property.location, fontSize = 12.sp, color = TextGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val (typeText, typeIcon) = when (property.type) {
                PropertyType.DEPARTAMENTO -> "Dept." to R.drawable.ic_departamento
                PropertyType.CASA -> "Casa" to R.drawable.ic_casa_relleno_abajo
                PropertyType.TERRENO -> "Terreno" to R.drawable.ic_terreno
            }
            FeatureItem(typeIcon, typeText)
            if (property.type == PropertyType.TERRENO) {
                if (property.frontMeters != null && property.depthMeters != null) {
                    FeatureItem(R.drawable.ic_regla_medicion, "${property.frontMeters.toInt()}x${property.depthMeters.toInt()} m")
                }
            } else {
                property.bedrooms?.let { FeatureItem(R.drawable.ic_habitaciones, "$it recámaras") }
                property.bathrooms?.let { FeatureItem(R.drawable.ic_bathrooms, "$it baños") }
                property.garages?.let { FeatureItem(R.drawable.ic_cocheras, "$it cocheras") }
            }
        }
    }
}

@Composable
private fun FeatureItem(iconRes: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(painterResource(iconRes), contentDescription = null, tint = TextGray, modifier = Modifier.size(15.dp))
        Text(text, fontSize = 12.sp, color = TextGray)
    }
}

private fun formatCurrency(amount: Double): String =
    NumberFormat.getNumberInstance(Locale.US).format(amount.toLong())

/** clickable sin ripple: evita el rectángulo gris oscuro al presionar pines, el sheet, etc. */
@Composable
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )