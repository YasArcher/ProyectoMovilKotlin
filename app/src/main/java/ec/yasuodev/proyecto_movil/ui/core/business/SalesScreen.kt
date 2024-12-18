package ec.yasuodev.proyecto_movil.ui.core.business

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.shared.models.AuxiliarSaleProduct
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SalesScreen(
    viewModel: SalesViewModel,
    navController: NavController,
    store: String,
    seller: String
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF9B59B6))
    ) {

        SalesContent(viewModel, navController, Modifier, store, seller)
        confirmButton(viewModel, seller)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SalesContent(
    viewModel: SalesViewModel,
    navController: NavController,
    modifier: Modifier,
    storeID: String,
    seller: String
) {
    val store by viewModel.store.observeAsState(Store("", "", "", "", false))
    val showDialog by viewModel.showDialog.observeAsState(false)
    var expanded by remember { mutableStateOf(false) }
    var transactionType by remember { mutableStateOf(TransactionType.SALE) }
    val income by viewModel.income.observeAsState(0.0)
    LaunchedEffect(key1 = viewModel) {
        val dateToday = java.time.LocalDate.now().toString()
        viewModel.fetchBusiness(storeID)
    }

    Column(modifier.fillMaxSize()) {
        // Encabezado
        HeaderSection(title = "Vender")

        Row(modifier.padding(horizontal = 16.dp)) {
            // Botón para abrir el modal en modo agregar
            AddButton(onClick = {
                transactionType = TransactionType.SALE
                viewModel.showDialog(true)
                expanded = false
            })
            TotalCard(income)
        }

        // Tabla de productos
        Spacer(modifier = Modifier.height(16.dp))

        ProductsTable(viewModel)

        Spacer(modifier = Modifier.weight(1f))
    }

    // Mostrar el modal si `showModal` es true
    // Modal para agregar producto
    if (showDialog) {
        StyledProductSelectionDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showDialog(false) },
            onProductSelected = { productName, quantity ->
                viewModel.addProductToTable(productName, quantity, seller)
            },
            seller
        )
    }
}

@Composable
fun HeaderSection(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
@Composable
fun TotalCard(total: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Padding adicional al lado derecho
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = "Total: $${"%.2f".format(total)}",
            color = Color(0xFF81C784), // Verde claro
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductsTable(viewModel: SalesViewModel) {
    val productsModel by viewModel.productsModel.observeAsState(emptyList())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableHeaderCell("Producto", Modifier.weight(1f))
            TableHeaderCell("Cantidad", Modifier.weight(1f))
            TableHeaderCell("Total", Modifier.weight(1f))
            TableHeaderCell("Acciones", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(productsModel) { product ->
                SaleTableRow(
                    product = product,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun TableHeaderCell(text: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .background(Color(0xFF3C1F62), shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SaleTableRow(product: AuxiliarSaleProduct, viewModel: SalesViewModel) {
    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditProductDialog(
            product = product,
            onDismiss = { showEditDialog = false },
            onSave = { newQuantity ->
                viewModel.editProductInTable(product.product, newQuantity)
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .background(Color(0xFF3C1F62), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = product.productName,
            modifier = Modifier.weight(1f),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${product.quantity}",
            modifier = Modifier.weight(1f),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "%.2f".format(product.total),
            modifier = Modifier.weight(1f),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { showEditDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint = Color.White
                )
            }

            IconButton(onClick = {
                viewModel.removeProductFromTable(product.product)
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun AddButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD8A3D9))
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Agregar",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Agregar", color = Color.White)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StyledProductSelectionDialog(
    viewModel: SalesViewModel,
    onDismiss: () -> Unit,
    onProductSelected: (product: Product, quantity: Int) -> Unit,
    seller: String
) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    val filteredProducts by viewModel.filteredProducts.observeAsState(emptyList())
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableIntStateOf(1) }
    val context = LocalContext.current
    val addState by viewModel.addState.observeAsState(initial = AddState.Loading)

    LaunchedEffect(searchQuery) {
        viewModel.filterProducts(searchQuery)
    }

    LaunchedEffect(addState) {
        if (addState is AddState.Success) {
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Seleccionar Producto",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5A005A) // Color principal morado
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
        text = {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White, // Fondo blanco
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Campo de búsqueda
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            searchQuery = query
                            viewModel.filterProducts(query) // Filtra productos en el ViewModel
                        },
                        label = { Text("Buscar producto") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Lista de productos filtrados
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(filteredProducts) { product ->
                            if (product.stock > 0) { // Valida que tenga stock disponible
                                ProductRow(product, isSelected = selectedProduct?.id == product.id) {
                                    selectedProduct = product
                                    searchQuery = product.name
                                }
                            }
                        }
                    }

                    // Selector de cantidad
                    QuantitySelector(
                        quantity = quantity,
                        onQuantityChange = { quantity = it },
                        maxQuantity = selectedProduct?.stock ?: 0
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedProduct?.let {
                        // Lógica de confirmación mejorada del segundo AlertDialog
                        onProductSelected(it, quantity)
                        coroutineScope.launch {
                            onDismiss.apply {
                                Toast.makeText(
                                    context,
                                    "Agregando Venta",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            viewModel.onAddSelected().apply {
                                when (addState) {
                                    is AddState.Success -> {
                                        Toast.makeText(
                                            context,
                                            (addState as AddState.Success).message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    is AddState.Error -> {
                                        Toast.makeText(
                                            context,
                                            (addState as AddState.Error).message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    else -> Unit
                                }
                            }
                        }
                        onDismiss() // Cierra el modal
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A005A)),
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedProduct != null && quantity <= (selectedProduct?.stock ?: 0) // Habilita solo si es válido
            ) {
                Text("Agregar", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar", color = Color.White)
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ProductRow(product: Product, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12F) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditProductDialog(
    product: AuxiliarSaleProduct,
    onDismiss: () -> Unit,
    onSave: (newQuantity: Int) -> Unit
) {
    var quantity by remember { mutableStateOf(product.quantity) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Editar Producto",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5A005A)
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Producto: ${product.productName}", style = MaterialTheme.typography.bodyLarge)
                QuantitySelector(
                    quantity = quantity,
                    onQuantityChange = { quantity = it },
                    maxQuantity = product.productStock + product.quantity
                )
                Divider(color = Color(0xFF5A005A), thickness = 1.dp)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (quantity in 1..(product.productStock + product.quantity)) {
                        onSave(quantity)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A005A)),
                enabled = quantity in 1..(product.productStock + product.quantity)
            ) {
                Text("Actualizar", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
            ) {
                Text("Cancelar", color = Color.White)
            }
        },
        modifier = Modifier.background(Color.White, shape = RoundedCornerShape(16.dp))
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun confirmButton(viewModel: SalesViewModel, seller: String) {
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            coroutineScope.launch {
                viewModel.confirmSales(seller)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A005A))
    ) {
        Text(
            text = "Confirmar Venta",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}
