package ec.yasuodev.proyecto_movil

import AppNavGraph
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ec.yasuodev.proyecto_movil.ui.theme.ProyectomovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectomovilTheme {
                AppNavGraph()
            }
        }
    }
}