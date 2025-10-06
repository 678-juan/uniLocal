package com.example.unilocal.ui.pantallas.usuario.tapsUsuario
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.example.unilocal.model.entidad.Lugar
import com.example.unilocal.ui.componentes.FichaInformacion
import com.example.unilocal.ui.componentes.PublicacionUno
import com.example.unilocal.ui.componentes.ComentarioCard
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.UsuarioViewModel

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import com.example.unilocal.model.entidad.Comentario
import java.util.*


@Composable
fun LugarDetalles(
    idLugar: String,
    navController: NavController? = null,
    usuarioViewModel: UsuarioViewModel? = null,
    lugaresViewModel: LugaresViewModel? = null
) {
    val lugarViewModel = lugaresViewModel ?: throw IllegalStateException("LugaresViewModel es requerido")
    val lugares by lugarViewModel.lugares.collectAsState()
    val lugar: Lugar? = lugares.find { it.id == idLugar }

    var nuevoComentario by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 🔹 Encabezado
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Atrás",
                    modifier = Modifier.clickable {
                        navController?.popBackStack()
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Más información del lugar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 🔹 Contenido del lugar
        lugar?.let { lugarActual ->
            item {
                PublicacionUno(
                    lugar = lugarActual,
                    onClick = { },
                    usuarioViewModel = usuarioViewModel
                )
            }

            item { FichaInformacion(lugar = lugarActual) }

            // 🔹 Sección de comentarios
            item {
                Text(
                    text = "Comentarios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Campo para agregar comentario
            item {
                OutlinedTextField(
                    value = nuevoComentario,
                    onValueChange = { nuevoComentario = it },
                    label = { Text("Escribe tu comentario...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Botón agregar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (nuevoComentario.isNotBlank()) {
                                val comentario = Comentario(
                                    id = UUID.randomUUID().toString(),
                                    usuarioId = usuarioViewModel?.usuarioActual?.value?.id ?: "usuario_anonimo",
                                    lugarId = lugarActual.id,
                                    texto = nuevoComentario.trim(),
                                    estrellas = 5,
                                    fecha = System.currentTimeMillis()
                                )
                                lugarViewModel.agregarComentario(lugarActual.id, comentario)
                                nuevoComentario = ""
                            }
                        }
                    ) {
                        Text("Agregar")
                    }
                }
            }

            // Lista de comentarios
            if (lugarActual.comentarios.isEmpty()) {
                item {
                    Text(
                        text = "Aún no hay comentarios. Sé el primero en comentar.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(
                    lugarActual.comentarios.sortedByDescending { it.fecha }
                ) { comentario ->
                    usuarioViewModel?.let { usuarioVM ->
                        ComentarioCard(
                            comentario = comentario,
                            usuarioViewModel = usuarioVM
                        )
                    }
                }
            }
        }
    }
}