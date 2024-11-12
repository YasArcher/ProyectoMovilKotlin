package ec.yasuodev.proyecto_movil.ui.auth.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicButton
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicField
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicText
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(viewModel: RegisterViewModel, navController: NavController) {
    Scaffold(
        containerColor = Color(0xFF9B86BE) // Todo el fondo en color morado
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Habilita el desplazamiento
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabecera morada con texto centrado y la imagen debajo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Regístrate",
                            fontSize = 30.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        HeaderImage(modifier = Modifier.size(100.dp))
                    }
                }

                // Fondo blanco con esquinas redondeadas para el formulario
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 45.dp, topEnd = 45.dp)
                        )
                        .padding(horizontal = 48.dp, vertical = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    RegisterContent(viewModel, navController)
                }
            }
        }
    }
}

@Composable
fun RegisterContent(viewModel: RegisterViewModel, navController: NavController) {
    val userState: UserState by viewModel.userState.observeAsState(initial = UserState.Loading)
    val context = LocalContext.current
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val confirmPassword: String by viewModel.confirmPassword.observeAsState(initial = "")
    val registerEnable: Boolean by viewModel.registerEnable.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val passwordVisible: Boolean by viewModel.passwordVisible.observeAsState(initial = false)
    val confirmPasswordVisible: Boolean by viewModel.confirmPasswordVisible.observeAsState(initial = false)
    val registerSuccess by viewModel.registerSuccess.observeAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.padding(5.dp))

            Text(
                text = "Email",
                fontSize = 14.sp,
                color = Color(0xFF443D8B),
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.padding(4.dp))

            DynamicField(
                value = email,
                onTextFieldChange = { newValue ->
                    viewModel.onRegisterChanged(
                        newValue,
                        password,
                        confirmPassword
                    )
                },
                tipo = 1,
            )
            Spacer(modifier = Modifier.padding(4.dp))
            DynamicText(
                message = viewModel.onRegisterMessageEmail(email),
                state = viewModel.isValidEmail(email)
            )

            Spacer(modifier = Modifier.padding(2.dp))

            Text(
                text = "Contraseña",
                fontSize = 14.sp,
                color = Color(0xFF443D8B),
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.padding(4.dp))

            DynamicField(
                value = password,
                onTextFieldChange = { newValue ->
                    viewModel.onRegisterChanged(
                        email,
                        newValue,
                        confirmPassword
                    )
                },
                tipo = 0,
                passwordVisible = passwordVisible,
                onVisibilityChange = { viewModel.togglePasswordVisibility() }
            )
            Spacer(modifier = Modifier.padding(4.dp))
            DynamicText(
                message = viewModel.onRegisterMessagePassword(password),
                state = viewModel.isValidPassword(password)
            )

            Spacer(modifier = Modifier.padding(2.dp))

            Text(
                text = "Confirmar Contraseña",
                fontSize = 14.sp,
                color = Color(0xFF443D8B),
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.padding(4.dp))

            DynamicField(
                value = confirmPassword,
                onTextFieldChange = { newValue ->
                    viewModel.onRegisterChanged(
                        email,
                        password,
                        newValue
                    )
                },
                tipo = 0,
                passwordVisible = confirmPasswordVisible,
                onVisibilityChange = { viewModel.toggleConfirmPasswordVisibility() }
            )
            Spacer(modifier = Modifier.padding(4.dp))
            DynamicText(
                message = viewModel.onRegisterMessageConfirmationPassword(confirmPassword),
                state = viewModel.isConfirmPassword(password, confirmPassword)
            )

            Spacer(modifier = Modifier.height(16.dp))

            DynamicButton(
                type = 4,
                text = "Registrarme",
                enable = registerEnable,
                method = {
                    coroutineScope.launch {
                        val register = viewModel.signUp(context, email, password).apply {
                            Toast.makeText(context, "Registrando usuario", Toast.LENGTH_SHORT).show()
                        }
                        viewModel.onRegiserSelected().apply {
                            if (registerSuccess) {
                                Toast.makeText(
                                    context,
                                    (userState as UserState.Success).message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("login")
                            } else {
                                Toast.makeText(
                                    context,
                                    (userState as UserState.Error).message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                },
                textSize = 16f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ya tienes cuenta? Inicia sesión aquí!",
                fontSize = 15.sp,
                color = Color(0xFF9B86BE),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(
                color = Color(0xFF5A639C),
                shape = RoundedCornerShape(34.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.convenience_store_cash_register_female_svgrepo_com),
            contentDescription = "Header Image",
            modifier = modifier.padding(6.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()
    val viewModel = RegisterViewModel() // Asegúrate de que RegisterViewModel pueda ser instanciado sin parámetros

    RegisterScreen(viewModel = viewModel, navController = navController)
}
