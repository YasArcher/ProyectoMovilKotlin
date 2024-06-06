package ec.yasuodev.proyecto_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ec.yasuodev.proyecto_movil.ui.login.ui.LoginScreen
import ec.yasuodev.proyecto_movil.ui.login.ui.LoginViewModel
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import ec.yasuodev.proyecto_movil.ui.theme.ProyectomovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectomovilTheme {

                LoginScreen(LoginViewModel());
            }
        }
    }
}