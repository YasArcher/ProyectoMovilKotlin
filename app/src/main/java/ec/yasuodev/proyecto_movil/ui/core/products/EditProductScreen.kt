package ec.yasuodev.proyecto_movil.ui.core.products

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.core.models.EditState
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicButton
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicField
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicText
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import kotlinx.coroutines.launch

@Composable
fun EditProductScreen(
    viewModel: EditProductViewModel,
    navController: NavController,
    product: Product
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Edit(Modifier.fillMaxSize(), viewModel, navController, product)
    }
}

@Composable
fun Edit(
    modifier: Modifier,
    viewModel: EditProductViewModel,
    navController: NavController,
    product: Product
) {
    val context = LocalContext.current
    val editState by viewModel.editState.observeAsState(initial = EditState.Loading)
    val name: String by viewModel.name.observeAsState(initial = product.name)
    val price: String by viewModel.price.observeAsState(initial = product.price.toString())
    val stock: String by viewModel.stock.observeAsState(initial = product.stock.toString())
    val editEnable: Boolean by viewModel.editEnable.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        }
        viewModel.id = product.id.toString()
    }
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                DynamicField(
                    value = name,
                    onTextFieldChange = { newValue ->
                        viewModel.onEditChanged(
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
                        viewModel.onEditChanged(
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
                        viewModel.onEditChanged(
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
                DynamicButton(type = 1, text = "Editar", enable = editEnable,{
                    coroutineScope.launch {
                        viewModel.updateProduct().apply {
                            Toast.makeText(context, "Actualizando producto", Toast.LENGTH_SHORT).show()
                        }
                        viewModel.onEditSelected().apply {
                            when (editState) {
                                is EditState.Success -> {
                                    Toast.makeText(
                                        context,
                                        (editState as EditState.Success).message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("products")
                                }
                                is EditState.Error -> {
                                    Toast.makeText(
                                        context,
                                        (editState as EditState.Error).message,
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
