package ec.yasuodev.proyecto_movil.ui.auth.reset

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicButton
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicField
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetScreen(viewModel: ResetViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Restablecer Contraseña",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Reset(Modifier.align(Alignment.Center), viewModel, navController)
        }
    }
}

@Composable
fun Reset(modifier: Modifier, viewModel: ResetViewModel, navController: NavController) {
    val context = LocalContext.current
    val email: String by viewModel.email.observeAsState(initial = "")
    val resetEnable: Boolean by viewModel.resetEnable.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val userState: UserState by viewModel.userState.observeAsState(initial = UserState.Loading)
    val coroutineScope = rememberCoroutineScope()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(
            modifier = modifier
                .padding(horizontal = 32.dp)
        ) {
            HeaderImage(modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(16.dp))
            DynamicField(
                value = email,
                onTextFieldChange = { newValue -> viewModel.onResetChanged(newValue) },
                tipo = 1,
            )
            Spacer(modifier = Modifier.padding(4.dp))
            DynamicText(
                message = viewModel.onResetMessageEmail(email),
                state = viewModel.isValidEmail(email)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            DynamicButton(
                type = 1,
                text = "Enviar",
                enable = resetEnable,
                method = {
                    coroutineScope.launch {
                        viewModel.forgotPassword(email).apply {
                            Toast.makeText(context, "Enviando correo", Toast.LENGTH_SHORT).show()
                        }
                        viewModel.onResetSelected().apply {
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
                },
                textSize = 16f,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.cart_svgrepo_com),
        contentDescription = "Header Image",
        modifier = modifier
    )
}