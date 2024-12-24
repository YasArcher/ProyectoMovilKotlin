package ec.yasuodev.proyecto_movil.ui.core.products

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel,
    navController: NavController,
    store: String
) {
    // ÚNICA llamada a fetchCategories (evita duplicados)
    LaunchedEffect(Unit) {
        Log.d("AddProductScreen", "LaunchedEffect: fetchCategories()")
        viewModel.fetchCategories()
    }

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
            AddContent(viewModel, navController, store)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<ProductCategory>,
    selectedCategoryName: String,
    onCategorySelected: (ProductCategory) -> Unit
) {
    // Controla si está abierto o cerrado el menú
    var expanded by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .focusRequester(focusRequester)
            .zIndex(2f) // Para evitar que quede detrás de algo
    ) {
        TextField(
            value = selectedCategoryName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = !expanded
                }
                .focusRequester(focusRequester)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { cat ->
                DropdownMenuItem(
                    text = {
                        Text(cat.category_name, color = Color.Black)
                    },
                    onClick = {
                        onCategorySelected(cat)
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
    navController: NavController,
    store: String
) {
    val context = LocalContext.current

    val addState by viewModel.addState.observeAsState(initial = AddState.Loading)
    val isLoading by viewModel.isLoading.observeAsState(initial = false)

    // Observa tus campos del ViewModel
    val name by viewModel.name.observeAsState("")
    val price by viewModel.price.observeAsState("")
    val stock by viewModel.stock.observeAsState("")
    val category by viewModel.category.observeAsState("")

    val editEnable by viewModel.editEnable.observeAsState(false)

    // Observa las categorías (importante: ver si llega > 0)
    val categories by viewModel.categories.observeAsState(emptyList())
    Log.d("AddContent", "Observe categories => size: ${categories.size}")

    // Estado local para mostrar el nombre de la categoría seleccionada
    var selectedCategoryName by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = viewModel) {
        // Si no hay token, navega al login
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        }
        // Setea la tienda en el ViewModel
        viewModel.store = store
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // ----------- CAMPOS: nombre, precio, stock ----------- //
                DynamicField(
                    value = name,
                    onTextFieldChange = { newValue ->
                        viewModel.onAddChanged(
                            newValue,
                            price,
                            stock,
                            category
                        )
                    },
                    tipo = 2,
                )
                Spacer(modifier = Modifier.padding(8.dp))
                DynamicText(
                    message = viewModel.onEditMessageName(name),
                    state = viewModel.isValidName(name)
                )

                Spacer(modifier = Modifier.padding(8.dp))
                DynamicField(
                    value = price,
                    onTextFieldChange = { newValue ->
                        viewModel.onAddChanged(
                            name,
                            newValue,
                            stock,
                            category
                        )
                    },
                    tipo = 5,
                )
                Spacer(modifier = Modifier.padding(8.dp))
                DynamicText(
                    message = viewModel.onEditMessagePrice(price),
                    state = viewModel.isValidPrice(price)
                )

                Spacer(modifier = Modifier.padding(8.dp))
                DynamicField(
                    value = stock,
                    onTextFieldChange = { newValue ->
                        viewModel.onAddChanged(
                            name,
                            price,
                            newValue,
                            category
                        )
                    },
                    tipo = 5,
                )
                Spacer(modifier = Modifier.padding(8.dp))
                DynamicText(
                    message = viewModel.onEditMessageStock(stock),
                    state = viewModel.isValidStock(stock)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                // --------------- COMBO PARA CATEGORÍAS --------------- //
                Spacer(modifier = Modifier.height(8.dp))
                // Imprime cuántas categorías hay
                Log.d("AddContent", "CategoryDropdown => categories.size = ${categories.size}")

                CategoryDropdown(
                    categories = categories,
                    selectedCategoryName = selectedCategoryName,
                    onCategorySelected = { cat ->
                        selectedCategoryName = cat.category_name
                        // Llamamos a la misma onAddChanged, pero pasando cat.id (UUID)
                        viewModel.onAddChanged(
                            name,
                            price,
                            stock,
                            cat.id
                        )
                    }
                )

                Spacer(modifier = Modifier.padding(16.dp))

                // --------------- BOTÓN "Agregar" --------------- //
                DynamicButton(
                    type = 1,
                    text = "Agregar",
                    enable = editEnable,
                    method = {
                        coroutineScope.launch {
                            viewModel.addProduct().apply {
                                Toast.makeText(context, "Agregando Producto", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            viewModel.onAddSelected().apply {
                                when (addState) {
                                    is AddState.Success -> {
                                        Toast.makeText(
                                            context,
                                            (addState as AddState.Success).message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("products")
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
                )
            }
        }
    }
}
