package ec.yasuodev.proyecto_movil.ui.core.profile

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.core.models.EditState
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicButton
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicField
import ec.yasuodev.proyecto_movil.ui.shared.components.DynamicText
import ec.yasuodev.proyecto_movil.ui.shared.models.User
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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
    val image: String by viewModel.image.observeAsState(initial = user.image)
    val selectedImageUri: Uri by viewModel.selectedImageUri.observeAsState(initial = Uri.EMPTY)
    val coroutineScope = rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.onImageSelected(it)
            }
        }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        LaunchedEffect(key1 = viewModel) {
            viewModel.id = user.id
        }
        Column(modifier = modifier) {
            ImageHeader(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.CenterHorizontally),
                url = selectedImageUri.toString().ifEmpty {
                    "https://vgnnieizrwmjemlnziaj.supabase.co/storage/v1/object/public/storeApp/users/${user.image}.jpg"
                },
                image = viewModel.imageExist(image),
            ) {
                viewModel.onEditChanged(name, lastName, nickName, selectedImageUri)
                launcher.launch("image/*")
            }
            DynamicField(
                value = name,
                onTextFieldChange = { newValue ->
                    viewModel.onEditChanged(
                        newValue,
                        lastName,
                        nickName,
                        selectedImageUri
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
                        nickName,
                        selectedImageUri
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
                        newValue,
                        selectedImageUri
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
                    try {
                        viewModel.updateUser(
                            if (selectedImageUri == Uri.EMPTY) ByteArray(0) else
                                uriToByteArray(
                                    context.contentResolver,
                                    selectedImageUri
                                )
                        ).apply {
                            Toast.makeText(context, "Actualizando usuario", Toast.LENGTH_SHORT)
                                .show()
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
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Error al actualizar: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}

@Composable
fun ImageHeader(modifier: Modifier, url: String, image: String, onImageClick: () -> Unit) {
    if (image != "noImage") {
        val painter = rememberAsyncImagePainter(url)
        Image(
            painter = painter,
            contentDescription = "Header Image",
            modifier = modifier.clickable { onImageClick() },
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.user_id_svgrepo_com),
            contentDescription = "Header Image",
            modifier = modifier.clickable { onImageClick() }
        )
    }
}

suspend fun uriToByteArray(contentResolver: ContentResolver, uri: Uri): ByteArray {
    return withContext(Dispatchers.IO) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val bufferedInputStream = BufferedInputStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            try {
                var byte: Int
                while (bufferedInputStream.read().also { byte = it } != -1) {
                    outputStream.write(byte)
                }
                return@withContext outputStream.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
                throw e
            } finally {
                bufferedInputStream.close()
                outputStream.close()
            }
        } ?: throw IOException("InputStream for Uri $uri is null")
    }
}
