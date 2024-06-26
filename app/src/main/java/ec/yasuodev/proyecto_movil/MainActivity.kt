package ec.yasuodev.proyecto_movil

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import ec.yasuodev.proyecto_movil.ui.navigation.AppNavGraph
import ec.yasuodev.proyecto_movil.ui.theme.ProyectomovilTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectomovilTheme {
                AppNavGraph()
            }
        }
    }
}