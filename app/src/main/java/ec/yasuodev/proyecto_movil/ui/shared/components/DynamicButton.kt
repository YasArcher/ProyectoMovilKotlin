package ec.yasuodev.proyecto_movil.ui.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DynamicButton(type: Int, text: String, enable: Boolean, method: () -> Unit) {
    Button(
        onClick = { method() },
        modifier = Modifier
            .fillMaxWidth()
            .height(43.dp),
        colors = colors(type = type),
        enabled = enable
    ) {
        Text(text = text, style = MaterialTheme.typography.body1 )
    }
}

@Composable
private fun colors(type: Int) = when (type) {
    /*Login*/
    1 -> ButtonDefaults.buttonColors(
        contentColor = Color.White,
        containerColor = Color.Blue,
        disabledContentColor = Color.White,
        disabledContainerColor = Color.Gray
    )

    2 -> ButtonDefaults.buttonColors(
        contentColor = Color.White,
        containerColor = Color.Green,
        disabledContentColor = Color.LightGray,
        disabledContainerColor = Color.DarkGray
    )

    3 -> ButtonDefaults.buttonColors(
        contentColor = Color.White,
        containerColor = Color.Red,
        disabledContentColor = Color.DarkGray,
        disabledContainerColor = Color.LightGray,
    )

    else -> ButtonDefaults.buttonColors(
        contentColor = Color.Gray,
        containerColor = Color.White,
        disabledContentColor = Color.Black,
        disabledContainerColor = Color.LightGray
    )
}