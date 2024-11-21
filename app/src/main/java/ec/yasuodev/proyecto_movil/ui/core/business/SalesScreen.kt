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
    // Estado para controlar la visibilidad del modal
    var showModal by remember { mutableStateOf(false) }
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
            // Cambiar AddButton para activar el modal
            AddButton(onClick = { showModal = true })
            TotalCard(total)
        }

        // Tabla de productos
        Spacer(modifier = Modifier.height(16.dp))
        ProductsTable(products)

        Spacer(modifier = Modifier.weight(1f))
    }

    // Mostrar modal si `showModal` es true
    if (showModal) {
        StyledAddModal(
            onDismiss = { showModal = false },
            onConfirm = { productName, quantity, value ->
                selectedProductName = productName
                selectedQuantity = quantity
                selectedValue = value
                showModal = false

                // Aquí puedes llamar al ViewModel para agregar el producto
                println("Producto: $productName, Cantidad: $quantity, Valor: $value")
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
fun ProductsTable(products: List<Product>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        // Encabezado de la tabla
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

        // Filas de productos en un LazyColumn
        LazyColumn(
            modifier = Modifier.weight(1f) // Ocupa el espacio restante
        ) {
            items(products) { product ->
                ProductRow(product)
            }
        }

        // Botón Aceptar fijo al final
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            AcceptButton {
                // Acción para aceptar la venta
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
fun ProductRow(product: Product) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp) // Espaciado alrededor de cada fila
            .background(Color(0xFF3C1F62), shape = RoundedCornerShape(8.dp)) // Fondo morado con bordes redondeados
            .padding(horizontal = 12.dp, vertical = 8.dp), // Padding interno
        verticalAlignment = Alignment.CenterVertically // Alinear el contenido verticalmente al centro
    ) {
        Text(
            text = product.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp), // Ajuste para alineación uniforme
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${product.quantity}",
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp), // Ajuste para alineación uniforme
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "%.2f".format(product.total),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp), // Ajuste para alineación uniforme
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly, // Distribuir equitativamente los botones
            verticalAlignment = Alignment.CenterVertically // Alineación vertical para los íconos
        ) {
            // Botón de Editar con fondo circular
            Box(
                modifier = Modifier
                    .size(30.dp) // Tamaño del círculo
                    .background(Color(0xFFD8A3D9), shape = RoundedCornerShape(50)) // Fondo morado claro con forma circular
                    .padding(4.dp), // Padding interno del círculo
                contentAlignment = Alignment.Center // Alinear el ícono al centro
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint = Color(0xFF3C1F62) // Ícono en blanco
                )
            }

            // Botón de Eliminar con fondo circular
            Box(
                modifier = Modifier
                    .size(30.dp) // Tamaño del círculo
                    .background(Color(0xFFD8A3D9), shape = RoundedCornerShape(50)) // Fondo rosa claro con forma circular
                    .padding(4.dp), // Padding interno del círculo
                contentAlignment = Alignment.Center // Alinear el ícono al centro
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFF3C1F62) // Ícono en blanco
                )
            }
        }
    }
}

@Composable
fun AcceptButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A0097))
    ) {
        Text("Aceptar", color = Color.White)
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
    onDismiss: () -> Unit,
    onConfirm: (productName: String, quantity: Int, value: Double) -> Unit,

) {
    var productName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Agregar",
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
                    // Campo de búsqueda de productos
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("Productos") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = Color(0xFF7A0097)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Nombre del Producto
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("Nombre del Producto") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Cantidad
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Cantidad") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Valor
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = { Text("Valor") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedQuantity = quantity.toIntOrNull() ?: 0
                    val parsedValue = value.toDoubleOrNull() ?: 0.0
                    onConfirm(productName, parsedQuantity, parsedValue)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A005A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar", color = Color.White)
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}


data class Product(val name: String, val quantity: Int, val total: Double)