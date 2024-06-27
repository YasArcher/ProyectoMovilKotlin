package ec.yasuodev.proyecto_movil.ui.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DynamicButton(
    type: Int,
    text: String,
    enable: Boolean,
    method: () -> Unit,
    modifier: Modifier = Modifier,
    textSize: Float = 16f
) {
    Button(
        onClick = { method() },
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = buttonColors(type),
        enabled = enable
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = textSize.sp)
        )
    }
}

@Composable
private fun buttonColors(type: Int) = when (type) {
    1 -> ButtonDefaults.buttonColors(
        contentColor = Color.White,
        containerColor = Color(0xFF1E88E5), // Blue
        disabledContentColor = Color.White,
        disabledContainerColor = Color.Gray
    )
    2 -> ButtonDefaults.buttonColors(
        contentColor = Color.White,
        containerColor = Color(0xFF43A047), // Green
        disabledContentColor = Color.LightGray,
        disabledContainerColor = Color.DarkGray
    )
    3 -> ButtonDefaults.buttonColors(
        contentColor = Color.White,
        containerColor = Color(0xFFE53935), // Red
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