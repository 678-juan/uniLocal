package com.example.unilocal.ui.componentes

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CampoTexto(
    valor: String,
    cuandoCambia: (String) -> Unit,
    etiqueta: String,
    modificador: Modifier = Modifier,
    transformacion: VisualTransformation = VisualTransformation.None,
    opcionesTeclado: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = valor,
        onValueChange = cuandoCambia,
        label = { Text(etiqueta) },
        modifier = modificador,
        visualTransformation = transformacion,
        keyboardOptions = opcionesTeclado
    )
}