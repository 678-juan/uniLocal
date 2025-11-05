package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.material3.Text
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.util.BoundingBox
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.content.Context
import com.example.unilocal.ui.map.createPlacePinDrawable
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import android.location.Location
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import com.example.unilocal.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unilocal.ui.componentes.FichaLugar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
// No lectura en vivo de UsuarioViewModel: Recomendaciones lee la ubicación persistida solo al iniciar
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.ui.componentes.FichaInformacion
import com.example.unilocal.ui.componentes.PublicacionUno
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.UsuarioViewModel


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

    // Al iniciar, preferir la ubicación del dispositivo. Capturar una vez para mantener el mapa estable.
    var ubicacionUsuario by remember { mutableStateOf<GeoPoint?>(null) }
    val usuarioVM: UsuarioViewModel = usuarioViewModel ?: androidx.lifecycle.viewmodel.compose.viewModel(LocalContext.current as androidx.activity.ComponentActivity)
    LaunchedEffect(permisoUbicacionConcedido) {
        if (permisoUbicacionConcedido) {
                // pedir a UsuarioViewModel que obtenga y persista una ubicación del dispositivo si falta
            try {
                usuarioVM.obtenerYGuardarUbicacionDispositivoSiFalta()
            } catch (e: Exception) {

            }

            // Leer la ubicacionSeleccionada desde el ViewModel
            val fromVm = usuarioVM.ubicacionSeleccionada.value
            if (fromVm != null) {
                ubicacionUsuario = GeoPoint(fromVm.latitud, fromVm.longitud)
                android.util.Log.d("Recomendaciones", "using location from UsuarioViewModel -> lat=${fromVm.latitud}, lng=${fromVm.longitud}")
            } else {
                // si el ViewModel aún no tiene una, intentar obtenerla directamente del dispositivo (getCurrentLocation)
                try {
                    clienteUbicacion.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { loc: Location? ->
                            if (loc != null) {
                                android.util.Log.d("Recomendaciones", "getCurrentLocation direct -> lat=${loc.latitude}, lng=${loc.longitude}, accuracy=${loc.accuracy}")
                                android.os.Handler(context.mainLooper).post { ubicacionUsuario = GeoPoint(loc.latitude, loc.longitude) }
                            } else {
                                clienteUbicacion.lastLocation.addOnSuccessListener { lastLoc: Location? ->
                                    if (lastLoc != null) {
                                        android.util.Log.d("Recomendaciones", "fused lastLocation direct -> lat=${lastLoc.latitude}, lng=${lastLoc.longitude}, accuracy=${lastLoc.accuracy}")
                                        android.os.Handler(context.mainLooper).post { ubicacionUsuario = GeoPoint(lastLoc.latitude, lastLoc.longitude) }
                                    } else {
                                        android.util.Log.d("Recomendaciones", "no device location available; ubicacionUsuario stays null")
                                        android.os.Handler(context.mainLooper).post { ubicacionUsuario = null }
                                    }
                                }
                            }
                        }
                } catch (t: Throwable) {
                    android.util.Log.e("Recomendaciones", "Error al obtener ubicación directa del dispositivo", t)
                    ubicacionUsuario = null
                }
            }
        } else {
            ubicacionUsuario = null
        }
    }

    // ayudita para calcular distancia (metros) entre dos GeoPoint
    fun distanciaEnMetros(p1: GeoPoint, p2: GeoPoint): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
        return results[0]
    }

    // Usar createPlacePinDrawable de MapPinUtils para los pines
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

                                // calcular lugares cercanos para marcadores: si hay ubicación del usuario, filtrar radio 2km
                                val nearbyPlacesOnMap = if (ubicacionUsuario != null) {
                                    lugaresAutorizados.filter { lugar ->
                                        val p = GeoPoint(lugar.ubicacion.latitud, lugar.ubicacion.longitud)
                                        distanciaEnMetros(ubicacionUsuario!!, p) <= 2000f
                                    }
                                } else {
                                    emptyList()
                                }

                                // agregar marcadores rojos para lugares cercanos
                                nearbyPlacesOnMap.forEach { lugar ->
                                    val m = Marker(map).apply {
                                        position = GeoPoint(lugar.ubicacion.latitud, lugar.ubicacion.longitud)
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

                                // decidir ubicación efectiva: la del usuario si existe, de lo contrario EAM (Armenia) por defecto
                                val defaultEam = GeoPoint(4.5338889, -75.6811111)
                                val effectiveLocation = ubicacionUsuario ?: defaultEam

                                // agregar un marcador azul en la ubicación efectiva (si es por defecto, indicarlo)
                                val effectiveMarker = Marker(map).apply {
                                    position = effectiveLocation
                                    title = if (ubicacionUsuario != null) "Tu posición" else "EAM (ubicación por defecto)"
                                    icon = createPlacePinDrawable(map.context, android.graphics.Color.BLUE)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                                map.overlays.add(effectiveMarker)

                                // calcular lugares cercanos alrededor de la ubicación efectiva (2 km)
                                val nearbyPlaces = lugaresAutorizados.filter { lugar ->
                                    val p = GeoPoint(lugar.ubicacion.latitud, lugar.ubicacion.longitud)
                                    distanciaEnMetros(effectiveLocation, p) <= 2000f
                                }

                                if (nearbyPlaces.isEmpty()) {
                                    // no hay lugares cercanos: si es ubicación real hacer zoom cercano, si no mostrar zoom de ciudad
                                    if (ubicacionUsuario != null) {
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
        // compute and display nearby places (2km) below the map as FichaLugar
        val defaultEamForList = GeoPoint(4.5338889, -75.6811111)
        val effectiveLocationForList = ubicacionUsuario ?: defaultEamForList
        val nearbyPlacesForList = lugaresAutorizados.filter { lugar ->
            val p = GeoPoint(lugar.ubicacion.latitud, lugar.ubicacion.longitud)
            distanciaEnMetros(effectiveLocationForList, p) <= 2000f
        }

        if (nearbyPlacesForList.isEmpty()) {
            item {
                Text(
                    text = if (ubicacionUsuario == null) "Usando ubicación por defecto (EAM, Armenia). No hay recomendaciones dentro de 2 km" else "No hay recomendaciones dentro de 2 km",
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(nearbyPlacesForList) { lugar ->
                // show a compact card for each nearby place
                FichaLugar(lugar = lugar, onClick = { navegarALugar(lugar.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}









