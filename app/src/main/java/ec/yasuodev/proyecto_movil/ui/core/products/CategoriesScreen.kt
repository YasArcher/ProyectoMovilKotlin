package ec.yasuodev.proyecto_movil.ui.core.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.core.business.SalesViewModel
import ec.yasuodev.proyecto_movil.ui.shared.models.ProductCategory

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel,
    store: String
) {
    val store: String by viewModel.store.observeAsState(store)
    val categories: List<ProductCategory> by viewModel.categories.observeAsState(emptyList())
    LaunchedEffect(key1 = viewModel) {
        viewModel.fetchCategories()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF9B59B6)) // Fondo morado
    ) {
        Column {
            // Encabezado
            CategoriesHeader()

            // Lista de categorías
            CategoriesList(categories)

            // Botón Aceptar
            AcceptButton(onClick = {
                // Acción al aceptar
            })
        }
    }
}

@Composable
fun CategoriesHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .weight(1.3f)
                .background(
                    color = Color(0xFF363062),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 8.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Categoría",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .weight(1.3f)
                .background(
                    color = Color(0xFF363062),
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
}

@Composable
fun CategoriesList(categories: List<ProductCategory>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .padding(16.dp)
    ) {
        // Encabezado de la lista
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Categoría",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3C1F62) // Morado oscuro
            )
            Text(
                text = "Acciones",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3C1F62)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Lista de categorías
        LazyColumn {
            items(categories) { category ->
                CategoryRow(category, onEdit = {
                    // Acción de editar
                }, onDelete = {
                    // Acción de eliminar
                })
            }
        }
    }
}

@Composable
fun CategoryRow(category: ProductCategory, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFF3C1F62), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.category_name,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onEdit() }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint = Color(0xFFD8A3D9) // Rosa claro
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = { onDelete() }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFD8A3D9)
                )
            }
        }
    }
}

@Composable
fun AcceptButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A0097)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Aceptar",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
