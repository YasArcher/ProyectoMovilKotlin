package ec.yasuodev.proyecto_movil.ui.core.business

import android.os.Build
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.shared.models.AuxiliarSaleProduct
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import kotlinx.coroutines.launch

enum class TransactionType {
    SALE, PURCHASE
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BusinessScreen(viewModel: BusinessViewModel, navController: NavController, store: String) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BusinessContent(viewModel, navController, Modifier, store)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BusinessContent(
    viewModel: BusinessViewModel,
    navController: NavController,
    modifier: Modifier,
    storeID: String
) {
    val context = LocalContext.current
    val store by viewModel.store.observeAsState(Store("", "", "", ""))
    val products by viewModel.products.observeAsState(emptyList())
    val showDialog by viewModel.showDialog.observeAsState(false)
    var expanded by remember { mutableStateOf(false) }
    var transactionType by remember { mutableStateOf(TransactionType.SALE) }

    LaunchedEffect(key1 = viewModel) {
        val stid = "126b4bc6-bdea-4542-a3c8-0d75ed75d887"
        val dateToday = java.time.LocalDate.now().toString()
        viewModel.fetchBusiness(stid)
        viewModel.getSalesByDate(stid, dateToday)
        viewModel.getExpendituresByDate(stid, dateToday)
    }

    Box(modifier.fillMaxSize()) {
        Column(modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Bienvenido a ${store.name}",
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(15.dp),
            )
            StatsCard(modifier, viewModel)
            Spacer(modifier = Modifier.padding(10.dp))
            Column(modifier) {
                Text(text = "Productos vendidos hoy", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.padding(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "Producto",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1.3f)
                    )
                    Text(
                        text = "Cantidad",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1.4f)
                    )
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.padding(10.dp))
                SalesList(viewModel = viewModel, modifier = modifier)
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
                    transactionType = TransactionType.SALE
                    viewModel.showDialog(true)
                    expanded = false
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
                ProductSelectionDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showDialog(false) },
                    onProductSelected = { product, quantity ->
                        viewModel.showDialog(false)
                    }
                )
            }
            TransactionType.PURCHASE -> {
                PurchaseDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showDialog(false) },
                    onPurchaseAdded = { reason, amount ->
                        viewModel.showDialog(false)
                    }
                )
            }
        }
    }
}

@Composable
fun StatsCard(modifier: Modifier, viewModel: BusinessViewModel) {
    val income by viewModel.income.observeAsState(0.0)
    val expenditures by viewModel.expenditures.observeAsState(0.0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp)
                .clip(RoundedCornerShape(8.dp))
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
                    StatItem("Ingresos", income, Color(0xFF388E3C))  // Verde para ingresos
                    StatItem("Egresos", expenditures, Color.Red)     // Rojo para egresos
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

@Composable
fun SalesList(
    viewModel: BusinessViewModel,
    modifier: Modifier,
) {
    val productsModel by viewModel.productsModel.observeAsState(emptyList())
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        LazyColumn {
            items(productsModel) { product ->
                SaleCard(product, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SaleCard(product: AuxiliarSaleProduct, viewModel: BusinessViewModel) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.productName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1.5f)
            )
            Text(
                text = "${product.quantity}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${product.total.format(2)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ProductSelectionDialog(
    viewModel: BusinessViewModel,
    onDismiss: () -> Unit,
    onProductSelected: (product: Product, quantity: Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    val filteredProducts by viewModel.filteredProducts.observeAsState(emptyList())
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableIntStateOf(1) }

    LaunchedEffect(searchQuery) {
        viewModel.filterProducts(searchQuery)
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Seleccionar Producto", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar producto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(filteredProducts) { product ->
                        ProductRow(product, isSelected = selectedProduct?.id == product.id) {
                            selectedProduct = product
                            searchQuery = product.name
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                QuantitySelector(quantity, onQuantityChange = { quantity = it })
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
                            selectedProduct?.let {
                                onProductSelected(it, quantity)
                                coroutineScope.launch {
                                    viewModel.addSaleProduct(it, quantity)
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
fun PurchaseDialog(
    viewModel: BusinessViewModel,
    onDismiss: () -> Unit,
    onPurchaseAdded: (reason: String, amount: Double) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var reason by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

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
                    textStyle = MaterialTheme.typography.bodyLarge
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
                                    //viewModel.addPurchase(reason, amount.toDouble())
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

@Composable
fun QuantitySelector(quantity: Int, onQuantityChange: (Int) -> Unit) {
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
        Button(onClick = { onQuantityChange(quantity + 1) }) {
            Icon(Icons.Filled.Add, "Aumentar")
        }
    }
}