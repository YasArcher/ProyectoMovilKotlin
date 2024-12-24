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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.Store

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: ProductsViewModel, navController: NavController) {

    val context = LocalContext.current

    // Observamos la tienda y la lista de productos de nuestro ViewModel
    val store by viewModel.store.observeAsState(Store("", "", "", "", false))
    val products by viewModel.products.observeAsState(emptyList())

    // Verificamos token y pedimos datos de la tienda
    LaunchedEffect(viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        } else {
            viewModel.fetchStore(context)
        }
    }

    // Para el buscador
    var searchQuery by remember { mutableStateOf("") }

    // Para el modal de edición (opcional, si deseas usarlo en vez de la navegación)
    var showEditModal by remember { mutableStateOf(false) }
    var selectedProductName by remember { mutableStateOf("") }
    var selectedProductPrice by remember { mutableStateOf("") }
    var selectedProductStock by remember { mutableStateOf("") }

    Scaffold(
        // TopBar opcional: si quieres conservar la original, quítalo o modifícalo a tu gusto
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Productos",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        // Floating Action Button para agregar producto
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    addProduct(navController, store.id)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        },
        // Agregamos la BottomNavBar como en el estático
//        bottomBar = {
//            BottomNavBar(
//                navController = navController,
//                selectedItem = "Inicio",
//                onItemSelected = { selectedItem ->
//                    when (selectedItem) {
//                        "Inicio" -> navController.navigate("home")
//                        "Tiendas" -> navController.navigate("stores")
//                        "Órdenes" -> navController.navigate("orders")
//                        "Perfil" -> navController.navigate("profile")
//                    }
//                }
//            )
//        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ======== PARTE SUPERIOR CON FONDO Y BUSCADOR (Canvas + OutlinedTextField) ========
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Fondo curvo
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

                // Encabezado + Buscador
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Encabezado (icónicos, si los deseas conservar)
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
                        IconButton(onClick = { navController.navigate("clientHome") }) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Inicio",
                                tint = Color.White,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }

                    Text(
                        text = "Mis Productos",
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

            // ======== LISTA DE PRODUCTOS ========
            // Filtramos los productos con searchQuery
            val filteredProducts = products.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF3F3F3))
                    .padding(8.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductItem(
                        name = product.name,
                        price = product.price,
                        stock = product.stock,
                        imageUrl = "https://via.placeholder.com/150",
                        onEditClick = {
                            // Aquí puedes elegir abrir modal o navegar
                            // 1) Navegar a la pantalla de edición:
                            // editProduct(product, navController)

                            // 2) O abrir modal in-place (ejemplo):
                            selectedProductName = product.name
                            selectedProductPrice = product.price.toString()
                            selectedProductStock = product.stock.toString()
                            showEditModal = true
                        },
                        onDeleteClick = {
                            product.id?.let { viewModel.deleteProduct(it) }
                        }
                    )
                }
            }
        }

        // ======== MODAL PARA EDICIÓN (opcional si no deseas navegar) ========
        if (showEditModal) {
            EditProductModal(
                productName = selectedProductName,
                productPrice = selectedProductPrice,
                productStock = selectedProductStock,
                onNameChange = { selectedProductName = it },
                onPriceChange = { selectedProductPrice = it },
                onStockChange = { selectedProductStock = it },
                onSave = {
                    // Aquí podrías disparar viewModel.updateProduct(...) si lo tuvieras
                    showEditModal = false
                },
                onDismiss = {
                    showEditModal = false
                }
            )
        }
    }
}

/**
 *  Barra de navegación inferior adaptada de tu código "estático"
 *  Ajustada para recibir el `NavController`.
 */
//@Composable
//fun BottomNavBar(
//    navController: NavController,
//    selectedItem: String,
//    onItemSelected: (String) -> Unit
//) {
//    BottomNavigation(
//        backgroundColor = Color(0xFFF3F3F3),
//        contentColor = Color(0xFF4E4376)
//    ) {
//        val items = listOf(
//            NavBarItem("Inicio", Icons.Default.Home, "Inicio"),
//            NavBarItem("Tiendas", Icons.Default.ShoppingCart, "Tiendas"),
//            NavBarItem("Órdenes", ImageVector.vectorResource(id = R.drawable.box_svgrepo_com), "Órdenes"),
//            NavBarItem("Perfil", Icons.Default.Person, "Perfil")
//        )
//
//        items.forEach { item ->
//            BottomNavigationItem(
//                icon = {
//                    Icon(
//                        imageVector = item.icon,
//                        contentDescription = item.label,
//                        modifier = Modifier.size(24.dp)
//                    )
//                },
//                label = {
//                    Text(
//                        text = item.label,
//                        fontSize = 12.sp,
//                        color = if (selectedItem == item.label) Color(0xFF4E4376) else Color.Gray
//                    )
//                },
//                selected = selectedItem == item.label,
//                onClick = { onItemSelected(item.label) },
//                alwaysShowLabel = true,
//                selectedContentColor = Color(0xFF4E4376),
//                unselectedContentColor = Color.Gray
//            )
//        }
//    }
//}

data class NavBarItem(val route: String, val icon: ImageVector, val label: String)

/**
 *  Ítem de producto (adaptado) que llama a la lógica de ViewModel al borrar/editar.
 */
@Composable
fun ProductItem(
    name: String,
    price: Double,
    stock: Int,
    imageUrl: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
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
            // Imagen del producto
            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información del producto
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Precio: $$price",
                    fontSize = 16.sp,
                    color = Color(0xFF27AE60)
                )
                Text(
                    text = "En stock: $stock",
                    fontSize = 16.sp,
                    color = Color(0xFF2D9CDB)
                )
            }

            // Botones de acción
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                }
            }
        }
    }
}

/**
 *  Modal de edición (similar al del código estático).
 */
@Composable
fun EditProductModal(
    productName: String,
    productPrice: String,
    productStock: String,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)) // Fondo semitransparente
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onDismiss()
            }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .align(Alignment.Center),
            //elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                // Podrías mostrar la imagen del producto si lo deseas
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
                        containerColor = Color(0xFF9B51E0),
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

// ===================
// Lógica de navegar a otras pantallas que ya existía
// ===================
fun editProduct(product: Product, navController: NavController) {
    navController.navigate(
        "editProduct/${product.id}/${product.name}/${product.store}/${product.price}/${product.stock}/${product.category}"
    )
}

fun addProduct(navController: NavController, store: String) {
    navController.navigate("addProducts/${store}")
}
