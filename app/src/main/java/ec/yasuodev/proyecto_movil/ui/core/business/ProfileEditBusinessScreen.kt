package ec.yasuodev.proyecto_movil.ui.core.business

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.shared.models.Store

@Composable
fun ProfileEditBusinessScreen(
    navController: NavController,
    viewModel: ProfileBusinessViewModel = viewModel()
) {
    val store = viewModel.store

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                selectedItem = "Perfil", // Marca "Perfil" como seleccionado
                onItemSelected = { selectedItem ->
                    // Manejo de la navegación según el elemento seleccionado
                    when (selectedItem) {
                        "Inicio" -> navController.navigate("home")
                        "Tiendas" -> navController.navigate("stores")
                        "Órdenes" -> navController.navigate("orders")
                        "Perfil" -> { /* Ya estás en esta pantalla */ }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F3F3)) // Color de fondo = gris claro
                .padding(innerPadding) // Ajusta el contenido para que no se superponga con el navbar
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, size.height * 0.6f)
                    cubicTo(
                        size.width * 0.2f, size.height * 1.2f,
                        size.width * 0.8f, size.height * 0.4f,
                        size.width, size.height * 0.8f
                    )
                    lineTo(size.width, 0f)
                    lineTo(0f, 0f)
                    close()
                }
                drawPath(
                    path = path,
                    color = Color(0xFF9B51E0) // Morado
                )
            }

            // Íconos superiores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono de tres puntos
                IconButton(
                    onClick = {
                        // Acción para las opciones
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }

                // Ícono de casa
                IconButton(
                    onClick = {
                        navController.navigate("home")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home Icon",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            // Contenedor principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Tarjeta que incluye la imagen y el contenido
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Imagen redondeada
                        Image(
                            painter = rememberImagePainter(data = store.business_image),
                            contentDescription = "Business Image",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nombre del negocio
                        Text(
                            modifier = Modifier.padding(bottom = 10.dp),
                            text = "Nombre Negocio:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        Text(
                            text = store.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nombre del dueño
                        Text(
                            modifier = Modifier.padding(bottom = 7.dp),
                            text = "Dueño:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        Text(
                            text = store.owner,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botones: Editar y Eliminar
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate("editBusiness/${store.id}")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B51E0))
                            ) {
                                Text(text = "Editar", color = Color.White)
                            }
                            Button(
                                onClick = {

                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60))
                            ) {
                                Text(text = "Eliminar", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    navController: NavController,
    selectedItem: String, // Indica el elemento seleccionado, como "Inicio", "Tiendas", etc.
    onItemSelected: (String) -> Unit // Acción a realizar cuando se selecciona un elemento
) {
    BottomNavigation(
        backgroundColor = Color(0xFFF3F3F3), // Fondo gris claro
        contentColor = Color(0xFF4E4376) // Color de los íconos y texto
    ) {
        val items = listOf(
            NavBarItem("Inicio", Icons.Default.Home, "Inicio"),
            NavBarItem("Tiendas", Icons.Default.ShoppingCart, "Tiendas"),
            NavBarItem("Órdenes", ImageVector.vectorResource(id = R.drawable.store_svgrepo_com), "Órdenes"),
            NavBarItem("Perfil", Icons.Default.Person, "Perfil")
        )

        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp) // Tamaño del ícono
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp, // Tamaño de texto
                        color = if (selectedItem == item.label) Color(0xFF4E4376) else Color.Gray // Resalta el seleccionado
                    )
                },
                selected = selectedItem == item.label,
                onClick = { onItemSelected(item.label) },
                alwaysShowLabel = true, // Muestra las etiquetas debajo de los íconos
                selectedContentColor = Color(0xFF4E4376), // Morado oscuro para el seleccionado
                unselectedContentColor = Color.Gray
            )
        }
    }
}

data class NavBarItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Preview(showBackground = true)
@Composable
fun PreviewProfileEditBusinessScreenWithBottomNavBar() {
    val fakeStore = Store(
        id = "1",
        name = "Inventory Hub",
        owner = "Edison Ortiz",
        business_image = ""
    )
    val fakeViewModel = object : ProfileBusinessViewModel() {
        override val store = fakeStore
    }

    val navController = rememberNavController()

    ProfileEditBusinessScreen(
        navController = navController,
        viewModel = fakeViewModel
    )
}
