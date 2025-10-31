package com.example.unilocal.ui.componentes

import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unilocal.utils.RequestResult
import com.google.android.gms.tasks.OnFailureListener
import kotlinx.coroutines.delay

@Composable
fun Resultadooperacion(
    result: RequestResult?,
    onSucess: suspend() -> Unit,
    onFailure: suspend() -> Unit,

) {


    LaunchedEffect( result) {
        when (result) {
            is RequestResult.Sucess -> {
                delay(2000)
                onSucess()

            }
            is RequestResult.Error -> {
                delay(2000)
                onFailure()
            }
            else -> {}
        }
    }
}