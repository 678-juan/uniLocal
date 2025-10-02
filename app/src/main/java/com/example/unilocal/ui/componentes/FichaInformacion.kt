package com.example.unilocal.ui.componentes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unilocal.model.entidad.Lugar

import androidx.compose.ui.unit.sp


@Composable
fun FichaInformacion(
    lugar: Lugar,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {


            Text(
                text = lugar.nombre,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )


            Text(
                text = lugar.categoria,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF555555)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))


            Text(
                text = "Dirección",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = lugar.direccion,
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
            Text(
                text = "A 1.2 km aprox.",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))


            val hoy = "Lunes"
            Text(
                text = "Horario",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            lugar.horario.forEach { (dia, horas) ->
                val (inicio, fin) = horas
                Text(
                    text = "$dia: $inicio - $fin",
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // --- Teléfono y Estado ---
            Text(
                text = "Teléfono: ${lugar.telefono}",
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
            Text(
                text = "Estado: ${lugar.estado.name}",
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
        }
    }
}