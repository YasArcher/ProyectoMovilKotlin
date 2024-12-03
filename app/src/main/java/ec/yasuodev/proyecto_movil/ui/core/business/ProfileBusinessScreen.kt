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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import ec.yasuodev.proyecto_movil.R

@Composable
fun ProfileBusinessScreen(
    navController: NavController,
    viewModel: ProfileBusinessViewModel = viewModel()
) {
    val store = viewModel.store // Observa los cambios en `store`

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                selectedItem = "Perfil",
                onItemSelected = { selectedItem ->
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
                .background(Color(0xFFF3F3F3))
        ) {
            // Fondo morado
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, size.height * 0.6f)
                    cubicTo(
                        size.width * 0.2f, size.height,
                        size.width * 0.8f, size.height * 0.4f,
                        size.width, size.height * 0.6f
                    )
                    lineTo(size.width, 0f)
                    lineTo(0f, 0f)
                    close()
                }
                drawPath(path, color = Color(0xFF9B51E0))
            }

            // Íconos superiores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                IconButton(
                    onClick = {
                        // Acción para las opciones
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "More Options",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            // Tarjeta de información
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Imagen del negocio
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

                        // Información del negocio
                        Text("Nombre Negocio:", fontSize = 16.sp, color = Color.Gray)
                        Text(store.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Dueño:", fontSize = 16.sp, color = Color.Gray)
                        Text(store.owner, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón para editar
                        Button(
                            modifier = Modifier.fillMaxWidth(1f),
                            onClick = { navController.navigate("editBusiness/${store.id}") }) {
                            Text("Editar")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileBusinessScreen() {
    val fakeStore = Store(
        id = "1",
        name = "Micromercados y supermercados la favorita",
        owner = "Edison Ortiz",
        business_image = ""
    )
    val fakeViewModel = object : ProfileBusinessViewModel() {
        override val store = fakeStore
    }

    ProfileBusinessScreen(
        navController = rememberNavController(),
        viewModel = fakeViewModel
    )
}