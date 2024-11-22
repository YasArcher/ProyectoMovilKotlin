package ec.yasuodev.proyecto_movil.ui.core.business

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController



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
    }
}

@Composable
fun SalesContent(
    viewModel: SalesViewModel,
    navController: NavController,
    modifier: Modifier,
    store: String,
    seller: String
) {
    // Estados para controlar el modal
    var showModal by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var selectedProductName by remember { mutableStateOf("") }
    var selectedQuantity by remember { mutableStateOf(0) }
    var selectedValue by remember { mutableStateOf(0.0) }

    // Ejemplo de productos (puedes usar datos reales del ViewModel si están disponibles)
    val products = listOf(
        Product("Pan", 8, 0.96),
        Product("Leche", 4, 1.20)
    )
    val total = products.sumOf { it.total }

    Column(modifier.fillMaxSize()) {
        // Encabezado
        HeaderSection(title = "Vender", total = total)

        Row(modifier.padding(horizontal = 16.dp)) {
            // Botón para abrir el modal en modo agregar
            AddButton(onClick = {
                isEditing = false // Modo agregar
                selectedProductName = ""
                selectedQuantity = 0
                selectedValue = 0.0
                showModal = true
            })
            TotalCard(total)
        }

        // Tabla de productos
        Spacer(modifier = Modifier.height(16.dp))
        ProductsTable(
            products = products,
            onEditClick = { product ->
                isEditing = true // Modo edición
                selectedProductName = product.name
                selectedQuantity = product.quantity
                selectedValue = product.total
                showModal = true
            },
            onDeleteClick = { product ->
                // Aquí puedes manejar la eliminación del producto
                println("Eliminar producto: ${product.name}")
            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }

    // Mostrar el modal si `showModal` es true
    if (showModal) {
        StyledAddModal(
            isEditing = isEditing,
            productName = selectedProductName,
            quantity = selectedQuantity,
            value = selectedValue,
            onDismiss = { showModal = false },
            onConfirm = { productName, quantity, value ->
                if (isEditing) {
                    // Lógica para guardar cambios al editar
                    println("Producto actualizado: $productName, Cantidad: $quantity, Valor: $value")
                } else {
                    // Lógica para agregar un producto nuevo
                    println("Producto agregado: $productName, Cantidad: $quantity, Valor: $value")
                }
                showModal = false
            }
        )
    }
}




@Composable
fun HeaderSection(title: String, total: Double) {
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

@Composable
fun ProductsTable(products: List<Product>, onEditClick: (Product) -> Unit, onDeleteClick: (Product) -> Unit) {
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
            items(products) { product ->
                ProductRow(
                    product = product,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick
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

@Composable
fun ProductRow(product: Product, onEditClick: (Product) -> Unit, onDeleteClick: (Product) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .background(Color(0xFF3C1F62), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = product.name,
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
            IconButton(onClick = { onEditClick(product) }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint = Color.White
                )
            }

            IconButton(onClick = { onDeleteClick(product) }) {
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

@Composable
fun StyledAddModal(
    isEditing: Boolean, // Nuevo parámetro para determinar si estamos editando
    productName: String = "", // Nombre del producto para edición
    quantity: Int = 0, // Cantidad del producto para edición
    value: Double = 0.0, // Valor del producto para edición
    onDismiss: () -> Unit,
    onConfirm: (productName: String, quantity: Int, value: Double) -> Unit
) {
    // Estados inicializados con los valores proporcionados
    var editableProductName by remember { mutableStateOf(productName) }
    var editableQuantity by remember { mutableStateOf(quantity.toString()) }
    var editableValue by remember { mutableStateOf(value.toString()) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = if (isEditing) "Editar Producto" else "Agregar Producto", // Cambia el título dinámicamente
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5A005A)
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
                    // Campo de nombre del producto
                    OutlinedTextField(
                        value = editableProductName,
                        onValueChange = { editableProductName = it },
                        label = { Text("Nombre del Producto") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo de cantidad
                    OutlinedTextField(
                        value = editableQuantity,
                        onValueChange = { editableQuantity = it },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo de valor
                    OutlinedTextField(
                        value = editableValue,
                        onValueChange = { editableValue = it },
                        label = { Text("Valor") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedQuantity = editableQuantity.toIntOrNull() ?: 0
                    val parsedValue = editableValue.toDoubleOrNull() ?: 0.0
                    onConfirm(editableProductName, parsedQuantity, parsedValue) // Llamada a onConfirm
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A005A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Guardar Cambios" else "Agregar", color = Color.White) // Botón dinámico
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}



data class Product(val name: String, val quantity: Int, val total: Double)