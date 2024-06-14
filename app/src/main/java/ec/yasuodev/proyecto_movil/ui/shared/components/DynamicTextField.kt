package ec.yasuodev.proyecto_movil.ui.shared.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import ec.yasuodev.proyecto_movil.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicField(
    value: String,
    onTextFieldChange: (String) -> Unit,
    isPassword: Number = 0,
    passwordVisible: Boolean? = false,
    onVisibilityChange: (() -> Unit)? = null
) {
    // Actualizar el tipo de teclado si es una contraseña
    val type = if (isPassword == 0 ) {
        if (passwordVisible == true) KeyboardType.Text else KeyboardType.Password
    } else {
        KeyboardType.Text
    }
    val placeholder = if (isPassword == 0) {
        "Contraseña"
    } else if (isPassword == 1) {
        "Email"
    } else if(isPassword == 2){
        "Nombre de Usuario"
    } else {
        ""
    }

    val image = if (isPassword==0) {
        if (passwordVisible == true) {
            painterResource(id = R.drawable.visibility_24px)
        } else {
            painterResource(id = R.drawable.visibility_off_24px)
        }
    } else {
        null
    }

    TextField(
        value = value,
        onValueChange = { onTextFieldChange(it) },
        placeholder = { Text(text = placeholder) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = type),
        singleLine = true,
        maxLines = 1,
        visualTransformation = if (type == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            image?.let {
                Icon(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.clickable { onVisibilityChange?.invoke() })
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Blue,
            unfocusedIndicatorColor = Color.Gray,
        )
    )
}