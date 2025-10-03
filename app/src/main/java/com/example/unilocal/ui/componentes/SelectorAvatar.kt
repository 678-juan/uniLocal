package com.example.unilocal.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unilocal.R

@Composable
fun SelectorAvatar(
    avatarSeleccionado: Int,
    onAvatarSeleccionado: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val avatares = listOf(
        R.drawable.hombre,
        R.drawable.mujer,
        R.drawable.hombre1,
        R.drawable.mujer1,
        R.drawable.hombre2,
        R.drawable.mujer2
    )

    Column(modifier = modifier) {
        Text(
            text = "Selecciona tu avatar",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(avatares.size) { index ->
                val avatarId = avatares[index]
                val estaSeleccionado = avatarSeleccionado == index
                
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (estaSeleccionado) 3.dp else 1.dp,
                            color = if (estaSeleccionado) Color(0xFF1976D2) else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable { onAvatarSeleccionado(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = avatarId),
                        contentDescription = "Avatar ${index + 1}",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
