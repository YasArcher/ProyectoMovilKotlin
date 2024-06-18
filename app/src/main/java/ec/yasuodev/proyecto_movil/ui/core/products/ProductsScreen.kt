package ec.yasuodev.proyecto_movil.ui.core.products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.Store

@Composable
fun ProductsScreen(viewModel: ProductsViewModel, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Products(Modifier.fillMaxSize(), viewModel, navController)
    }
}

@Composable
fun Products(modifier: Modifier, viewModel: ProductsViewModel, navController: NavController) {
    val context = LocalContext.current
    val store: Store by viewModel.store.observeAsState(initial = Store("", "", ""))
    val products: List<Product> by viewModel.products.observeAsState(initial = emptyList())

    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        }
        viewModel.fetchStore(context)
    }
    Column {
        Row(Modifier.padding(bottom = 8.dp)) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                Text(text = "Nombre")
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                Text(text = "  Precio")
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                Text(text = "  Stock")
            }
            Column(Modifier.weight(1f)) {
                IconButton(onClick = { addProduct(navController, store.id) }) {
                    Icon(Icons.Default.Add, contentDescription = "add")
                }
            }
        }
        LazyColumn {
            items(products) { product ->
                ProductCard(product = product, viewModel = viewModel, navController = navController)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, viewModel: ProductsViewModel, navController: NavController) {
    Card(Modifier.padding(vertical = 8.dp, horizontal = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1.3f)) {
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = product.name)
            }
            Column(Modifier.weight(1f)) {
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = " " + product.price.toString() + " $")
            }
            Column(Modifier.weight(1f)) {
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = "      " + product.stock.toString())
            }
            IconButton(
                onClick = { product.id?.let { viewModel.deleteProduct(it) } },
                modifier = Modifier
            ) {
                Icon(Icons.Default.Delete, contentDescription = "delete")
            }
            IconButton(onClick = { editProduct(product, navController) }, modifier = Modifier) {
                Icon(Icons.Default.Edit, contentDescription = "edit")
            }
        }
    }
}

fun editProduct(product: Product, navController: NavController) {
    navController.navigate("editProduct/${product.id}/${product.name}/${product.store}/${product.price}/${product.stock}")
}

fun addProduct(navController: NavController, store: String) {
    navController.navigate("addProducts/${store}")
}