import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ec.yasuodev.proyecto_movil.ui.auth.login.LoginScreen
import ec.yasuodev.proyecto_movil.ui.auth.login.LoginViewModel
import ec.yasuodev.proyecto_movil.ui.auth.register.RegisterScreen
import ec.yasuodev.proyecto_movil.ui.auth.register.RegisterViewModel
import ec.yasuodev.proyecto_movil.ui.auth.reset.ResetScreen
import ec.yasuodev.proyecto_movil.ui.auth.reset.ResetViewModel
import ec.yasuodev.proyecto_movil.ui.core.home.HomeViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf("home", "profile", "settings")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(LoginViewModel(), navController)
            }
            composable("register") {
                RegisterScreen(RegisterViewModel(), navController)
            }
            composable("reset") {
                ResetScreen(ResetViewModel(), navController)
            }
            composable("home") {
                HomeScreen(HomeViewModel(), navController)
            }
            composable("profile") {
                ProfileScreen()
            }
            composable("settings") {
                SettingsScreen()
            }
        }
    }
}