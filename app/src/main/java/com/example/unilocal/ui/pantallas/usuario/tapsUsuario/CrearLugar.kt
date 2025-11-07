package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
 
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.unilocal.R
import com.example.unilocal.ui.componentes.CampoTexto
import com.example.unilocal.ui.componentes.Resultadooperacion
import com.example.unilocal.ui.theme.VerdePrincipal
import com.example.unilocal.utils.RequestResult
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.UsuarioViewModel
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.model.entidad.Ubicacion
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.Marker
import android.graphics.Color as AndroidColor
import com.example.unilocal.ui.map.createPlacePinDrawable
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.core.content.ContextCompat
import androidx.compose.foundation.horizontalScroll

@Composable
fun CrearLugar(
    navController: NavController? = null,
    lugaresViewModel: LugaresViewModel = viewModel(),
    usuarioViewModel: UsuarioViewModel? = null
) {
    // Asegura un UsuarioViewModel con alcance Activity si no se pasa uno
    val usuarioVM: UsuarioViewModel = usuarioViewModel ?: viewModel(LocalContext.current as androidx.activity.ComponentActivity)
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var direccion by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var imagenSeleccionada by remember { mutableStateOf<android.net.Uri?>(null) }

    // horarios
    var horarios by remember {
        mutableStateOf(
            mapOf(
                "Lunes" to "",
                "Martes" to "",
                "Miércoles" to "",
                "Jueves" to "",
                "Viernes" to "",
                "Sábado" to "",
                "Domingo" to ""
            )
        )
    }
    val usuarioActual = usuarioVM.usuarioActual.collectAsState().value
    val lugarResult by lugaresViewModel.lugarResult.collectAsState()
    val context = LocalContext.current

    val categorias = listOf(
        stringResource(R.string.categoria_restaurante),
        stringResource(R.string.categoria_cafe),
        stringResource(R.string.categoria_hotel),
        stringResource(R.string.categoria_museo),
        stringResource(R.string.categoria_comida_rapida),
        stringResource(R.string.categoria_pasada)
    )

    // convertir horarios
    fun convertirHorariosAPair(horariosTexto: Map<String, String>): Map<String, Pair<String, String>> {
        return horariosTexto.mapValues { (_, horarioTexto) ->
            if (horarioTexto.isBlank() || horarioTexto.lowercase().contains("cerrado")) {
                Pair("Cerrado", "Cerrado")
            } else {
                // buscar horarios
                val patron = Regex("""(.+?)\s*[-–—]\s*(.+)""")
                val match = patron.find(horarioTexto)

                if (match != null) {
                    Pair(match.groupValues[1].trim(), match.groupValues[2].trim())
                } else {
                    // si no encuentra patrón, usar todo como apertura
                    Pair(horarioTexto.trim(), horarioTexto.trim())
                }
            }
        }
    }

    // para seleccionar imagen
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        imagenSeleccionada = uri
    }

    // Ubicación seleccionada en el mapa (lat, lng) como MutableState para MapDialog
    // Se inicializa desde la ubicación del dispositivo si está disponible; si no, queda en 0.0
    val latSeleccionadaState = remember { mutableStateOf(0.0) }
    val lngSeleccionadaState = remember { mutableStateOf(0.0) }


    // Por defecto: Armenia, Colombia si no hay ubicación (valores inline usados bajo demanda)
    // referencia al MapView nativo
    val referenciaMapa = remember { mutableStateOf<MapView?>(null) }

    // Cliente de ubicación (FusedLocationProvider)
    val clienteUbicacion = remember {
        try {
            LocationServices.getFusedLocationProviderClient(context)
        } catch (e: Exception) {
            null
        }
    }

    // permiso de ubicación
    val contextoPermisos = LocalContext.current
    var permisoUbicacionConcedido by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                contextoPermisos,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val lanzadorPermisoUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido: Boolean ->
        permisoUbicacionConcedido = concedido
        if (concedido) {
                // si el mapa ya existe, intentar centrarlo
            val mv = referenciaMapa.value
            if (mv != null) {
                if (clienteUbicacion != null) {
                    clienteUbicacion.lastLocation.addOnSuccessListener { loc: Location? ->
                        if (loc != null) {
                            android.util.Log.d("CrearLugar", "fused lastLocation -> lat=${loc.latitude}, lng=${loc.longitude}, accuracy=${loc.accuracy}")
                            android.os.Handler(contextoPermisos.mainLooper).post {
                                mv.controller.setCenter(GeoPoint(loc.latitude, loc.longitude))
                                mv.controller.setZoom(16.5)
                            }
                        } else {
                            android.util.Log.d("CrearLugar", "fused lastLocation -> null")
                        }
                    }
                } else {
                    val lm = contextoPermisos.getSystemService(LocationManager::class.java)
                    val loc: Location? = try {
                        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    } catch (e: Exception) { null }
                    if (loc != null) {
                        android.os.Handler(contextoPermisos.mainLooper).post {
                            mv.controller.setCenter(GeoPoint(loc.latitude, loc.longitude))
                            mv.controller.setZoom(16.5)
                        }
                    }
                }
            }
        }
    }
    // Si hay permiso, pedir a UsuarioViewModel que obtenga y persista una ubicación del dispositivo (una vez)
    LaunchedEffect(permisoUbicacionConcedido) {
        if (permisoUbicacionConcedido) {
            try {
                usuarioVM.obtenerYGuardarUbicacionDispositivoSiFalta()
                } catch (e: Exception) {
                    // ignorar
                }
        }
    }
    // Intentar obtener la ubicación del dispositivo al componer, si el permiso está concedido.
    LaunchedEffect(permisoUbicacionConcedido, clienteUbicacion) {
        if (permisoUbicacionConcedido) {
            try {
                clienteUbicacion?.lastLocation?.addOnSuccessListener { loc: Location? ->
                    if (loc != null) {
                        // solo setear si aún no hay una selección previa en memoria
                        if (latSeleccionadaState.value == 0.0 && lngSeleccionadaState.value == 0.0) {
                            latSeleccionadaState.value = loc.latitude
                            lngSeleccionadaState.value = loc.longitude
                        }
                    }
                }
            } catch (e: Exception) {
                // ignorar
            }
        } else {
            // pedir permiso para que el diálogo pueda mostrar la ubicación si el usuario lo permite
            try {
                lanzadorPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } catch (e: Exception) {
                // ignore
            }
        }
    }
    var showMap by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
    ) {
        // header con gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(VerdePrincipal, VerdePrincipal.copy(alpha = 0.8f))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // icono principal
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Crear lugar",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.crea_nuevo_hogar),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Comparte tu lugar favorito con la comunidad",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // categoría
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "📂 Categoría",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.selecciona_categoria),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        categorias.forEach { categoria ->
                            val isSelected = categoriaSeleccionada == categoria
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .background(
                                        if (isSelected) VerdePrincipal else Color(0xFFF0F0F0),
                                        shape = RoundedCornerShape(25.dp)
                                    )
                                    .border(
                                        if (isSelected) 0.dp else 1.dp,
                                        if (isSelected) Color.Transparent else Color.LightGray,
                                        shape = RoundedCornerShape(25.dp)
                                    )
                                    .clickable { categoriaSeleccionada = categoria }
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = categoria,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // imagen
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "📷 Imagen del Lugar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Selecciona una imagen que represente tu lugar",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // preview de imagen o botón para seleccionar
                    if (imagenSeleccionada != null) {
                        // mostrar imagen seleccionada
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    Color(0xFFF5F5F5),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    2.dp,
                                    VerdePrincipal,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = "Imagen seleccionada",
                                    tint = VerdePrincipal,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Imagen seleccionada",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = VerdePrincipal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Toca para cambiar",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        // botón para seleccionar imagen
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    Color(0xFFF5F5F5),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    2.dp,
                                    Color.LightGray,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = "Seleccionar imagen",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Toca para seleccionar imagen",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "JPG, PNG o GIF",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // información básica
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "📝 Información Básica",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CampoTexto(
                        valor = nombre,
                        cuandoCambia = { nombre = it },
                        etiqueta = stringResource(R.string.nombre_lugar)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CampoTexto(
                        valor = descripcion,
                        cuandoCambia = { descripcion = it },
                        etiqueta = stringResource(R.string.descripcion_lugar)
                    )

                    Spacer(modifier = Modifier.height(12.dp))


                    Spacer(modifier = Modifier.height(12.dp))

                    CampoTexto(
                        valor = telefono,
                        cuandoCambia = { telefono = it },
                        etiqueta = stringResource(R.string.telefono_lugar)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // horarios
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "🕒 Horarios de Atención",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Ingresa el horario para cada día (ej: 8:00 AM - 6:00 PM o Cerrado)",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // lista de horarios por día
                    horarios.forEach { (dia, horario) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // nombre del día
                            Text(
                                text = dia,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.width(90.dp)
                            )

                            // campo de horario
                            OutlinedTextField(
                                value = horario,
                                onValueChange = { nuevoHorario ->
                                    horarios = horarios.toMutableMap().apply {
                                        put(dia, nuevoHorario)
                                    }
                                },
                                placeholder = { Text("8:00 AM - 6:00 PM", fontSize = 14.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VerdePrincipal,
                                    unfocusedBorderColor = Color.Gray
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // botón de conveniencia
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                horarios = horarios.mapValues { "8:00 AM - 6:00 PM" }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Llenar todos",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ubicación
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "📍 Ubicación",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CampoTexto(
                            valor = direccion,
                            cuandoCambia = { direccion = it },
                            etiqueta = stringResource(R.string.marca_direccion),
                            modificador = Modifier.weight(1f)
                        )

                        Button(
                            onClick = { /* abrir mapa */ },
                            colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Buscar en mapa",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // mapa placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                Color(0xFFE8F5E8),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                2.dp,
                                VerdePrincipal.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                showMap = true
                                if (!permisoUbicacionConcedido) {
                                    lanzadorPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Mapa",
                                tint = VerdePrincipal,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Mapa interactivo (toca para seleccionar)",
                                fontSize = 14.sp,
                                color = VerdePrincipal,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                                if (latSeleccionadaState.value != 0.0 || lngSeleccionadaState.value != 0.0) {
                                Text(text = "Lat: ${String.format("%.6f", latSeleccionadaState.value)}  Lng: ${String.format("%.6f", lngSeleccionadaState.value)}", fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))





            // botón de guardar
            Button(
                onClick = {
                    if (nombre.isBlank() || descripcion.isBlank() || telefono.isBlank() || direccion.isBlank() || categoriaSeleccionada.isBlank()) {
                        android.widget.Toast.makeText(
                            context,
                            "Completa todos los campos",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val nuevoLugar = Lugar(
                        id = System.currentTimeMillis().toString(),
                        nombre = nombre,
                        descripcion = descripcion,
                        direccion = direccion,
                        categoria = categoriaSeleccionada,
                        horario = convertirHorariosAPair(horarios),
                        telefono = telefono,
                        imagenUri = imagenSeleccionada?.toString() ?: "default_image",
                        likes = 0,
                        estado = EstadoLugar.PENDIENTE,
                        creadorId = usuarioActual?.id ?: "anon",
                        calificacionPromedio = 0.0,
                        ubicacion = Ubicacion(latitud = latSeleccionadaState.value, longitud = lngSeleccionadaState.value),
                        comentarios = emptyList()
                    )

                    lugaresViewModel.crearLugar(nuevoLugar, context)
                    // No navegar ni mostrar toast aquí, esperar al resultado
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Crear",
                        tint = Color.White
                    )
                    Text(
                        text = stringResource(R.string.boton_guardar),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mostrar resultado de la operación
            Resultadooperacion(
                result = lugarResult,
                onSucess = {
                    lugaresViewModel.resetear()
                    navController?.popBackStack()
                },
                onFailure = {
                    lugaresViewModel.resetear()
                }
            )
        }
    }

    // Mostrar el dialog del mapa cuando showMap sea true
    if (showMap) {
        MapDialog(
            showMap = showMap,
            onDismiss = {
                showMap = false
                // publicar la ubicación seleccionada en UsuarioViewModel para que otras pantallas (Recomendaciones) la usen
                val nueva = Ubicacion(latitud = latSeleccionadaState.value, longitud = lngSeleccionadaState.value)
                usuarioVM.setUbicacionSeleccionada(nueva)
                // persistir la selección para que otras pantallas o el próximo inicio puedan leerla
                try {
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putString("ubicacion_lat", nueva.latitud.toString())
                        .putString("ubicacion_lng", nueva.longitud.toString())
                        .apply()
                } catch (e: Exception) {
                    // ignorar errores de persistencia
                }
            },
            mapViewRef = referenciaMapa,
            latSeleccionadaState = latSeleccionadaState,
            lngSeleccionadaState = lngSeleccionadaState,
            permisoUbicacion = permisoUbicacionConcedido,
            clienteUbicacion = clienteUbicacion
        )
    }

    // Manejar navegación cuando se crea exitosamente

}

// Diálogo de mapa a pantalla completa: se muestra cuando showMap == true
@Composable
private fun MapDialog(
    showMap: Boolean,
    onDismiss: () -> Unit,
    mapViewRef: MutableState<MapView?>,
    latSeleccionadaState: MutableState<Double>,
    lngSeleccionadaState: MutableState<Double>,
    permisoUbicacion: Boolean,
    clienteUbicacion: FusedLocationProviderClient?
) {
    if (!showMap) return
    Dialog(onDismissRequest = onDismiss) {
    // Propietario del ciclo de vida para el MapView
        val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    // Overlay de pantalla completa; centrar un contenedor para que el mapa no cubra toda la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    // make the dialog wider (closer to screen edges)
                    .fillMaxWidth(0.96f)
                    // increase overall dialog height so the map can be taller than wide
                    .height(760.dp)
                    .padding(vertical = 12.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(2.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                // Encerrar el MapView en una caja recortada para evitar que se desborde
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        // hacer el mapa más alto (vertical) manteniéndolo contenido
                        .height(680.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(2.dp, Color.LightGray.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                ) {
                    AndroidView(
                        factory = { ctx ->
                        val appCtx = ctx.applicationContext
                        val prefs = appCtx.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE)
                        Configuration.getInstance().load(appCtx, prefs)

                        val map = MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(16.5)
                        }

                        mapViewRef.value = map

                        val marker = Marker(map).apply {
                            // pin azul para el usuario (mismo estilo que en Recomendaciones)
                            icon = createPlacePinDrawable(ctx, AndroidColor.parseColor("#2196F3"), 44)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        map.overlays.add(marker)

                        val receiver = object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                android.os.Handler(ctx.mainLooper).post {
                                    latSeleccionadaState.value = p.latitude
                                    lngSeleccionadaState.value = p.longitude
                                    marker.position = GeoPoint(p.latitude, p.longitude)
                                    map.invalidate()
                                }
                                return true
                            }

                            override fun longPressHelper(p: GeoPoint): Boolean = false
                        }
                        map.overlays.add(MapEventsOverlay(receiver))

                        // Lógica de centrado
                        if (latSeleccionadaState.value != 0.0 || lngSeleccionadaState.value != 0.0) {
                            val p = GeoPoint(latSeleccionadaState.value, lngSeleccionadaState.value)
                            marker.position = p
                            map.controller.setCenter(p)
                        } else if (permisoUbicacion) {
                            if (clienteUbicacion != null) {
                                clienteUbicacion.lastLocation.addOnSuccessListener { loc: Location? ->
                                    if (loc != null) {
                                        android.os.Handler(ctx.mainLooper).post {
                                            val p = GeoPoint(loc.latitude, loc.longitude)
                                            latSeleccionadaState.value = loc.latitude
                                            lngSeleccionadaState.value = loc.longitude
                                            marker.position = p
                                            map.controller.setCenter(p)
                                            map.invalidate()
                                        }
                                    }
                                }
                            } else {
                                val lm = ctx.getSystemService(LocationManager::class.java)
                                val loc: Location? = try {
                                    lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                        ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                } catch (e: Exception) { null }
                                if (loc != null) {
                                    android.os.Handler(ctx.mainLooper).post {
                                        val p = GeoPoint(loc.latitude, loc.longitude)
                                        latSeleccionadaState.value = loc.latitude
                                        lngSeleccionadaState.value = loc.longitude
                                        marker.position = p
                                        map.controller.setCenter(p)
                                        map.invalidate()
                                    }
                                }
                            }
                        } else {
                            android.os.Handler(ctx.mainLooper).post {
                                val p = GeoPoint(4.5338889, -75.6811111)
                                latSeleccionadaState.value = p.latitude
                                lngSeleccionadaState.value = p.longitude
                                marker.position = p
                                map.controller.setCenter(p)
                                map.invalidate()
                            }
                        }

                            map
                            },
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }

                // buttons row inside the boxed container, aligned to bottom
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        TextButton(onClick = onDismiss) { Text("Confirmar") }
                    }
                }
            }

            // Manejo del ciclo de vida del MapView dentro del diálogo
            DisposableEffect(lifecycleOwner, mapViewRef.value) {
                val map = mapViewRef.value
                val observer = LifecycleEventObserver { _, event ->
                    map?.let {
                        when (event) {
                            Lifecycle.Event.ON_START -> it.onResume()
                            Lifecycle.Event.ON_STOP -> it.onPause()
                            else -> {}
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    mapViewRef.value?.onPause()
                    mapViewRef.value = null
                }
            }
        }
    }
}


