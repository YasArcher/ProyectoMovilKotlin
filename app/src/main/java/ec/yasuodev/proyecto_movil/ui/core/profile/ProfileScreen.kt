package ec.yasuodev.proyecto_movil.ui.core.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicButton
import ec.yasuodev.proyecto_movil.ui.shared.models.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Cuenta")
                },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Profile(Modifier.align(Alignment.Center), viewModel, navController)
        }
    }
}

@Composable
fun Profile(modifier: Modifier, viewModel: ProfileViewModel, navController: NavController) {
    val context = LocalContext.current
    val user by viewModel.user.observeAsState(User("", "", "", "", "", ""))
    val isLoading by viewModel.isLoading.observeAsState(false)
    val userState by viewModel.userState.observeAsState(UserState.Loading)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        }
        viewModel.fetchUser(context)
    }
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        HeaderImage(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.CenterHorizontally),
            "https://vgnnieizrwmjemlnziaj.supabase.co/storage/v1/object/public/storeApp/users/${user.image}.jpg",
            user.image
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Text(
            text = "Nombre: ${user.name}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Apellido: ${user.lastName}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Correo: ${user.email}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Usuario: ${user.nickName}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.weight(1f))
        DynamicButton(type = 2, text = "Editar", enable = true) {
            navController.navigate("editUser/${user.id}/${user.name}/${user.name}/${user.email}/${user.nickName}/${user.image}")
        }
        Spacer(modifier = Modifier.padding(8.dp))
        DynamicButton(type = 3, text = "Cerrar Sesión", enable = true) {
            coroutineScope.launch {
                viewModel.signOut(context).apply {
                    Toast.makeText(context, "Cerrando sesion", Toast.LENGTH_SHORT).show()
                }
                viewModel.onCloseSelected().apply {
                    when (userState) {
                        is UserState.Success -> {
                            Toast.makeText(
                                context,
                                (userState as UserState.Success).message,
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("login")
                        }

                        is UserState.Error -> {
                            Toast.makeText(
                                context,
                                (userState as UserState.Error).message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
    }
}


@Composable
fun HeaderImage(modifier: Modifier, url: String, image: String) {
    if (image != "noImage") {
        val painter = rememberAsyncImagePainter(url)
        Image(
            painter = painter,
            contentDescription = "Header Image",
            modifier = modifier,
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.user_id_svgrepo_com),
            contentDescription = "Header Image",
            modifier = modifier
        )
    }
}

