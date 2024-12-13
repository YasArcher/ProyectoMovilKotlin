package ec.yasuodev.proyecto_movil.ui.core.business

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.shared.models.Store

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileEditBusinessScreen(
    navController: NavController,
    viewModel: ProfileEditBusinessViewModel,
    storeId: String,
) {
    val store = viewModel.store.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val selectedImageUri = viewModel.selectedImageUri.collectAsState().value
    var showEditModal by remember { mutableStateOf(false) }
    val owner = viewModel.owner.collectAsState().value

    LaunchedEffect(key1 = viewModel) {
        viewModel.fetchBusiness(storeId)
        viewModel.fetchOwner(store?.owner ?: "")
    }

    Scaffold {  innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F3F3))
                .padding(innerPadding)
        ) {
            // Background wave
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, size.height * 0.6f)
                    cubicTo(
                        size.width * 0.2f, size.height * 1.2f,
                        size.width * 0.8f, size.height * 0.4f,
                        size.width, size.height * 0.8f
                    )
                    lineTo(size.width, 0f)
                    lineTo(0f, 0f)
                    close()
                }
                drawPath(
                    path = path,
                    color = Color(0xFF9B51E0)
                )
            }

            // Top icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }

                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home Icon",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        store?.let { currentStore ->
                            // Business image
                            Image(
                                painter = rememberImagePainter(
                                    data = selectedImageUri ?: currentStore.business_image
                                ),
                                contentDescription = "Business Image",
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.Gray, CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Business name
                            Text(
                                modifier = Modifier.padding(bottom = 10.dp),
                                text = "Nombre Negocio:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                            Text(
                                text = currentStore.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Owner name
                            Text(
                                modifier = Modifier.padding(bottom = 7.dp),
                                text = "Dueño:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                            Text(
                                text = owner?.name + " " + owner?.lastname,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Action buttons
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { showEditModal = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B51E0))
                                ) {
                                    Text(text = "Editar", color = Color.White)
                                }
                                Button(
                                    onClick = {
                                        viewModel.deleteBusiness(currentStore.id)
                                        navController.navigate("home")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60))
                                ) {
                                    Text(text = "Eliminar", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            if (showEditModal) {
                EditBusinessModal(
                    store = store,
                    onImageSelected = { uri -> viewModel.onImageSelected(uri) },
                    onSave = { name ->
                        viewModel.updateBusinessWithImage(name)
                        showEditModal = false
                    },
                    onDismiss = { showEditModal = false }
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF9B51E0)
                )
            }
        }
    }
}

@Composable
fun EditBusinessModal(
    store: Store?,
    onImageSelected: (Uri) -> Unit,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var businessName by remember { mutableStateOf(store?.name ?: "") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable { onDismiss() }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "EDITAR NEGOCIO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF4E4376),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Nombre del negocio") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar Imagen")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onSave(businessName) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B51E0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Guardar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}