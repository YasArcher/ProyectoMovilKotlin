package ec.yasuodev.proyecto_movil.ui.shared.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import ec.yasuodev.proyecto_movil.R
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
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
        6 -> KeyboardType.Number
        else -> KeyboardType.Text
    }

    val placeholder = when (tipo) {
        0 -> "Contraseña"
        1 -> "Email"
        2 -> "Nombre"
        3 -> "Apellido"
        4 -> "Nombre de Usuario"
        5 -> "Precio"
        6 -> "Stock"
        else -> ""
    }

    val leadingIconImage = when (tipo) {
        0 -> painterResource(id = R.drawable.baseline_key_24)
        1 -> painterResource(id = R.drawable.baseline_email_24)
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

    OutlinedTextField(
        value = value,
        onValueChange = { onTextFieldChange(it) },
        placeholder = { Text(text = placeholder, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9B86BE)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = type),
        singleLine = true,
        visualTransformation = if (tipo == 0 && passwordVisible == false) PasswordVisualTransformation() else VisualTransformation.None,
        leadingIcon = leadingIconImage?.let {
            {
                Icon(
                    painter = it,
                    contentDescription = null,
                    tint = Color(0xFF72BF85)
                )
            }
        },
        trailingIcon = trailingIconImage?.let {
            {
                Icon(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.clickable { onVisibilityChange?.invoke() },
                    tint = Color(0xFF72BF85)
                )
            }
        },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(15.dp), // Ajuste para bordes más redondeados
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.Transparent,
            focusedBorderColor = Color(0xFF9B86BE),  // Color del borde cuando está enfocado
            unfocusedBorderColor = Color(0xFF443D8B) ,  // Color del borde cuando no está enfocado
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DynamicFieldPreview() {
    MaterialTheme {
        val textState = remember { mutableStateOf("") }
        val passwordVisibleState = remember { mutableStateOf(false) }

        DynamicField(
            value = textState.value,
            onTextFieldChange = { textState.value = it },
            tipo = 1, // Cambia a 0 para probar como campo de contraseña
            passwordVisible = passwordVisibleState.value,
            onVisibilityChange = { passwordVisibleState.value = !passwordVisibleState.value }
        )
    }
}
