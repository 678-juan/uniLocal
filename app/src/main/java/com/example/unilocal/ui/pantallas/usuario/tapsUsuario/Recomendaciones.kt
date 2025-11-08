package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
// R not required in this file
import com.example.unilocal.ui.componentes.FichaLugar
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.ui.map.createPlacePinDrawable
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.UsuarioViewModel
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.cos


@Composable
fun Recomendaciones(
    lugaresViewModel: LugaresViewModel,
    navegarALugar: (String) -> Unit,
    usuarioViewModel: UsuarioViewModel? = null
) {
    val lugares by lugaresViewModel.lugares.collectAsState()

    // solo lugares autorizados
    val lugaresAutorizados = lugares.filter { it.estado == EstadoLugar.AUTORIZADO }.take(10)

    val context = LocalContext.current
    // Preferir la última ubicación conocida del dispositivo como fuente inicial (igual que en CrearLugar)
    val clienteUbicacion = remember { LocationServices.getFusedLocationProviderClient(context) }
    var permisoUbicacionConcedido by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    ) }
    val lanzadorPermisoUbicacion = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { concedido ->
        permisoUbicacionConcedido = concedido
    }

    // pedir permiso una vez si no está concedido
    LaunchedEffect(permisoUbicacionConcedido) {
        if (!permisoUbicacionConcedido) {
            lanzadorPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Recolectar reactiva y continuamente la ubicación seleccionada desde el ViewModel.
    // Esto asegura que si el usuario selecciona/actualiza su ubicación en otra pantalla,
    // Recomendaciones se actualiza automáticamente.
    val usuarioVM: UsuarioViewModel = usuarioViewModel ?: androidx.lifecycle.viewmodel.compose.viewModel(LocalContext.current as androidx.activity.ComponentActivity)
    val ubicacionSeleccionadaVm by usuarioVM.ubicacionSeleccionada.collectAsState()

    // Helper que obtiene la ubicación del dispositivo una sola vez y devuelve el resultado
    // vía callback. Recomendaciones la usará como ubicación inicial sin sobrescribir el
    // valor del ViewModel (esto evita que la selección manual en CrearLugar sea reemplazada).
    fun obtenerUbicacionUnaVez(context: Context, onResult: (com.example.unilocal.model.entidad.Ubicacion?) -> Unit) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        try {
            client.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc: Location? ->
                    if (loc != null) {
                        onResult(com.example.unilocal.model.entidad.Ubicacion(loc.latitude, loc.longitude))
                    } else {
                        onResult(null)
                    }
                }
                .addOnFailureListener {
                    onResult(null)
                }
        } catch (t: Throwable) {
            onResult(null)
        }
    }

    // Estado local para almacenar la ubicación obtenida una sola vez durante esta composición
    var ubicacionUnaVez by remember { mutableStateOf<com.example.unilocal.model.entidad.Ubicacion?>(null) }

    // Si el permiso de ubicación está concedido, siempre obtenemos la ubicación del dispositivo
    // al entrar en esta composición y la usamos como la ubicación efectiva. Esto garantiza que
    // Recomendaciones siempre muestre la ubicación real del dispositivo aunque el usuario
    // haya cambiado manualmente la selección en CrearLugar.
    LaunchedEffect(permisoUbicacionConcedido) {
        if (permisoUbicacionConcedido) {
            obtenerUbicacionUnaVez(context) { u ->
                if (u != null) {
                    ubicacionUnaVez = u
                    android.util.Log.d("Recomendaciones", "Device location fetched -> lat=${u.latitud}, lng=${u.longitud}")
                } else {
                    try {
                        usuarioVM.obtenerYGuardarUbicacionDispositivoSiFalta()
                    } catch (_: Exception) { /* ignore */ }
                }
            }
        }
    }

    // ayudita para calcular distancia (metros) entre dos GeoPoint
    fun distanciaEnMetros(p1: GeoPoint, p2: GeoPoint): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
        return results[0]
    }

    // Usar createPlacePinDrawable de MapPinUtils para los pines

    // definir ubicación efectiva y lugares cercanos (250 m) una vez para que mapa y lista usen lo mismo
    val defaultEam = GeoPoint(4.5338889, -75.6811111)
    // Priorizar siempre la ubicación del dispositivo (ubicacionUnaVez). Ignorar la selección
    // del ViewModel para que los cambios en CrearLugar no afecten a Recomendaciones.
    val effectiveLocation = ubicacionUnaVez?.let { GeoPoint(it.latitud, it.longitud) } ?: defaultEam
    val nearbyPlaces = lugaresAutorizados.filter { lugar ->
        val p = GeoPoint(lugar.ubicacion.latitud, lugar.ubicacion.longitud)
        distanciaEnMetros(effectiveLocation, p) <= 250f
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Tu ubicacion", fontWeight = FontWeight.Bold)

            // Vista previa del mapa en caja recortada con borde visible
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp) // taller view for map + markers
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color.LightGray.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val appCtx = ctx.applicationContext
                        val prefs = appCtx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
                        Configuration.getInstance().load(appCtx, prefs)

                        val map = MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            // desactivar escalado de tiles por DPI para reducir re-escalados costosos
                            setTilesScaledToDpi(false)
                            // allow fling and smooth gestures
                            setFlingEnabled(true)
                            setUseDataConnection(true)
                            controller.setZoom(12.0)
                            controller.setCenter(GeoPoint(4.5338889, -75.6811111))
                            isClickable = true
                            // evitar que el padre (LazyColumn) intercepte eventos táctiles del mapa
                            setOnTouchListener { v, event ->
                                v.parent?.requestDisallowInterceptTouchEvent(true)
                                false
                            }
                        }
                        // disable built-in zoom controls overlay (can affect layout)
                        map.setBuiltInZoomControls(false)
                        map
                    }
                    , update = { map ->
                        // Ejecutar actualizaciones UI en el hilo del MapView para evitar problemas de hilos
                        map.post {
                            try {
                                        map.overlays.clear()

                                                    // agregar marcadores rojos para lugares cercanos (usar same nearbyPlaces que la lista, 250 m)
                                        nearbyPlaces.forEach { lugar ->
                                            // calcular posición original y posible desplazamiento si está encima del usuario
                                            val originalPos = GeoPoint(lugar.ubicacion.latitud, lugar.ubicacion.longitud)
                                            val distanceToUser = distanciaEnMetros(effectiveLocation, originalPos)
                                            // si la distancia es pequeña (ahora 3 m), desplazar para que no queden solapados
                                            val displayPos = if (distanceToUser < 3.0f) {
                                                // desplazar ~2 metros para separar visualmente los pines
                                                val metersOffset = 2.0
                                                val deltaLat = metersOffset / 111000.0
                                                val deltaLon = metersOffset / (111000.0 * cos(Math.toRadians(effectiveLocation.latitude)))
                                                GeoPoint(originalPos.latitude + deltaLat, originalPos.longitude + deltaLon)
                                            } else {
                                                originalPos
                                            }

                                            val m = Marker(map).apply {
                                                position = displayPos
                                                title = lugar.nombre ?: "Lugar"
                                                icon = createPlacePinDrawable(map.context, android.graphics.Color.RED)
                                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                                setOnMarkerClickListener { _, _ ->
                                                    navegarALugar(lugar.id)
                                                    true
                                                }
                                            }
                                            map.overlays.add(m)
                                        }

                                // decidir ubicación efectiva: (ya calculada arriba -> effectiveLocation)

                                // agregar un marcador azul en la ubicación efectiva (si es por defecto, indicarlo)
                                val effectiveMarker = Marker(map).apply {
                                    position = effectiveLocation
                                    title = if (ubicacionSeleccionadaVm != null) "Tu posición" else "EAM (ubicación por defecto)"
                                    icon = createPlacePinDrawable(map.context, android.graphics.Color.BLUE)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                                map.overlays.add(effectiveMarker)

                                if (nearbyPlaces.isEmpty()) {
                                    // no hay lugares cercanos: si es ubicación real hacer zoom cercano, si no mostrar zoom de ciudad
                                    if (ubicacionSeleccionadaVm != null) {
                                        map.controller.setCenter(effectiveLocation)
                                        map.controller.setZoom(16.5)
                                    } else {
                                        map.controller.setCenter(defaultEam)
                                        map.controller.setZoom(12.0)
                                    }
                                } else {
                                    // ajustar vista para incluir usuario/por defecto + lugares cercanos
                                    val points = nearbyPlaces.map { GeoPoint(it.ubicacion.latitud, it.ubicacion.longitud) } + effectiveLocation
                                    val minLat = points.minOf { it.latitude }
                                    val maxLat = points.maxOf { it.latitude }
                                    val minLon = points.minOf { it.longitude }
                                    val maxLon = points.maxOf { it.longitude }
                                    val bbox = BoundingBox(maxLat, maxLon, minLat, minLon)
                                    map.zoomToBoundingBox(bbox, true, 40)
                                }

                            } catch (ex: Throwable) {
                                // Registrar en log para ayudar depuración
                                android.util.Log.e("Recomendaciones", "Error actualizando overlays del mapa", ex)
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Recomendaciones cerca a ti", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
        }
    // mostrar la misma lista de lugares que los marcadores del mapa (nearbyPlaces calculado arriba, 250 m)
        if (nearbyPlaces.isEmpty()) {
            item {
                Text(
                    text = if (ubicacionSeleccionadaVm == null) "Usando ubicación por defecto (EAM, Armenia). No hay recomendaciones dentro de 250 m" else "No hay recomendaciones dentro de 250 m",
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(nearbyPlaces) { lugar ->
                // show a compact card for each nearby place
                FichaLugar(lugar = lugar, onClick = { navegarALugar(lugar.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}










