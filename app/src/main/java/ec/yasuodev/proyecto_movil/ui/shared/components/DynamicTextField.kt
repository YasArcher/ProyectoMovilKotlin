package ec.yasuodev.proyecto_movil.ui.shared.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import ec.yasuodev.proyecto_movil.R

@Composable
fun DynamicField(
    value: String,
    onTextFieldChange: (String) -> Unit,
    tipo: Int = -1,
    passwordVisible: Boolean? = false,
    onVisibilityChange: (() -> Unit)? = null
) {
    val type = when (tipo) {
        0 -> if (passwordVisible == true) KeyboardType.Text else KeyboardType.Password
        1 -> KeyboardType.Email
        5 -> KeyboardType.Number
        else -> KeyboardType.Text
    }

    val placeholder = when (tipo) {
        0 -> "Contraseña"
        1 -> "Email"
        2 -> "Nombre"
        3 -> "Apellido"
        4 -> "Nombre de Usuario"
        else -> ""
    }

    val leadingIconImage = when (tipo) {
        0 -> painterResource(id = R.drawable.key_24px)
        1 -> painterResource(id = R.drawable.mail_24px)
        else -> null
    }

    val trailingIconImage = if (tipo == 0) {
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
        visualTransformation = if (tipo == 0 && !passwordVisible!!) PasswordVisualTransformation() else VisualTransformation.None,
        leadingIcon = leadingIconImage?.let {
            { Icon(painter = it, contentDescription = null) }
        },
        trailingIcon = trailingIconImage?.let {
            {
                Icon(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.clickable { onVisibilityChange?.invoke() })
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}