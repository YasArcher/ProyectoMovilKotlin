package ec.yasuodev.proyecto_movil.ui.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicButton
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicField
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {
    Scaffold(
        containerColor = Color(0xFFF5F5F5) // Fondo claro
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Cabecera morada con la imagen superpuesta
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(190.dp)
                    .background(
                        color = Color(0xFF9B86BE),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Digital Inventory Hub",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 29.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                )
            }

            // Imagen superpuesta en la cabecera
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 100.dp) // Ajusta la posición vertical de la imagen
            ) {
                HeaderImage(
                    modifier = Modifier.size(220.dp)
                    
                )
            }
            // Contenido principal desplazable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 250.dp) // Deja espacio para la cabecera
                    .verticalScroll(rememberScrollState()) // Habilita el desplazamiento en la columna
            ) {
                Login(modifier = Modifier.align(Alignment.CenterHorizontally), viewModel, navController)
            }
        }
    }
}

@Composable
fun Login(modifier: Modifier, viewModel: LoginViewModel, navController: NavController) {
    val context = LocalContext.current
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val loginEnable: Boolean by viewModel.loginEnable.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val passwordVisible: Boolean by viewModel.passwordVisible.observeAsState(initial = false)
    val userState: UserState by viewModel.userState.observeAsState(initial = UserState.Loading)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) != null) {
            navController.navigate("home")
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(
            modifier = modifier.padding(horizontal = 32.dp)
        ) {
            Spacer(modifier = Modifier.padding(16.dp))
            // Campo de Email
            Text(
                text = "Email",
                fontSize = 14.sp,
                color = Color(0xFF443D8B), // Color morado del texto
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.padding(3.dp))

            DynamicField(
                value = email,
                onTextFieldChange = { newValue -> viewModel.onLoginChanged(newValue, password) },
                tipo = 1
            )
            Spacer(modifier = Modifier.padding(4.dp))

            // Mensaje de validación para email
            DynamicText(
                message = viewModel.onLoginMessageEmail(email),
                state = viewModel.isValidEmail(email)
            )
            Spacer(modifier = Modifier.padding(8.dp))

            // Campo de Contraseña
            Text(
                text = "Contraseña",
                fontSize = 14.sp,
                color = Color(0xFF443D8B),
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.padding(3.dp))

            DynamicField(
                value = password,
                onTextFieldChange = { newValue -> viewModel.onLoginChanged(email, newValue) },
                tipo = 0,
                passwordVisible = passwordVisible,
                onVisibilityChange = { viewModel.togglePasswordVisibility() }
            )
            Spacer(modifier = Modifier.padding(4.dp))

            // Mensaje de validación para contraseña
            DynamicText(
                message = viewModel.onLoginMessagePassword(password),
                state = viewModel.isValidPassword(password)
            )
            Spacer(modifier = Modifier.padding(8.dp))

            // Link de "¿Olvidaste tu contraseña?"
            ForgotPassword(
                modifier = Modifier.align(Alignment.End),
                navController = navController
            )
            Spacer(modifier = Modifier.padding(10.dp))

            // Botón de Iniciar Sesión
            DynamicButton(
                type = 4,
                text = "Iniciar Sesión",
                enable = loginEnable,
                method = {
                    coroutineScope.launch {
                        viewModel.signIn(context, email, password).apply {
                            Toast.makeText(context, "Iniciando sesión", Toast.LENGTH_SHORT).show()
                        }
                        viewModel.onLoginSelected().apply {
                            when (userState) {
                                is UserState.Success -> {
                                    Toast.makeText(
                                        context,
                                        (userState as UserState.Success).message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("home")
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
                },
                textSize = 16f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            // Texto de registro
            Text(
                text = "No tienes cuenta? Regístrate ya!",
                fontSize = 15.sp,
                color = Color(0xFF9B86BE),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ForgotPassword(modifier: Modifier, navController: NavController) {
    Text(
        text = "¿Olvidaste tu contraseña?",
        modifier = modifier.clickable { navController.navigate("reset") },
        fontSize = 15.sp,
        fontFamily = FontFamily.SansSerif,
        color = Color(0xFF443D8B) // Color morado
    )
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.login),
        contentDescription = "Header Image",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Utiliza un NavController y LoginViewModel de prueba
    val navController = rememberNavController()
    val viewModel = LoginViewModel() // Asegúrate de que LoginViewModel pueda ser instanciado sin parámetros

    LoginScreen(viewModel = viewModel, navController = navController)
}
