package com.example.unilocal.ui.pantallas.admin.tapsAdmin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unilocal.viewModel.ModeracionViewModel

@Composable
fun HistorialAdmin(
    moderacionViewModel: ModeracionViewModel = viewModel()
) {
    val historial = moderacionViewModel.historial.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = "Historial", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        if (historial.isEmpty()) {
            Text("Sin registros todavía")
        } else {
            historial.forEach { reg ->
                ListItem(
                    headlineContent = { Text(reg.lugarNombre) },
                    supportingContent = {
                        Text("${reg.accion} • ${reg.fechaIso}")
                    }
                )
                Divider()
            }
        }
    }
}


