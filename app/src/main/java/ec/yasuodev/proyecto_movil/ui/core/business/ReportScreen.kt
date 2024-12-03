package ec.yasuodev.proyecto_movil.ui.core.business

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReportingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF9B59B6)) // Fondo morado
    ) {
        Column {
            // Encabezado
            ReportingHeader()

            // Gráfico y detalles
            ReportingContent()
        }
    }
}

@Composable
fun ReportingHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ícono superior derecho
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Gráficos de reportería",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ReportingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gráfico circular estilo dashboard
        CircularChartDashboard(percentageIncome = 50f, percentageExpenses = 50f)

        Spacer(modifier = Modifier.height(16.dp))

        // Detalles de ingresos y egresos
        ReportingDetails()
    }
}

@Composable
fun CircularChartDashboard(percentageIncome: Float, percentageExpenses: Float) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        // Fondo del gráfico circular (gráfico principal)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diameter = size.minDimension
            drawArc(
                color = Color(0xFF3F51B5), // Color para ingresos (azul)
                startAngle = -90f,
                sweepAngle = percentageIncome * 360 / 100,
                useCenter = false,
                size = size,
                style = Stroke(width = 40f, cap = StrokeCap.Round)
            )
            drawArc(
                color = Color(0xFF673AB7), // Color para egresos (morado)
                startAngle = -90f + percentageIncome * 360 / 100,
                sweepAngle = percentageExpenses * 360 / 100,
                useCenter = false,
                size = size,
                style = Stroke(width = 40f, cap = StrokeCap.Round)
            )
        }
        // Etiqueta en el centro del gráfico
        Text(
            text = "50%\n50%",
            color = Color(0xFF7E57C2),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ReportingDetails() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReportingRow("Ingresos", 10.5, Color(0xFF3F51B5)) // Azul
        ReportingRow("Egresos", 10.5, Color(0xFF673AB7)) // Morado
        ReportingRow("Total", 21.0, Color(0xFFE1BEE7)) // Rosa claro
    }
}

@Composable
fun ReportingRow(label: String, value: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = "$${value}",
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}


@Composable
fun BottomNavigationItem(
    label: String,
    icon: @Composable () -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF7E57C2) else Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFF7E57C2) else Color.Gray
        )
    }
}
