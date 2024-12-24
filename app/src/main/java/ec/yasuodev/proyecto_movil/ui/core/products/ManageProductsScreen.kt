package ec.yasuodev.proyecto_movil.ui.core.products

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ec.yasuodev.proyecto_movil.R

@Composable
fun ManageProductsScreen(navController: NavController) { // Added navController as a parameter
    var showEditModal by remember { mutableStateOf(false) } // Control del modal
    var selectedProductName by remember { mutableStateOf("Arroz") }
    var selectedProductPrice by remember { mutableStateOf("20") }
    var selectedProductStock by remember { mutableStateOf("50") }
    var searchQuery by remember { mutableStateOf("") } // Estado para el texto de búsqueda

    Scaffold(
        bottomBar = {
            BottomNavBar1(
                navController = navController, // Pass navController to BottomNavBar
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
                modifier = Modifier
                    .fillMaxWidth()
            ) {
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
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Encabezado
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
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
                        text = "Inicio",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 25.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Buscador
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Busca un producto...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Borrar texto")
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
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
                items(10) { index -> // Cambiar a la lista real de productos
                    if ("Producto $index".contains(searchQuery, ignoreCase = true)) {
                        ProductItem(
                            name = "Producto $index",
                            price = (20 + index).toDouble(),
                            stock = 50 - index,
                            imageUrl = "https://via.placeholder.com/150",
                            onEditClick = {
                                selectedProductName = "Producto $index"
                                selectedProductPrice = "${20 + index}"
                                selectedProductStock = "${50 - index}"
                                showEditModal = true
                            },
                            onDeleteClick = { /* Manejar eliminación */ }
                        )
                    }
                }
            }
        }

        // Mostrar modal de edición si es necesario
        if (showEditModal) {
            EditProductModal(
                productName = selectedProductName,
                productPrice = selectedProductPrice,
                productStock = selectedProductStock,
                onNameChange = { selectedProductName = it },
                onPriceChange = { selectedProductPrice = it },
                onStockChange = { selectedProductStock = it },
                onSave = {
                    // Guardar cambios en el producto
                    showEditModal = false
                },
                onDismiss = {
                    // Cerrar el modal
                    showEditModal = false
                }
            )
        }

    }
}

@Composable
fun BottomNavBar1(
    navController: NavController, // Added navController as a parameter
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    BottomNavigation(
        backgroundColor = Color(0xFFF3F3F3),
        contentColor = Color(0xFF4E4376)
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
                        modifier=Modifier.size(24.dp)
                    )
                },
                label={
                    Text(text=item.label, fontSize=12.sp,
                        color=if(selectedItem==item.label) Color(0xFF4E4376) else Color.Gray)
                },
                selected=selectedItem==item.label,
                onClick={onItemSelected(item.label)},
                alwaysShowLabel=true,
                selectedContentColor=Color(0xFF4E4376),
                unselectedContentColor=Color.Gray
            )
        }
    }
}

data class NavBarItem1(val route: String, val icon: ImageVector, val label: String)

@Composable
fun ProductItem1(
    name: String,
    price: Int,
    stock: Int,
    imageUrl: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            Image(
                painter = rememberImagePainter(data=imageUrl),
                contentDescription=null,
                modifier=Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier=Modifier.width(16.dp))

            // Información del producto
            Column(modifier=Modifier.weight(1f)) {
                Text(text=name, fontSize=18.sp, fontWeight=FontWeight.Bold)
                Text(text="Precio: $$price", fontSize=16.sp, color=Color(0xFF27AE60)) // Verde para el precio
                Text(text="En stock: $stock", fontSize=16.sp, color=Color(0xFF2D9CDB)) // Azul para el stock
            }

            // Botones de acción
            Row {
                IconButton(onClick=onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription=null, tint=Color.Gray)
                }
                IconButton(onClick=onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription=null, tint=Color.Red)
                }
            }
        }
    }
}

@Composable
fun EditProductModal1(
    productName: String,
    productPrice: String,
    productStock: String,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
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
                    text = "EDITAR PRODUCTO",
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
                    value = productName,
                    onValueChange = onNameChange,
                    label = { Text("Nombre Producto") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = productPrice,
                    onValueChange = onPriceChange,
                    label = { Text("Precio Producto") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = productStock,
                    onValueChange = onStockChange,
                    label = { Text("Stock Producto") },
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
fun ManageProductsScreenPreview() {
    val navController = rememberNavController()
    ManageProductsScreen(navController = navController)
}

