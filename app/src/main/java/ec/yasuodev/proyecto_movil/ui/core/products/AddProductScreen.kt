package ec.yasuodev.proyecto_movil.ui.core.products

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicButton
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicField
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicText
import ec.yasuodev.proyecto_movil.ui.shared.models.ProductCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel,
    navController: NavController,
    store: String
) {
    Log.d("AddProductScreen", "store: $store")
    var showLoading by remember { mutableStateOf(true) } // Estado local para controlar el indicador de carga
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
            return@LaunchedEffect
        }

        // Retraso estático de 4000 ms antes de mostrar el contenido
        // Configura la tienda y carga categorías
        if (viewModel.store.value != store) {
            viewModel.store.value = store
        }
        showLoading = false // Oculta el indicador de carga después del retraso
    }

    // Muestra el indicador de carga o el contenido
    if (showLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Agregar Producto",
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AddContent(viewModel, navController)
            }
        }
    }
}

@Composable
fun CategoryDropdown(
    categories: List<ProductCategory>,
    selectedCategoryName: String,
    onCategorySelected: (ProductCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = if (selectedCategoryName.isEmpty()) "Selecciona una categoría" else selectedCategoryName,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = category.category_name,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
fun AddContent(
    viewModel: AddProductViewModel,
    navController: NavController
) {
    val categories = viewModel.staticCategories
    val name by viewModel.name.observeAsState("")
    val price by viewModel.price.observeAsState("")
    val stock by viewModel.stock.observeAsState("")
    val category by viewModel.category.observeAsState("")
    val editEnable by viewModel.editEnable.observeAsState(false)
    var selectedCategoryName by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Agregar Producto",
                style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DynamicField(
                value = name,
                onTextFieldChange = { newValue ->
                    viewModel.onAddChanged(newValue, price, stock, category)
                },
                tipo = 2,
            )
            Spacer(modifier = Modifier.padding(8.dp))

            DynamicField(
                value = price,
                onTextFieldChange = { newValue ->
                    viewModel.onAddChanged(name, newValue, stock, category)
                },
                tipo = 5,
            )
            Spacer(modifier = Modifier.padding(8.dp))

            DynamicField(
                value = stock,
                onTextFieldChange = { newValue ->
                    viewModel.onAddChanged(name, price, newValue, category)
                },
                tipo = 6,
            )
            Spacer(modifier = Modifier.padding(8.dp))

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Categoría",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(start = 8.dp)
            )
            CategoryDropdown(
                categories = categories,
                selectedCategoryName = selectedCategoryName,
                onCategorySelected = { cat ->
                    selectedCategoryName = cat.category_name
                    viewModel.onAddChanged(name, price, stock, cat.id)
                }
            )

            Spacer(modifier = Modifier.padding(16.dp))
            DynamicButton(
                type = 1,
                text = "Agregar",
                enable = editEnable,
                method = {
                    coroutineScope.launch {
                        viewModel.addProduct()
                        Toast.makeText(context, "Producto agregado correctamente", Toast.LENGTH_SHORT).show()
                        navController.navigate("products")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 12.dp)
            )
        }
    }
}