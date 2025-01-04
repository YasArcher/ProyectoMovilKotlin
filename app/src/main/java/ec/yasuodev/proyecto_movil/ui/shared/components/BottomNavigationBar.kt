package ec.yasuodev.proyecto_movil.ui.shared.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ec.yasuodev.proyecto_movil.R

@Composable
fun BottomNavigationBar(navController: NavController, rol: String) {
    val items = when (rol) {
        "seller" -> listOf(
            BottomNavItem.Home,
            BottomNavItem.Profile,
            BottomNavItem.Products,
            BottomNavItem.Settings
        )
        "cliente" -> listOf(
            BottomNavItem.Home,
            BottomNavItem.Profile,
            BottomNavItem.Shop, // Agregar ítem específico para clientes
            BottomNavItem.Settings
        )
        else -> listOf(
            BottomNavItem.Home,
            BottomNavItem.Profile
        )
    }

    BottomNavigation(
        contentColor = MaterialTheme.colorScheme.onSurface,
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = true
            )
        }
    }
}

sealed class BottomNavItem(var title: String, var icon: Int, var route: String) {
    object Home : BottomNavItem("Home", R.drawable.home_24px, "home")
    object Profile : BottomNavItem("Perfil", R.drawable.person_24px, "profile")
    object Products : BottomNavItem("Productos", R.drawable.grocery_24px, "products")
    object Settings : BottomNavItem("Configuración", R.drawable.settings_24px, "settings")
    object Shop : BottomNavItem("Tienda", R.drawable.shopping_bag, "shop")
}
