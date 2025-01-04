package ec.yasuodev.proyecto_movil.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ec.yasuodev.proyecto_movil.ui.auth.login.LoginScreen
import ec.yasuodev.proyecto_movil.ui.auth.login.LoginViewModel
import ec.yasuodev.proyecto_movil.ui.auth.register.RegisterScreen
import ec.yasuodev.proyecto_movil.ui.auth.register.RegisterViewModel
import ec.yasuodev.proyecto_movil.ui.auth.reset.ResetScreen
import ec.yasuodev.proyecto_movil.ui.auth.reset.ResetViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraphMain(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "login",
    ) {
        composable("login") {
            val loginViewModel = remember { LoginViewModel() }
            LoginScreen(loginViewModel, navController)
        }
        composable("register") {
            val registerViewModel = remember { RegisterViewModel() }
            RegisterScreen(registerViewModel, navController)
        }
        composable("reset") {
            val resetViewModel = remember { ResetViewModel() }
            ResetScreen(resetViewModel, navController)
        }
        composable("clientNavGraph") {
            ClientNavGraph()
        }
        composable("sellerNavGraph") {
            SellerNavGraph()
        }
    }
}
