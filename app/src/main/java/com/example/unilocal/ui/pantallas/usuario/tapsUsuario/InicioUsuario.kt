package com.example.unilocal.ui.pantallas.usuario.tapsUsuario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unilocal.ui.componentes.PublicacionUno
import com.example.unilocal.viewModel.LugaresViewModel
import com.example.unilocal.viewModel.UsuarioViewModel

@Composable
fun Inicio(
    lugaresViewModel: LugaresViewModel? = null,
    usuarioViewModel: UsuarioViewModel? = null,
    navegarALugar: (String) -> Unit = {}
){
    val lugaresVM: LugaresViewModel = lugaresViewModel ?: viewModel()
    val lugares by lugaresVM.lugares.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Todos los lugares",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        items(lugares) { lugar ->
            PublicacionUno(
                lugar = lugar,
                onClick = { 
                    println("DEBUG: Click en PublicacionUno - ${lugar.nombre}")
                    navegarALugar(lugar.id) 
                },
                usuarioViewModel = usuarioViewModel
            )
        }
    }
}