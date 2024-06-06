package com.david.easycutter.view.elements

import android.health.connect.datatypes.units.Length
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Composable para mostrar un input de contraseña con la opción de mostrar la contraseña.
 *
 * @param passwordState el estado mutable que contiene la contraseña.
 * @param labelId el identificador de la etiqueta del campo de entrada.
 * @param passwordVisible el estado mutable que indica si la contraseña debe mostrarse o no.
 */
@Composable
fun PasswordInput(
    passwordState: MutableState<String>,
    labelId: String,
    passwordVisible: MutableState<Boolean>,
) {
    // Determina la transformación visual según la visibilidad de la contraseña
    val visualTransformation = if (passwordVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        value = passwordState.value,
        onValueChange = {passwordState.value = it},
        label = { Text(text = labelId) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = visualTransformation,
        // Icono de visibilidad de contraseña
        trailingIcon = { PasswordVisibility(passwordVisible, passwordState) },
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth()
    )
}

/**
 * Composable para mostrar un icono de visibilidad de contraseña y cambiar el estado de visibilidad de la contraseña.
 *
 * @param passwordVisible el estado mutable que indica si la contraseña debe mostrarse o no.
 * @param passwordState el estado mutable que contiene la contraseña.
 */
@Composable
fun PasswordVisibility(
    passwordVisible: MutableState<Boolean>,
    passwordState: MutableState<String>
){
    // Verifica si la contraseña no está en blanco para mostrar el icono de visibilidad
    if (passwordState.value.isNotBlank()){
        // Determina qué icono usar según la visibilidad actual de la contraseña
        val image: ImageVector = if (passwordVisible.value){
            Icons.Default.VisibilityOff
        }else{
            Icons.Default.Visibility
        }

        // Botón para cambiar la visibilidad de la contraseña
        IconButton(onClick = {
            passwordVisible.value = !passwordVisible.value
        }) {
            Icon(
                imageVector = image,
                contentDescription = "Imagen visibilidad botón"
            )
        }
    }
}

/**
 * Composable para mostrar un input de texto normal.
 *
 * @param variableState el estado mutable que contiene el valor del campo de entrada.
 * @param labelId el identificador de la etiqueta del campo de entrada.
 * @param keyBoardType el tipo de teclado para el campo de entrada.
 * @param singleLine indica si el campo de entrada debe ser de una sola línea o no (predeterminado es verdadero).
 * @param modifier el modificador que se aplica al composable (predeterminado es un modificador de tamaño máximo de relleno).
 */
@Composable
fun NormalInput(
    variableState: MutableState<String>,
    labelId: String,
    keyBoardType: KeyboardType,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
        .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
        .fillMaxWidth()
){
    OutlinedTextField(
        value = variableState.value,
        onValueChange = {variableState.value = it},
        label = { Text(text = labelId) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyBoardType
        ),
        modifier = modifier,
    )
}