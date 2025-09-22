package com.example.unilocal.ui.componentes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults



import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CampoTexto(
    valor: String,
    cuandoCambia: (String) -> Unit,
    etiqueta: String,
    modificador: Modifier = Modifier,
    transformacion: VisualTransformation = VisualTransformation.None,
    opcionesTeclado: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modificador.fillMaxWidth()) {
        // Etiqueta fija (no flotante ni animada)
        Text(
            text = etiqueta,
            fontSize = 14.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        BasicTextField(
            value = valor,
            onValueChange = cuandoCambia,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                .padding(12.dp),
            singleLine = true,
            visualTransformation = transformacion,
            keyboardOptions = opcionesTeclado,
            textStyle = TextStyle(color = Color.Black)
        )
    }
}

