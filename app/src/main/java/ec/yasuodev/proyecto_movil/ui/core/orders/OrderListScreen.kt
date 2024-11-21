package ec.yasuodev.proyecto_movil.ui.core.orders

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ec.yasuodev.proyecto_movil.R

@Composable
fun OrderListScreen(navController: NavController) {
    var showEditModal by remember { mutableStateOf(false) } // Control del modal
    var selectedUserName by remember { mutableStateOf("Mateo") }
    var selectedUserLastName by remember { mutableStateOf("Barona") }
    var selectedUserNickname by remember { mutableStateOf("Flaco") }
    var searchQuery by remember { mutableStateOf("") } // Estado para el texto de búsqueda

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController, // Assuming navController is available here
                selectedItem = "Inicio", // Marca "Inicio" como seleccionado
                onItemSelected = { selectedItem -> // Manejo de la navegación según el elemento seleccionado
                    when (selectedItem) {
                        "Inicio" -> navController.navigate("home")
                        "Tiendas" -> navController.navigate("stores")
                        "Órdenes" -> navController.navigate("orders")
                        "Perfil" -> navController.navigate("profile")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Fondo superior con buscador
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Canvas(
                    modifier = Modifier.fillMaxWidth().height(275.dp)
                ) {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.8f)
                        cubicTo(
                            size.width * 0.4f,
                            size.height,
                            size.width * 1f,
                            size.height * 0.6f,
                            size.width,
                            size.height * 0.8f
                        )
                        lineTo(size.width, 0f)
                        lineTo(0f, 0f)
                        close()
                    }
                    drawPath(path, color = Color(0xFF9B51E0))
                }
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Encabezado
                    Row(
                        modifier = Modifier.padding(top = 10.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        IconButton(onClick = { /* Opciones adicionales */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = Color.White,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                        IconButton(onClick = { /* Navegar a inicio */ }) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Inicio",
                                tint = Color.White,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                    Text(
                        text = "Historial de pedidos",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 60.dp)
                    )
                }
            }

            // Lista de productos
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF3F3F3))
                    .padding(8.dp)
            ) {
                items(10) { index ->
                    if ("Producto $index".contains(searchQuery, ignoreCase = true)) {
                        UserItem(
                            name = "Tienda $index",
                            value = 10.0 + index,
                            onEditClick = {
                                selectedUserName = "Usuario $index"
                                selectedUserLastName = "Apellido $index"
                                selectedUserNickname = "Apodo $index"
                                showEditModal = false
                            }
                        )
                    }
                }
            }
        }

        // Mostrar modal de edición si es necesario
        if (showEditModal) {
            EditUserModal(
                userName = selectedUserName,
                userLastName = selectedUserLastName,
                userNickname = selectedUserNickname,
                onNameChange = { selectedUserName = it },
                onLastNameChange = { selectedUserLastName = it },
                onNicknameChange = { selectedUserNickname = it },
                onSave = { showEditModal = false },
                onDismiss = { showEditModal = false }
            )
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
        val items =
            listOf(
                NavBarItem("Inicio", Icons.Default.Home, "Inicio"),
                NavBarItem("Tiendas", Icons.Default.ShoppingCart, "Tiendas"),
                NavBarItem("Órdenes", ImageVector.vectorResource(id= R.drawable.box_svgrepo_com), "Órdenes"),
                NavBarItem("Perfil", Icons.Default.Person, "Perfil")
            )
        items.forEach { item ->
            BottomNavigationItem(
                icon={
                    Icon(
                        imageVector=item.icon,
                        contentDescription=item.label,
                        modifier=Modifier.size(24.dp) // Tamaño del ícono
                    )
                },
                label={
                    Text(text=item.label, fontSize=12.sp,
                        color=if(selectedItem==item.label) Color(0xFF4E4376) else Color.Gray) // Resalta el seleccionado
                },
                selected=selectedItem==item.label,
                onClick={onItemSelected(item.label)},
                alwaysShowLabel=true, // Muestra las etiquetas debajo de los íconos
                selectedContentColor=Color(0xFF4E4376), // Morado oscuro para el seleccionado
                unselectedContentColor=Color.Gray
            )
        }
    }
}

data class NavBarItem(val route: String, val icon: ImageVector, val label: String)

@Composable
fun UserItem(
    name: String,
    value: Double,
    onEditClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9B51E0), textDecoration = TextDecoration.Underline)
                Text(text = "Valor: "+ value.toString() + " $", fontSize = 14.sp, color = Color(0xFF9B51E0))
            }
            // Botones de acción
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(ImageVector.vectorResource(id = R.drawable.eye_svgrepo_com), contentDescription = null, tint = Color(0xFF72BF85))
                }
            }
        }
    }
}

@Composable
fun EditUserModal(
    userName: String,
    userLastName: String,
    userNickname: String,
    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onNicknameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit // Nueva función para cerrar el modal
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)) // Fondo semitransparente
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() } // Cierra el modal al tocar fuera
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .align(Alignment.Center),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón para cerrar el modal
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar modal",
                            tint = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "EDITAR USUARIO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF4E4376),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Image(
                    painter = rememberImagePainter(data = ""),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = userName,
                    onValueChange = onNameChange,
                    label = { Text("Nombre del usuario") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = userLastName,
                    onValueChange = onLastNameChange,
                    label = { Text("Apellido del usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = userNickname,
                    onValueChange = onNicknameChange,
                    label = { Text("Apodo del usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF9B51E0),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Editar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}


@Composable
fun BottomNavBar(selectedItem: String, onItemSelected: (String) -> Unit) {
    BottomNavigation(
        backgroundColor = Color(0xFFF3F3F3)
    ) {
        listOf("Inicio", "Tiendas", "Órdenes", "Perfil").forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null
                    )
                },
                label = { Text(item) },
                selected = selectedItem == item,
                onClick = { onItemSelected(item) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UsersListScreenPreview() {
    val navController = rememberNavController()
    OrderListScreen(navController = navController)
}