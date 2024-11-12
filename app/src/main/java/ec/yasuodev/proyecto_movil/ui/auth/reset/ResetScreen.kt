package ec.yasuodev.proyecto_movil.ui.auth.reset

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetScreen(viewModel: ResetViewModel, navController: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Encabezado con fondo morado y texto centrado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(320.dp)
                        .background(
                            color = Color(0xFF9B86BE),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Restablecer contraseña",
                            fontSize = 40.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 38.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HeaderImage(modifier = Modifier.size(125.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Texto de instrucción
                Text(
                    text = "Ingrese su correo electrónico",
                    fontSize = 16.sp,
                    color = Color(0xFF443D8B),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Formulario para correo electrónico y botón de enviar
                Reset(Modifier.padding(horizontal = 32.dp), viewModel, navController)
            }
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
        ) {
            DynamicField(
                value = email,
                onTextFieldChange = { newValue -> viewModel.onResetChanged(newValue) },
                tipo = 1,
            )
            Spacer(modifier = Modifier.padding(8.dp))
            DynamicText(
                message = viewModel.onResetMessageEmail(email),
                state = viewModel.isValidEmail(email)
            )
            Spacer(modifier = Modifier.padding(16.dp))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_back),
        contentDescription = "Header Image",
        modifier = modifier,
        colorFilter = ColorFilter.tint(Color.White)
    )
}

@Preview(showBackground = true)
@Composable
fun ResetScreenPreview() {
    val navController = rememberNavController()
    val viewModel = ResetViewModel() // Asegúrate de que ResetViewModel pueda ser instanciado sin parámetros

    ResetScreen(viewModel = viewModel, navController = navController)
}
