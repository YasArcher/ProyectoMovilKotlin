import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.core.home.HomeViewModel
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicField

@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Home(modifier = Modifier, viewModel, navController)
    }
}

@Composable
fun Home(modifier: Modifier, viewModel: HomeViewModel, navController: NavController) {
    // Implementación de la pantalla principal
    DynamicField(value = "", onTextFieldChange = { } , tipo = 2)
}

@Composable
fun ProfileScreen() {
    println("*******************Hola estoy en profile*******************")
}

@Composable
fun SettingsScreen() {
    println("*******************Hola estoy en settings*******************")
}
