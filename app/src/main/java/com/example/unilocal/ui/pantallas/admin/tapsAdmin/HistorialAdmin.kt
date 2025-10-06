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
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(reg.lugarNombre, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        AssistChip(onClick = {}, label = { Text(reg.accion.name) })
                        Spacer(Modifier.height(6.dp))
                        Text(reg.fechaIso, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        if (reg.motivo.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(reg.motivo, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Divider()
            }
        }
    }
}


