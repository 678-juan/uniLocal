package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.unilocal.ui.componentes.CampoMinimalista
import com.example.unilocal.ui.componentes.FichaLugar
import com.example.unilocal.ui.pantallas.usuario.navegacionUsuario.RutaTab
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.model.entidad.EstadoLugar

@Composable
fun Busqueda(
    navController: NavController? = null,
    lugaresViewModel: LugaresViewModel? = null
) {
    var textoBusqueda by remember { mutableStateOf("") }
    val lugaresVM: LugaresViewModel = lugaresViewModel ?: viewModel()
    val lugares by lugaresVM.lugares.collectAsState()
    
    // filtrar lugares
    val lugaresFiltrados = remember(textoBusqueda, lugares) {
        val lugaresAutorizados = lugares.filter { it.estado == EstadoLugar.AUTORIZADO }.take(15)
        
        if (textoBusqueda.isBlank()) {
            lugaresAutorizados
        } else {
            // Búsqueda optimizada - prioriza nombre
            lugaresAutorizados.filter { lugar ->
                lugar.nombre.contains(textoBusqueda, ignoreCase = true)
            }.ifEmpty {
                // Si no encuentra en nombre, busca en otros campos
                lugaresAutorizados.filter { lugar ->
                    lugar.categoria.contains(textoBusqueda, ignoreCase = true) ||
                    lugar.descripcion.contains(textoBusqueda, ignoreCase = true)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CampoMinimalista(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            placeholder = "Busca por categoría, nombre o descripción"
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (textoBusqueda.isBlank()) {
            Text("Recomendaciones mágicas", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            Text(
                "Resultados para: '$textoBusqueda' (${lugaresFiltrados.size})", 
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(lugaresFiltrados) { lugar ->
                FichaLugar(
                    lugar = lugar,
                    onClick = {
                        navController?.navigate(RutaTab.LugarDetalles(lugar.id))
                    },
                    lugaresViewModel = lugaresVM
                )
            }
        }
    }
}

