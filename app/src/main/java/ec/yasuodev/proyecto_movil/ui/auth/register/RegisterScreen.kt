package ec.yasuodev.proyecto_movil.ui.auth.register

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.auth.login.HeaderImage
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicButton
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicField
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicText
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(viewModel: RegisterViewModel, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Register(Modifier.align(Alignment.Center), viewModel, navController)
    }
}

@Composable
fun Register(modifier: Modifier, viewModel: RegisterViewModel, navController: NavController) {
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
        Column(modifier = modifier) {
            HeaderImage(Modifier.align((Alignment.CenterHorizontally)))
            Spacer(modifier = Modifier.padding(16.dp))
            /*Email TextField*/
            DynamicField(
                value = email,
                onTextFieldChange = { newValue ->
                    viewModel.onRegisterChanged(
                        newValue,
                        password,
                        confirmPassword
                    )
                },
                isPassword = 1,
            )
            Spacer(modifier = Modifier.padding(4.dp))
            DynamicText(
                message = viewModel.onRegisterMessageEmail(email),
                state = viewModel.isValidEmail(email)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            /*Password TextField*/
            DynamicField(
                value = password,
                onTextFieldChange = { newValue ->
                    viewModel.onRegisterChanged(
                        email,
                        newValue,
                        confirmPassword
                    )
                },
                isPassword = 0,
                passwordVisible = passwordVisible,
                onVisibilityChange = { viewModel.togglePasswordVisibility() }
            )
            Spacer(modifier = Modifier.padding(4.dp))
            DynamicText(
                message = viewModel.onRegisterMessagePassword(password),
                state = viewModel.isValidPassword(password)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            /*ConfirmPassword TextField*/
            DynamicField(
                value = confirmPassword,
                onTextFieldChange = { newValue ->
                    viewModel.onRegisterChanged(
                        email,
                        password,
                        newValue
                    )
                },
                isPassword = 0,
                passwordVisible = confirmPasswordVisible,
                onVisibilityChange = { viewModel.toggleConfirmPasswordVisibility() }
            )
            Spacer(modifier = Modifier.padding(4.dp))
            DynamicText(
                message = viewModel.onRegisterMessageConfirmationPassword(password),
                state = viewModel.isConfirmPassword(password, confirmPassword)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            /*Register Button*/
            DynamicButton(1, "Registrame", registerEnable) {
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
            }
        }
    }
}