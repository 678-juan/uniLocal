package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.unilocal.R
import com.example.unilocal.ui.componentes.BotonPrincipal
import com.example.unilocal.ui.componentes.CampoTexto
import com.example.unilocal.ui.theme.VerdePrincipal
import com.example.unilocal.viewModel.LugaresViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.unilocal.model.entidad.EstadoLugar
import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.model.entidad.Ubicacion
import com.example.unilocal.viewModel.UsuarioViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CrearLugar(
    navController: NavController? = null,
    lugaresViewModel: LugaresViewModel = viewModel(),
    usuarioViewModel: UsuarioViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("") }
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
    val usuarioActual = usuarioViewModel.usuarioActual.collectAsState().value
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
                    
                    CampoTexto(
                        valor = horario,
                        cuandoCambia = { horario = it },
                        etiqueta = stringResource(R.string.horario_atencion)
                    )
                    
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
                            ),
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
                                text = "Mapa interactivo",
                                fontSize = 14.sp,
                                color = VerdePrincipal,
                                fontWeight = FontWeight.Medium
                            )
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
                        longitud = 0.0,
                        estado = EstadoLugar.PENDIENTE,
                        creadorId = usuarioActual?.id ?: "anon",
                        calificacionPromedio = 0.0,
                        ubicacion = Ubicacion(latitud = 0.0, longitud = 0.0),
                        comentarios = emptyList()
                    )

                    lugaresViewModel.crearLugar(nuevoLugar)
                    android.widget.Toast.makeText(
                        context,
                        "Lugar enviado para moderación",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    navController?.popBackStack()
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
        }
    }
}