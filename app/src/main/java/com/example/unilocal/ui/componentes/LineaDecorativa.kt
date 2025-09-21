package com.example.unilocal.ui.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun LineaDecorativa(lineHeightDp: Dp) {
    Canvas(
        modifier = Modifier
            .height(lineHeightDp + 20.dp)
            .width(60.dp)
    ) {
        val lineHeight = lineHeightDp.toPx() // convertir Dp → Px

        // linea
        drawLine(
            color = Color(0xFF008080),
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, lineHeight),
            strokeWidth = 9f
        )

        // circulito al final de la línea
        drawCircle(
            color = Color(0xFF008080),
            radius = 17f,
            center = Offset(size.width / 2, lineHeight)
        )
    }
}
