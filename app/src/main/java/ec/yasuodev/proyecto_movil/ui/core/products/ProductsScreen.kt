package ec.yasuodev.proyecto_movil.ui.core.products

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.Store

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: ProductsViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Productos",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF9B51E0) // Color morado del primer diseño
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addProduct(navController, viewModel.store.value?.id ?: "") },
                containerColor = Color(0xFF9B51E0) // Color morado consistente
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto", tint = Color.White)
            }
        },
        containerColor = Color(0xFFF3F3F3) // Fondo claro
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ProductsContent(viewModel, navController)
        }
    }
}

@Composable
fun ProductsContent(viewModel: ProductsViewModel, navController: NavController) {
    val context = LocalContext.current
    val store: Store by viewModel.store.observeAsState(initial = Store("", "", "", ""))
    val products: List<Product> by viewModel.products.observeAsState(initial = emptyList())

    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        }
        viewModel.fetchStore(context)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Fondo curvo en la parte superior
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
                color = Color(0xFF9B51E0) // Fondo morado
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HeaderRow()

        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(products) { product ->
                ProductCard(product = product, viewModel = viewModel, navController = navController)
            }
        }
    }
}

@Composable
fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Producto",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = "Precio",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Stock",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Acciones",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ProductCard(product: Product, viewModel: ProductsViewModel, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp) // Bordes redondeados
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1.3f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${product.price} $",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = product.stock.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    IconButton(
                        onClick = { product.id?.let { viewModel.deleteProduct(it) } },
                        modifier = Modifier
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                    }
                    IconButton(onClick = { editProduct(product, navController) }, modifier = Modifier) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Gray)
                    }
                }
            }
        }
    }
}

fun editProduct(product: Product, navController: NavController) {
    navController.navigate("editProduct/${product.id}/${product.name}/${product.store}/${product.price}/${product.stock}")
}

fun addProduct(navController: NavController, store: String) {
    navController.navigate("addProducts/${store}")
}