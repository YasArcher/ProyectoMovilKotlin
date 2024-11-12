package ec.yasuodev.proyecto_movil.ui.shared.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomHeaderShape(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(250.dp)) {
        val width = size.width
        val height = size.height

        drawPath(
            path = Path().apply {
                // Esquina superior izquierda redondeada
                moveTo(0f, 0f)
                quadraticBezierTo(0f, height * 0.3f, width * 0.2f, height * 0.3f)

                // Curva inferior izquierda
                cubicTo(
                    width * 0.25f, height,
                    width * 0.75f, height,
                    width * 0.8f, height * 0.3f
                )

                // Esquina superior derecha redondeada
                quadraticBezierTo(width, height * 0.3f, width, 0f)

                lineTo(width, 0f)
                close()
            },
            color = Color(0xFF9B86BE)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomHeaderShapePreview() {
    CustomHeaderShape()
}
