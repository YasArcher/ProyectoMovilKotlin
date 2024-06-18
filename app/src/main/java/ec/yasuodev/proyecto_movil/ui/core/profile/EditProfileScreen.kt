package ec.yasuodev.proyecto_movil.ui.core.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.core.models.EditState
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicButton
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicField
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicText
import ec.yasuodev.proyecto_movil.ui.shared.models.User
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(viewModel: EditProfileViewModel, navController: NavController, user: User) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Edit(Modifier.fillMaxSize(), viewModel, navController, user)
    }
}

@Composable
fun Edit(
    modifier: Modifier,
    viewModel: EditProfileViewModel,
    navController: NavController,
    user: User
) {
    val context = LocalContext.current
    val editState by viewModel.editState.observeAsState(initial = EditState.Loading)
    val name: String by viewModel.name.observeAsState(initial = user.name)
    val lastName: String by viewModel.lastName.observeAsState(initial = user.lastName)
    val nickName: String by viewModel.nickName.observeAsState(initial = user.nickName)
    val editEnable: Boolean by viewModel.editEnable.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        LaunchedEffect(key1 = viewModel) {
            viewModel.id = user.id
        }
        Column(modifier = modifier) {
            DynamicField(
                value = name,
                onTextFieldChange = { newValue ->
                    viewModel.onEditChanged(
                        newValue,
                        lastName,
                        nickName
                    )
                },
                tipo = 2,
            )
            Spacer(modifier = Modifier.padding(8.dp))
            DynamicText(
                message = viewModel.onEditMessageName(name),
                state = viewModel.isValidName(name)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            DynamicField(
                value = lastName,
                onTextFieldChange = { newValue ->
                    viewModel.onEditChanged(
                        name,
                        newValue,
                        nickName
                    )
                },
                tipo = 3,
            )
            Spacer(modifier = Modifier.padding(8.dp))
            DynamicText(
                message = viewModel.onEditMessageLastName(lastName),
                state = viewModel.isValidLastName(lastName)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            DynamicField(
                value = nickName,
                onTextFieldChange = { newValue ->
                    viewModel.onEditChanged(
                        name,
                        lastName,
                        newValue
                    )
                },
                tipo = 4,
            )
            Spacer(modifier = Modifier.padding(8.dp))
            DynamicText(
                message = viewModel.onEditMessageNickName(nickName),
                state = viewModel.isValidNickName(nickName)
            )
            Spacer(modifier = Modifier.padding(16.dp))
            DynamicButton(type = 1, text = "Editar", enable = editEnable) {
                coroutineScope.launch {
                    viewModel.updateUser().apply {
                        Toast.makeText(context, "Actualizando usuario", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.onEditSelected().apply {
                        when (editState) {
                            is EditState.Success -> {
                                Toast.makeText(
                                    context,
                                    (editState as EditState.Success).message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("profile")
                            }

                            is EditState.Error -> {
                                Toast.makeText(
                                    context,
                                    (editState as EditState.Error).message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}