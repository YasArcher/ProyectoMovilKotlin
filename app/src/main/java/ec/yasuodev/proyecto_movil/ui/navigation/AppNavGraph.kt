import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ec.yasuodev.proyecto_movil.ui.auth.login.LoginScreen
import ec.yasuodev.proyecto_movil.ui.auth.login.LoginViewModel
import ec.yasuodev.proyecto_movil.ui.auth.register.RegisterScreen
import ec.yasuodev.proyecto_movil.ui.auth.register.RegisterViewModel
import ec.yasuodev.proyecto_movil.ui.auth.reset.ResetScreen
import ec.yasuodev.proyecto_movil.ui.auth.reset.ResetViewModel
import ec.yasuodev.proyecto_movil.ui.core.home.HomeScreen
import ec.yasuodev.proyecto_movil.ui.core.home.HomeViewModel
import ec.yasuodev.proyecto_movil.ui.core.home.SettingsScreen
import ec.yasuodev.proyecto_movil.ui.core.products.AddProductScreen
import ec.yasuodev.proyecto_movil.ui.core.products.AddProductViewModel
import ec.yasuodev.proyecto_movil.ui.core.products.EditProductScreen
import ec.yasuodev.proyecto_movil.ui.core.products.EditProductViewModel
import ec.yasuodev.proyecto_movil.ui.core.products.ProductsScreen
import ec.yasuodev.proyecto_movil.ui.core.products.ProductsViewModel
import ec.yasuodev.proyecto_movil.ui.core.profile.EditProfileScreen
import ec.yasuodev.proyecto_movil.ui.core.profile.EditProfileViewModel
import ec.yasuodev.proyecto_movil.ui.core.profile.ProfileScreen
import ec.yasuodev.proyecto_movil.ui.core.profile.ProfileViewModel
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.User

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf("home", "profile", "settings", "products")

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
                ProfileScreen(ProfileViewModel(), navController)
            }
            composable("settings") {
                SettingsScreen()
            }
            composable("editUser/{id}/{name}/{lastName}/{email}/{nickName}/{image}",
                arguments = listOf(
                    navArgument("id") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType },
                    navArgument("lastName") { type = NavType.StringType },
                    navArgument("email") { type = NavType.StringType },
                    navArgument("nickName") { type = NavType.StringType },
                    navArgument("image") { type = NavType.BoolType }
                )) { backStackEntry ->
                val user = User(
                    backStackEntry.arguments?.getString("id") ?: "",
                    backStackEntry.arguments?.getString("name") ?: "Usuario",
                    backStackEntry.arguments?.getString("lastName") ?: "Nuevo",
                    backStackEntry.arguments?.getString("email") ?: "",
                    backStackEntry.arguments?.getString("nickName") ?: "user",
                    backStackEntry.arguments?.getBoolean("image") ?: false
                )
                EditProfileScreen(EditProfileViewModel(), navController, user)
            }
            composable("products") {
                ProductsScreen(ProductsViewModel(), navController)
            }
            composable("addProducts/{store}", arguments = listOf(
                navArgument("store") { type = NavType.StringType }
            )) { backStackEntry ->
                AddProductScreen(
                    AddProductViewModel(),
                    navController,
                    backStackEntry.arguments?.getString("store") ?: ""
                )
            }
            composable("editProduct/{id}/{name}/{store}/{price}/{stock}", arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("store") { type = NavType.StringType },
                navArgument("price") { type = NavType.FloatType },
                navArgument("stock") { type = NavType.IntType }
            )) { backStackEntry ->
                val product = Product(
                    backStackEntry.arguments?.getString("id") ?: "",
                    backStackEntry.arguments?.getString("name") ?: "",
                    backStackEntry.arguments?.getString("store") ?: "",
                    backStackEntry.arguments?.getFloat("price") ?: 0.0f,
                    backStackEntry.arguments?.getInt("stock") ?: 0
                )
                EditProductScreen(EditProductViewModel(), navController, product)
            }
        }
    }
}