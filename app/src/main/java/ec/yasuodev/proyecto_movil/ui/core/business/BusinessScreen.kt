package ec.yasuodev.proyecto_movil.ui.core.business

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.shared.models.AuxiliarSaleProduct
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.Purchase
import ec.yasuodev.proyecto_movil.ui.shared.models.Sale
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import ec.yasuodev.proyecto_movil.ui.shared.models.Invoice

enum class TransactionType {
    SALE, PURCHASE
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BusinessScreen(
    viewModel: BusinessViewModel,
    navController: NavController,
    store: String,
    seller: String
) {
    val storee by viewModel.store.observeAsState(Store("", "", "", ""))
    LaunchedEffect(key1 = viewModel) {
        viewModel.fetchBusiness(store)
        viewModel.getInvoicesByDate(store, java.time.LocalDate.now().toString())
    }
    Scaffold(
        containerColor = Color(0xFFF5F5F5) // Fondo claro
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column {
                // Cabecera morada con texto y carta de estadísticas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp) // Ajusta la altura para incluir la carta
                        .background(
                            color = Color(0xFF9B86BE),
                            shape = RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.width(4.dp)) // Espacio entre el texto y el ícono
                            IconButton(onClick = {
                                navController.navigate("edit_business_profile/${storee.id}")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Más opciones",
                                    tint = Color.White // Cambia el color si es necesario
                                )
                            }
                            Text(
                                text = "Transacciones",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        StatsCard(modifier = Modifier.padding(horizontal = 16.dp), viewModel, navController)
                    }
                }

                // Contenido principal
                BusinessContent(viewModel, navController, Modifier, store, seller)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BusinessContent(
    viewModel: BusinessViewModel,
    navController: NavController,
    modifier: Modifier,
    storeID: String,
    seller: String
) {
   // val store by viewModel.store.observeAsState(Store("", "", "", ""))
    val showDialog by viewModel.showDialog.observeAsState(false)
    var expanded by remember { mutableStateOf(false) }
    var transactionType by remember { mutableStateOf(TransactionType.SALE) }

    LaunchedEffect(key1 = viewModel) {
        val dateToday = java.time.LocalDate.now().toString()
        viewModel.fetchBusiness(storeID)
        viewModel.getExpendituresByDate(storeID, dateToday)
    }

    Box(modifier.fillMaxSize()
        .background(Color.White)) {
        Column(modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {



            Spacer(modifier = Modifier.padding(10.dp))
            Column(modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Productos vendidos hoy",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 18.sp // Tamaño de fuente ajustado
                        ),
                        color = Color(0xFF6A3D98),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    // Encabezado de la tabla con elementos separados y bordes redondeados
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .background(
                                    color =  Color(0xFF363062),
                                    shape = RoundedCornerShape(16.dp) // Bordes redondeados
                                )
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Producto",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp)) // Separación entre encabezados

                        Box(
                            modifier = Modifier
                                .weight(1.4f)
                                .background(
                                    color = Color(0xFF363062),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cantidad",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp)) // Separación entre encabezados

                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .background(
                                    color =  Color(0xFF363062),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp)) // Separación entre encabezados

                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .background(
                                    color =  Color(0xFF363062),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Acciones",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de productos
                    SalesList(viewModel = viewModel, modifier = modifier)
                }

                Spacer(modifier = Modifier.padding(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Egresos del día", style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 18.sp // Tamaño de fuente ajustado
                    ), color = Color(0xFF6A3D98))
                    Spacer(modifier = Modifier.padding(10.dp))

                    // Encabezado de la tabla con elementos separados y bordes redondeados
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .background(
                                    color =  Color(0xFF363062),
                                    shape = RoundedCornerShape(16.dp) // Bordes redondeados
                                )
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Razón",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp)) // Separación entre encabezados

                        Box(
                            modifier = Modifier
                                .weight(1.4f)
                                .background(
                                    color =  Color(0xFF363062),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Valor",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp)) // Separación entre encabezados

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = Color(0xFF363062)   ,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Acciones",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.padding(5.dp))

                    // Lista de egresos
                    PurchasesList(viewModel = viewModel, modifier = modifier)
                }

            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { expanded = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, "Floating action button", tint = Color.White)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    //transactionType = TransactionType.SALE
                    //viewModel.showDialog(true)
                    //expanded = false
                    navController.navigate("sales/${storeID}/${seller}")
                }) {
                    Text("Agregar Venta")
                }
                DropdownMenuItem(onClick = {
                    transactionType = TransactionType.PURCHASE
                    viewModel.showDialog(true)
                    expanded = false
                }) {
                    Text("Agregar Egreso")
                }
            }
        }
    }
    if (showDialog) {
        when (transactionType) {
            TransactionType.SALE -> {
//                ProductSelectionDialog(
//                    viewModel = viewModel,
//                    onDismiss = { viewModel.showDialog(false) },
//                    onProductSelected = { product, quantity ->
//                        viewModel.showDialog(false)
//                    },
//                    seller
//                )
            }

            TransactionType.PURCHASE -> {
                PurchaseDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showDialog(false) },
                    onPurchaseAdded = { reason, amount ->
                        viewModel.showDialog(false)
                    },
                    storeID
                )
            }
        }
    }
}

@Composable
fun StatsCard(modifier: Modifier, viewModel: BusinessViewModel, navController: NavController) {
    val income by viewModel.income.observeAsState(0.0)
    val expenditures by viewModel.expenditures.observeAsState(0.0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .clickable {

                navController.navigate("reporteria") // Reemplaza "new_view_route" por tu ruta configurada
            },
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Transacciones del día",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Ingresos", income,  Color(0xFF72BF85))
                    StatItem("Egresos", expenditures, Color.Red)
                }
            }
        }
    }
}


@Composable
fun StatItem(label: String, amount: Double, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color = color
            )
        )
        Text(
            text = "$${amount.format(2)}",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SalesList(
    viewModel: BusinessViewModel,
    modifier: Modifier,
) {
    val productsModel by viewModel.productsModel.observeAsState(emptyList())
    val invoiceList by viewModel.invoiceList.observeAsState(emptyList())
    Log.d("SalesList", "SalesList: $invoiceList")
    ElevatedCard(

        modifier = modifier.fillMaxWidth().background( color = Color(0xFF9B86BE)),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RectangleShape
    ) {
        LazyColumn {
            items(invoiceList) { invoice ->
                SaleCard(invoice, viewModel = viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SaleCard(invoice: Invoice, viewModel: BusinessViewModel) {
    var showEditDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

//    if (showEditDialog) {
//        EditSaleDialog(
//            viewModel = viewModel,
//            sale = product,
//            onDismiss = { showEditDialog = false })
//    }

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Fondo blanco para cada fila
    ) {
        // Línea superior morada
        Divider(color = Color(0xFF9B86BE), thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto del Producto
            Text(
                text = invoice.client,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color =  Color(0xFF363062),
                modifier = Modifier.weight(1.5f)
            )

            // Cantidad
            Text(
                text = "Borrar",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF363062),
                modifier = Modifier.weight(1f)
            )

            // Total
            Text(
                text = "${invoice.value.format(2)}",
                style = MaterialTheme.typography.bodyMedium,
                color =  Color(0xFF363062),
                modifier = Modifier.weight(1f)
            )
            // Botón de ver más
            IconButton(
                onClick = {
                    //manejar modal para ver detalles
                },
                modifier = Modifier.size(24.dp) // Tamaño del icono más pequeño
            ) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "Ver",
                    tint =  Color(0xFF72BF85), // Color verde similar al icono de la imagen
                    modifier = Modifier.size(24.dp)
                )
            }

//            // Botón de Editar
//            IconButton(
//                onClick = { showEditDialog = true },
//                modifier = Modifier.size(24.dp) // Tamaño del icono más pequeño
//            ) {
//                Icon(
//                    Icons.Filled.Edit,
//                    contentDescription = "Editar",
//                    tint =  Color(0xFF72BF85), // Color verde similar al icono de la imagen
//                    modifier = Modifier.size(24.dp)
//                )
//            }
//
//            // Botón de Eliminar
//            IconButton(
//                onClick = {
////                    coroutineScope.launch {
////                        viewModel.deleteSaleProduct(product.id).apply {
////                            Toast.makeText(
////                                context,
////                                "Eliminando Venta",
////                                Toast.LENGTH_SHORT
////                            ).show()
////                        }
////                    }
//                },
//                modifier = Modifier.size(24.dp)
//            ) {
//                Icon(
//                    Icons.Filled.Delete,
//                    contentDescription = "Eliminar",
//                    tint =  Color(0xFF72BF85),
//                    modifier = Modifier.size(24.dp)
//                )
//            }
        }
    }
}




//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun ProductSelectionDialog(
//    viewModel: BusinessViewModel,
//    onDismiss: () -> Unit,
//    onProductSelected: (product: Product, quantity: Int) -> Unit,
//    seller: String
//) {
//    val coroutineScope = rememberCoroutineScope()
//    var searchQuery by remember { mutableStateOf("") }
//    val filteredProducts by viewModel.filteredProducts.observeAsState(emptyList())
//    var selectedProduct by remember { mutableStateOf<Product?>(null) }
//    var quantity by remember { mutableIntStateOf(1) }
//    val context = LocalContext.current
//    val addState by viewModel.addState.observeAsState(initial = AddState.Loading)
//
//    LaunchedEffect(searchQuery) {
//        viewModel.filterProducts(searchQuery)
//    }
//
//    LaunchedEffect(addState) {
//        if (addState is AddState.Success) {
//            onDismiss()
//        }
//    }
//
//    AlertDialog(
//        onDismissRequest = { onDismiss() },
//        title = { Text("Seleccionar Producto", style = MaterialTheme.typography.titleMedium) },
//        text = {
//            Column {
//                TextField(
//                    value = searchQuery,
//                    onValueChange = { searchQuery = it },
//                    label = { Text("Buscar producto") },
//                    modifier = Modifier.fillMaxWidth(),
//                    singleLine = true,
//                    textStyle = MaterialTheme.typography.bodyLarge
//                )
//                LazyColumn(modifier = Modifier.height(200.dp)) {
//                    items(filteredProducts) { product ->
//                        if (product.stock > 0) {
//                            SaleRow(product, isSelected = selectedProduct?.id == product.id) {
//                                selectedProduct = product
//                                searchQuery = product.name
//                            }
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.height(8.dp))
//                QuantitySelector(
//                    quantity,
//                    onQuantityChange = { quantity = it },
//                    selectedProduct?.stock ?: 0
//                )
//                Divider(modifier = Modifier.padding(vertical = 8.dp))
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 16.dp),
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    Button(
//                        onClick = { onDismiss() },
//                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
//                    ) {
//                        Text("Cancelar", color = Color.White)
//                    }
//                    Spacer(Modifier.width(16.dp))
//                    Button(
//                        onClick = {
//                            selectedProduct?.let {
//                                onProductSelected(it, quantity)
//                                coroutineScope.launch {
//                                    viewModel.addSaleProduct(it, quantity, seller).apply {
//                                        Toast.makeText(
//                                            context,
//                                            "Agregando Venta",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                    viewModel.onAddSelected().apply {
//                                        when (addState) {
//                                            is AddState.Success -> {
//                                                Toast.makeText(
//                                                    context,
//                                                    (addState as AddState.Success).message,
//                                                    Toast.LENGTH_SHORT
//                                                ).show()
//                                            }
//
//                                            is AddState.Error -> {
//                                                Toast.makeText(
//                                                    context,
//                                                    (addState as AddState.Error).message,
//                                                    Toast.LENGTH_SHORT
//                                                ).show()
//                                            }
//
//                                            else -> Unit
//                                        }
//                                    }
//                                }
//                            }
//                        },
//                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
//                        enabled = selectedProduct != null && quantity <= (selectedProduct?.stock
//                            ?: 0)
//                    ) {
//                        Text("Agregar", color = Color.White)
//                    }
//                }
//            }
//        },
//        confirmButton = {},
//        dismissButton = {}
//    )
//}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PurchaseDialog(
    viewModel: BusinessViewModel,
    onDismiss: () -> Unit,
    onPurchaseAdded: (reason: String, amount: Double) -> Unit,
    store: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var reason by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val addState by viewModel.addState.observeAsState(initial = AddState.Loading)

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Agregar Egreso", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                TextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Razón") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = {
                            if (reason.isNotBlank() && amount.isNotBlank()) {
                                onPurchaseAdded(reason, amount.toDouble())
                                coroutineScope.launch {
                                    viewModel.addPurchase(amount.toDouble(), reason, store).apply {
                                        Toast.makeText(
                                            context,
                                            "Agregando Egreso",
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
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Agregar", color = Color.White)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun SaleRow(product: Product, isSelected: Boolean, onClick: () -> Unit) {
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

@Composable
fun QuantitySelector(quantity: Int, onQuantityChange: (Int) -> Unit, maxQuantity: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) {
            Icon(Icons.Filled.Delete, "Disminuir")
        }
        Text(
            "$quantity",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Button(onClick = { if (quantity < maxQuantity) onQuantityChange(quantity + 1) }) {
            Icon(Icons.Filled.Add, "Aumentar")
        }
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun EditSaleDialog(
//    viewModel: BusinessViewModel,
//    sale: AuxiliarSaleProduct,
//    onDismiss: () -> Unit
//) {
//    val coroutineScope = rememberCoroutineScope()
//    var quantity by remember { mutableIntStateOf(sale.quantity) }
//    val context = LocalContext.current
//    val addState by viewModel.addState.observeAsState(initial = AddState.Loading)
//
//    LaunchedEffect(addState) {
//        if (addState is AddState.Success) {
//            onDismiss()
//        }
//    }
//
//    AlertDialog(
//        onDismissRequest = { onDismiss() },
//        title = { Text("Editar Venta", style = MaterialTheme.typography.titleMedium) },
//        text = {
//            Column {
//                Text("Producto: ${sale.productName}", style = MaterialTheme.typography.bodyLarge)
//                Spacer(modifier = Modifier.height(8.dp))
//                QuantitySelector(
//                    quantity,
//                    onQuantityChange = { quantity = it },
//                    maxQuantity = sale.productStock + sale.quantity
//                )
//                Divider(modifier = Modifier.padding(vertical = 8.dp))
//            }
//        },
//        confirmButton = {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Button(
//                    onClick = { onDismiss() },
//                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
//                ) {
//                    Text("Cancelar", color = Color.White)
//                }
//                Spacer(Modifier.width(16.dp))
//                Button(
//                    onClick = {
//                        if (quantity in 1..(sale.productStock + sale.quantity)) {
//                            val updatedSale = sale.copy(
//                                quantity = quantity,
//                                total = (quantity * sale.productPrice).toDouble()
//                            )
//                            coroutineScope.launch {
//                                viewModel.updateSaleProduct(updatedSale.toSale()).apply {
//                                    Toast.makeText(
//                                        context,
//                                        "Actualizando Venta",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                                viewModel.onAddSelected().apply {
//                                    when (addState) {
//                                        is AddState.Success -> {
//                                            Toast.makeText(
//                                                context,
//                                                (addState as AddState.Success).message,
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
//
//                                        is AddState.Error -> {
//                                            Toast.makeText(
//                                                context,
//                                                (addState as AddState.Error).message,
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
//
//                                        else -> Unit
//                                    }
//                                }
//                            }
//                        }
//                    },
//                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
//                    enabled = quantity in 1..(sale.productStock + sale.quantity)
//                ) {
//                    Text("Actualizar", color = Color.White)
//                }
//            }
//        },
//        dismissButton = {}
//    )
//}

private fun AuxiliarSaleProduct.toSale(): Sale {
    return Sale(
        id = this.id,
        created_at = this.created_at,
        total = this.total,
        product = this.product,
        quantity = this.quantity,
        id_business = this.id_business,
        seled_by = this.seled_by,
        state = "this.state",
        invoice_id = "this.invoice_id",
        price = this.productPrice
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PurchasesList(
    viewModel: BusinessViewModel,
    modifier: Modifier,
) {
    val purchasesList by viewModel.purchasesList.observeAsState(emptyList())
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(purchasesList) { purchase ->
            PurchaseCard(purchase, viewModel = viewModel)
        }
    }
}

@Composable
fun PurchaseCard(purchase: Purchase, viewModel: BusinessViewModel) {
    var showEditDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    if (showEditDialog) {
        EditPurchaseDialog(
            viewModel = viewModel,
            purchase = purchase,
            onDismiss = { showEditDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Fondo blanco para cada fila
    ) {
        // Línea superior morada
        Divider(color = Color(0xFF9B86BE), thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto de Razón
            Text(
                text = purchase.reason,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color =  Color(0xFF363062),
                modifier = Modifier.weight(1.5f)
            )

            // Valor
            Text(
                text = "${purchase.amount}",
                style = MaterialTheme.typography.bodyMedium,
                color =  Color(0xFF363062),
                modifier = Modifier.weight(1f)
            )

            // Botón de Editar
            IconButton(
                onClick = { showEditDialog = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint =  Color(0xFF72BF85), // Color verde similar al icono en SaleCard
                    modifier = Modifier.size(24.dp)
                )
            }

            // Botón de Eliminar
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.deletePurchase(purchase.id).apply {
                            Toast.makeText(
                                context,
                                "Eliminando Egreso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    viewModel.deletePurchase(purchase.id)
                }
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
            IconButton(
                onClick = {
                    showEditDialog = true
                }
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar")
            }
        }
    }
}

@Composable
fun EditPurchaseDialog(
    viewModel: BusinessViewModel,
    purchase: Purchase,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var reason by remember { mutableStateOf(purchase.reason) }
    var amount by remember { mutableStateOf(purchase.amount.toString()) }
    val context = LocalContext.current
    val addState by viewModel.addState.observeAsState(initial = AddState.Loading)

    LaunchedEffect(addState) {
        if (addState is AddState.Success) {
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Editar Egreso", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                TextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Razón") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cancelar", color = Color.White)
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (reason.isNotBlank() && amount.isNotBlank()) {
                            val updatedPurchase = purchase.copy(
                                reason = reason,
                                amount = amount.toDouble()
                            )
                            coroutineScope.launch {
                                viewModel.updatePurchase(updatedPurchase).apply {
                                    Toast.makeText(
                                        context,
                                        "Actualizando Egreso",
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
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Actualizar", color = Color.White)
                }
            }
        },
        dismissButton = {}
    )
}


class FakeBusinessViewModel(context: Context) : BusinessViewModel(context) {
    override val store: LiveData<Store> = MutableLiveData(Store("1", "Tienda de Prueba", "Ubicación", "Descripción"))
    override val income: LiveData<Double> = MutableLiveData(1000.0)
    override val expenditures: LiveData<Double> = MutableLiveData(500.0)
    override val productsModel: LiveData<List<AuxiliarSaleProduct>> = MutableLiveData(
        listOf(
            AuxiliarSaleProduct(id = "Producto A", total = 10.0, product = 100.0.toString(), productName = "1", quantity = 5),
            AuxiliarSaleProduct(id = "Producto A", total = 10.0, product = 100.0.toString(), productName = "1", quantity = 5),
            AuxiliarSaleProduct(id = "Producto A", total = 10.0, product = 100.0.toString(), productName = "1", quantity = 5),
            AuxiliarSaleProduct(id = "Producto A", total = 10.0, product = 100.0.toString(), productName = "1", quantity = 5),
            AuxiliarSaleProduct(id = "Producto A", total = 10.0, product = 100.0.toString(), productName = "1", quantity = 5),
            AuxiliarSaleProduct(id = "Producto A", total = 10.0, product = 100.0.toString(), productName = "1", quantity = 5),
            AuxiliarSaleProduct(id = "Producto A", total = 10.0, product = 100.0.toString(), productName = "1", quantity = 5),


        )
    )
    override val purchasesList: LiveData<List<Purchase>> = MutableLiveData(
        listOf(
            Purchase(
                id = "1",
                reason = "Razón A",
                created_at = "2023-01-01", // Ejemplo de fecha
                business_id = "business_id_123", // ID del negocio
                amount = 30.0 // Cantidad del egreso
            ),
            Purchase(
                id = "2",
                reason = "Razón B",
                created_at = "2023-01-02",
                business_id = "business_id_456",
                amount = 20.0
            )
        )
    )

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun BusinessScreenPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val fakeViewModel = FakeBusinessViewModel(context)

    BusinessScreen(
        viewModel = fakeViewModel,
        navController = navController,
        store = "1",
        seller = "seller"
    )
}
