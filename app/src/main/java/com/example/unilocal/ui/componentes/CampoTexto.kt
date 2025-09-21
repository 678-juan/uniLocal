package com.example.unilocal.ui.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults



import androidx.compose.ui.graphics.Color



@Composable
fun CampoTexto(
    valor: String,
    cuandoCambia: (String) -> Unit,
    etiqueta: String,
    modificador: Modifier = Modifier,
    transformacion: VisualTransformation = VisualTransformation.None,
    opcionesTeclado: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = valor,
        onValueChange = cuandoCambia,
        label = { Text(etiqueta) },
        modifier = modificador.fillMaxWidth(),
        visualTransformation = transformacion,
        keyboardOptions = opcionesTeclado,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black,
            disabledIndicatorColor = Color.Gray,
            errorIndicatorColor = Color.Red,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        )
    )
}
