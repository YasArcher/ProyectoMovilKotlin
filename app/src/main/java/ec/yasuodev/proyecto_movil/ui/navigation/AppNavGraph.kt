package ec.yasuodev.proyecto_movil.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import ec.yasuodev.proyecto_movil.ui.core.business.BusinessScreen
import ec.yasuodev.proyecto_movil.ui.core.business.BusinessViewModel
import ec.yasuodev.proyecto_movil.ui.core.business.SalesScreen
import ec.yasuodev.proyecto_movil.ui.core.business.SalesViewModel
import ec.yasuodev.proyecto_movil.ui.core.home.HomeScreen
import ec.yasuodev.proyecto_movil.ui.core.home.HomeViewModel
import ec.yasuodev.proyecto_movil.ui.core.manager.ManagerScreen
import ec.yasuodev.proyecto_movil.ui.core.manager.ManagerViewModel
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
import ec.yasuodev.proyecto_movil.ui.shared.components.BottomNavigationBar
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.User

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf("home", "profile", "manager", "products")
    val context = LocalContext.current;
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
            composable("manager") {
                ManagerScreen(ManagerViewModel(), navController)
            }
            composable("editUser/{id}/{name}/{lastName}/{email}/{nickName}/{image}",
                arguments = listOf(
                    navArgument("id") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType },
                    navArgument("lastName") { type = NavType.StringType },
                    navArgument("email") { type = NavType.StringType },
                    navArgument("nickName") { type = NavType.StringType },
                    navArgument("image") { type = NavType.StringType }
                )) { backStackEntry ->
                val user = User(
                    backStackEntry.arguments?.getString("id") ?: "",
                    backStackEntry.arguments?.getString("name") ?: "Usuario",
                    backStackEntry.arguments?.getString("lastName") ?: "Nuevo",
                    backStackEntry.arguments?.getString("email") ?: "",
                    backStackEntry.arguments?.getString("nickName") ?: "user",
                    backStackEntry.arguments?.getString("image") ?: "noImage"
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
                navArgument("price") { type = NavType.StringType },
                navArgument("stock") { type = NavType.IntType }
            )) { backStackEntry ->
                val product = Product(
                    backStackEntry.arguments?.getString("id") ?: "",
                    backStackEntry.arguments?.getString("name") ?: "",
                    backStackEntry.arguments?.getString("store") ?: "",
                    backStackEntry.arguments?.getString("price")?.toDouble() ?: 0.0,
                    backStackEntry.arguments?.getInt("stock") ?: 0
                )
                EditProductScreen(EditProductViewModel(), navController, product)
            }
            composable("business/{store}/{seller}", arguments = listOf(
                navArgument("store") { type = NavType.StringType },
                navArgument("seller") { type = NavType.StringType }
            )) { backStackEntry ->
                BusinessScreen(
                    BusinessViewModel(context),
                    navController,
                    backStackEntry.arguments?.getString("store") ?: "",
                    backStackEntry.arguments?.getString("seller") ?: ""
                )
            }
            composable("sales/{store}/{seller}", arguments = listOf(
                navArgument("store") { type = NavType.StringType },
                navArgument("seller") { type = NavType.StringType }
            )) { backStackEntry ->
                SalesScreen(
                    SalesViewModel(context),
                    navController,
                    backStackEntry.arguments?.getString("store") ?: "",
                    backStackEntry.arguments?.getString("seller") ?: ""
                )
            }
        }
    }
}