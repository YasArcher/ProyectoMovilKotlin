package ec.yasuodev.proyecto_movil.ui.core.manager

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerScreen(viewModel: ManagerViewModel, navController: NavController) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val userID by viewModel.userID.observeAsState("")
    val storeID by viewModel.storeID.observeAsState("")

    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        }
        viewModel.fetchUser(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis vendedores",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Manager")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ManagerContent(viewModel, navController, storeID)
        }
    }

    if (showDialog) {
        AddManagerDialog(
            viewModel = viewModel,
            storeID = storeID,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun AddManagerDialog(
    viewModel: ManagerViewModel,
    storeID: String,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val managerList by viewModel.managerList.observeAsState(listOf())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Agregar Vendedor") },
        text = {
            Column {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (managerList.none { it.email == email }) {
                        coroutineScope.launch {
                            viewModel.addManagerByEmail(storeID, email)
                        }
                    }
                    onDismiss()
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun ManagerContent(viewModel: ManagerViewModel, navController: NavController, storeID: String) {
    val managerList by viewModel.managerList.observeAsState(listOf())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        managerList.forEach { manager ->
            item {
                ManagerCard(manager = manager, onDeleteClick = {
                    coroutineScope.launch {
                        viewModel.deleteManager(manager.id, storeID).apply {
                            Toast.makeText(context, "Vendedor eliminado", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }
}

@Composable
fun ManagerCard(manager: User, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            ImageManager(
                modifier = Modifier.size(50.dp),
                selectedImageUri = Uri.EMPTY,
                image = manager.image ?: "noImage",
                onImageClick = {}
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = manager.name, style = MaterialTheme.typography.titleMedium)
                Text(text = manager.email, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onDeleteClick() }) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar Manager")
            }
        }
    }
}

@Composable
fun ImageManager(
    modifier: Modifier,
    selectedImageUri: Uri,
    image: String,
    onImageClick: () -> Unit
) {
    Box(modifier = modifier.clickable { onImageClick() }, contentAlignment = Alignment.Center) {
        if (selectedImageUri != Uri.EMPTY) {
            val painter = rememberAsyncImagePainter(selectedImageUri)
            Image(
                painter = painter,
                contentDescription = "Header Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        } else if (image != "noImage") {
            val url =
                "https://vgnnieizrwmjemlnziaj.supabase.co/storage/v1/object/public/storeApp/users/$image.jpg"
            val painter = rememberAsyncImagePainter(url)
            Image(
                painter = painter,
                contentDescription = "Header Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.seller_svgrepo_com),
                contentDescription = "Header Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        }
    }
}
