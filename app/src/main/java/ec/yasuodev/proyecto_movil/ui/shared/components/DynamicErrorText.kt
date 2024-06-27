package ec.yasuodev.proyecto_movil.ui.shared.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun DynamicText(
    message: String,
    state: Boolean,
    textSize: Int = 16,
    successColor: Color = Color(0xFF00FF00),
    errorColor: Color = Color(0xFFFF0000),
    modifier: Modifier = Modifier
) {
    val color = if (state) successColor else errorColor
    Text(
        text = message,
        style = TextStyle(
            fontSize = textSize.sp,
            color = color
        ).merge(MaterialTheme.typography.bodyLarge),
        modifier = modifier
    )
}
