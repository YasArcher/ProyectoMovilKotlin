package ec.yasuodev.proyecto_movil.ui.core.home

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.shared.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        }
        viewModel.fetchToken(context)
        viewModel.fetchStore()
    }
    val user by viewModel.user.observeAsState(User("", "", "", "", "", ""))
    val showDialog = remember { mutableStateOf(false) } // Estado para mostrar el diálogo
    val newStoreName = remember { mutableStateOf("") } // Estado para almacenar el nombre de la nueva tienda
    val addState by viewModel.addState.observeAsState(AddState.Loading) // Observamos el estado de la adición
    val owner: String by viewModel.owner.observeAsState(initial = "")
    val business_image: String by viewModel.business_image.observeAsState(initial = "")
    val userId: String = TokenManager.getToken(context).toString() // Método para obtener el ID del usuario


    // Escuchar cambios en el estado de la adición
    LaunchedEffect(addState) {
        when (addState) {
            is AddState.Success -> {
                // Mostrar mensaje de éxito (puedes usar un Toast o Snackbar)
                Toast.makeText(context, "Tienda agregada", Toast.LENGTH_SHORT).show()
            }
            is AddState.Error -> {
                // Mostrar mensaje de error (puedes usar un Toast o Snackbar)
                Toast.makeText(context, "Error al agregar la tienda", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bienvenido ${user.nickname}",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog.value = true }, // Al hacer clic, mostramos el cuadro de texto
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir tienda")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HomeContent(viewModel, navController, user)
            // Mostrar el diálogo cuando showDialog es verdadero
            if (showDialog.value) {
                AddStoreDialog(
                    onDismiss = { showDialog.value = false },
                    onAddStore = { name ->
                        // Llamar a la función para añadir la tienda
                        viewModel.addStore(name, context)
                        // Cerrar el diálogo después de intentar agregar la tienda
                        showDialog.value = false
                    },
                    storeName = newStoreName.value,
                    onNameChange = { newStoreName.value = it }
                )
            }
        }
    }
}

@Composable
fun AddStoreDialog(
    onDismiss: () -> Unit,
    onAddStore: (String) -> Unit,
    storeName: String,
    onNameChange: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Añadir Tienda", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = storeName,
                    onValueChange = onNameChange,
                    label = { Text("Nombre de la tienda") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    TextButton(onClick = {
                        onAddStore(storeName)
                    }) {
                        Text("Añadir")
                    }
                }
            }
        }
    }
}


@Composable
fun HomeContent(viewModel: HomeViewModel, navController: NavController, user: User) {
    val store by viewModel.store.observeAsState(Store("", "", "", ""))
    val storeList by viewModel.storeList.observeAsState(listOf())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Mis Negocios", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(16.dp))
        store.let {
            DefaultCard(it, navController, user, 1)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(text = "Otros Negocios", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(16.dp))
        storeList.forEach {
            DefaultCard(it, navController, user, 2)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DefaultCard(store: Store, navController: NavController, user: User, tipo : Int) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("business/${store.id}/${user.id}")
            }
            .padding(8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (tipo == 1) "Preciona aqui para ver tus ventas" else "Preciona aqui para vender",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Image(
                painter = if (tipo == 1) painterResource(id = R.drawable.shop_open_online_store_svgrepo_com)else painterResource(id = R.drawable.seller_in_shop_person_offer_sell_svgrepo_com),
                contentDescription = "Icono de tienda",
                modifier = Modifier.size(40.dp)
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir a ventas",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
