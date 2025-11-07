package com.example.unilocal.ui.componentes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.unilocal.utils.RequestResult
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

