package ec.yasuodev.proyecto_movil.ui.shared.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun DynamicText(message: String, state: Boolean) {
    val color = if (state) Color(0xFF00FF00) else Color(0xFFFF0000)
    Text(
        text = message,
        style = TextStyle(fontSize = 16.sp),
        color = color
    )
}