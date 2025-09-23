package com.example.unilocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.unilocal.ui.theme.TemaApp
import com.example.unilocal.ui.pantallas.Navegacion
import com.example.unilocal.ui.pantallas.usuario.PantallaEditarUsuario

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaApp {
                Navegacion()
            }
        }
    }
}

