package ec.yasuodev.proyecto_movil.ui.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicButton
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicField
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicText
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Login(Modifier.align(Alignment.Center), viewModel, navController)
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
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(modifier = modifier) {
            HeaderImage(Modifier.align((Alignment.CenterHorizontally)))
            Spacer(modifier = Modifier.padding(16.dp))
            /*Email TextField*/
            DynamicField(
                value = email,
                onTextFieldChange = { newValue -> viewModel.onLoginChanged(newValue, password) },
                isPassword = 1,
            )
            Spacer(modifier = Modifier.padding(4.dp))
            DynamicText(
                message = viewModel.onLoginMessageEmail(email),
                state = viewModel.isValidEmail(email)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            /*Password TextField*/
            DynamicField(
                value = password,
                onTextFieldChange = { newValue -> viewModel.onLoginChanged(email, newValue) },
                isPassword = 0,
                passwordVisible = passwordVisible,
                onVisibilityChange = { viewModel.togglePasswordVisibility() }
            )
            Spacer(modifier = Modifier.padding(4.dp))
            DynamicText(
                message = viewModel.onLoginMessagePassword(password),
                state = viewModel.isValidPassword(password)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            ForgotPassword(Modifier.align(Alignment.End), navController)
            Spacer(modifier = Modifier.padding(8.dp))
            /*Login Button*/
            DynamicButton(1, "Iniciar Sesión", loginEnable) {
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
            }
            Spacer(modifier = Modifier.padding(8.dp))
            /*Register Button*/
            DynamicButton(type = 2, text = "Registrame", enable = true) {
                navController.navigate("register")
            }
        }
    }
}

@Composable
fun ForgotPassword(modifier: Modifier, navController: NavController) {
    Text(
        text = "¿Olvidaste tu contraseña?",
        modifier = modifier.clickable { navController.navigate("reset") },
        fontSize = 12.sp,
        fontStyle = FontStyle.Italic,
        color = Color.Blue
    )
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.tienda),
        contentDescription = "Header Image",
        modifier = modifier
    )
}