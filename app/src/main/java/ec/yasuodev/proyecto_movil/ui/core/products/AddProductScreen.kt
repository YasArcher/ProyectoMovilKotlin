package ec.yasuodev.proyecto_movil.ui.core.products

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicButton
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicField
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(viewModel: AddProductViewModel, navController: NavController, store: String) {
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

@Composable
fun AddContent(
    viewModel: AddProductViewModel,
    navController: NavController,
    store: String
) {
    val context = LocalContext.current
    val addState by viewModel.addState.observeAsState(initial = AddState.Loading)
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val name: String by viewModel.name.observeAsState(initial = "")
    val price: String by viewModel.price.observeAsState(initial = "")
    val stock: String by viewModel.stock.observeAsState(initial = "")
    val editEnable: Boolean by viewModel.editEnable.observeAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        }
        viewModel.store = store
    }
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Surface(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                DynamicField(
                    value = name,
                    onTextFieldChange = { newValue ->
                        viewModel.onAddChanged(
                            newValue,
                            price,
                            stock
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
                            stock
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
                            newValue
                        )
                    },
                    tipo = 5,
                )
                Spacer(modifier = Modifier.padding(8.dp))
                DynamicText(
                    message = viewModel.onEditMessageStock(stock),
                    state = viewModel.isValidStock(stock)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                DynamicButton(type = 1, text = "Agregar", enable = editEnable, method ={
                    coroutineScope.launch {
                        viewModel.addProduct().apply {
                            Toast.makeText(context, "Agregando Producto", Toast.LENGTH_SHORT).show()
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
                })
            }
        }
    }
}