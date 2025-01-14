package ec.yasuodev.proyecto_movil.ui.core.profile

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cuenta",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF9B51E0)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF3F3F3)) // Fondo claro
        ) {
            // Fondo morado
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, size.height * 0.6f)
                    cubicTo(
                        size.width * 0.2f, size.height,
                        size.width * 0.8f, size.height * 0.4f,
                        size.width, size.height * 0.6f
                    )
                    lineTo(size.width, 0f)
                    lineTo(0f, 0f)
                    close()
                }
                drawPath(path, color = Color(0xFF9B51E0)) // Fondo morado
            }

            Profile(Modifier.align(Alignment.Center), viewModel, navController)
        }
    }
}

@Composable
fun Profile(modifier: Modifier, viewModel: ProfileViewModel, navController: NavController) {
    val context = LocalContext.current
    val user by viewModel.user.observeAsState(User("", "", "", "", "", "", ""))
    val isLoading by viewModel.isLoading.observeAsState(false)
    val userState by viewModel.userState.observeAsState(UserState.Loading)
    val coroutineScope = rememberCoroutineScope()

    // `LaunchedEffect` para iniciar fetch de datos
    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        } else {
            viewModel.fetchUser(context)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Card para información del perfil
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Imagen del usuario
                    HeaderImage(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape),
                        url = "https://vgnnieizrwmjemlnziaj.supabase.co/storage/v1/object/public/storeApp/users/${user.image}.jpg",
                        image = user.image
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Información del usuario
                    ProfileInfo("Nombre", user.name)
                    ProfileInfo("Apellido", user.lastname)
                    ProfileInfo("Correo", user.email)
                    ProfileInfo("Usuario", user.nickname)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para editar
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A005A)),
                        onClick = {
                            navController.navigate("editUser/${user.id}/${user.name}/${user.lastname}/${user.email}/${user.nickname}/${user.image}")
                        }) {
                        Text("Editar")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para cerrar sesión
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.signOut(context).apply {
                                    Toast.makeText(context, "Cerrando sesión", Toast.LENGTH_SHORT).show()
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
                        }) {
                        Text("Cerrar Sesión")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfo(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .padding(vertical = 4.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun HeaderImage(modifier: Modifier, url: String, image: String) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (image != "noImage") {
            val painter = rememberAsyncImagePainter(url)
            Image(
                painter = painter,
                contentDescription = "Header Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.user_id_svgrepo_com),
                contentDescription = "Header Image",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )
        }
    }
}