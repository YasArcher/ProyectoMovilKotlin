package ec.yasuodev.proyecto_movil.ui.core.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.shared.models.User
import kotlinx.coroutines.delay

@Composable
fun VendedorHomeScreen(viewModel: VendorHomeViewModel, navController: NavController) {
    val user by viewModel.user.observeAsState(User("", "", "", "", "", "", ""))
    val store by viewModel.store.observeAsState(Store("", "", "", "", false))
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        }
        viewModel.fetchToken(context)
        viewModel.fetchStore()
        while (store.id.isBlank()) {
            delay(1000)
        }
    }
    Scaffold(
        containerColor = Color(0xFF9B86BE) // Fondo lavanda
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Encabezado con el texto "MIS NEGOCIOS"
                VendedorTopBar()
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Aplica el padding deseado aquí
                ) {
                    VendedorBusinessCard(store,
                        navController = navController,
                        user = user,
                        tipo = 1 // Puedes cambiar el tipo para aplicar un borde si deseas
                    )
                }
                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre la tarjeta única y el formulario

                // Fondo blanco para el formulario con las demás tarjetas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 45.dp, topEnd = 45.dp)
                        )
                        .padding(
                            horizontal = 16.dp, vertical = 24.dp
                        ), // Añade padding alrededor del formulario
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Otros Negocios",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9B86BE),
                            modifier = Modifier.padding(bottom = 8.dp) // Espacio entre texto y línea
                        )
                        Divider(
                            color = Color(0xFF9B86BE), // Color de la línea
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth(0.5f) // Ajusta el ancho de la línea (50% de la pantalla)
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el Divider y el contenido
                        VendedorHomeContent(viewModel, navController, user, context)
                    }
                }
            }
        }
    }
}

@Composable
fun VendedorTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                Color(0xFF9B86BE),
                shape = RoundedCornerShape(bottomEnd = 125.dp, bottomStart = 125.dp)
            )
            .padding(vertical = 2.dp, horizontal = 16.dp)
    ) {
        // Iconos en la parte superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de los tres puntos en la esquina izquierda
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {


                Icon(
                    painter = painterResource(id = R.drawable.ic_dot),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            // Icono de la casa en la esquina derecha
            Icon(
                painter = painterResource(id = R.drawable.baseline_home_24), // Reemplaza con el ícono de la casa
                contentDescription = "Home", tint = Color.White, modifier = Modifier.size(38.dp)
            )
        }

        // Texto "MIS NEGOCIOS" y línea en el centro y parte inferior
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter) // Coloca el contenido en la parte inferior central
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "MIS NEGOCIOS", style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp)) // Espacio entre el texto y la línea
            Divider(
                color = Color.White, // Color de la línea
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth(0.5f) // Ajusta el ancho de la línea (50% del ancho del texto)
            )
        }
    }
}


@Composable
fun VendedorHomeContent(
    viewModel: VendorHomeViewModel, navController: NavController, user: User, context: Context
) {
    val store by viewModel.store.observeAsState(null) // Observa la tienda principal
    val storeList by viewModel.storeList.observeAsState(listOf()) // Observa la lista de negocios

    // Filtra los negocios para excluir el negocio principal
    val filteredStoreList = storeList.filter { it.id != store?.id }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjetas para otras tiendas (en "Otros Negocios")
        if (filteredStoreList.isNotEmpty()) {
            filteredStoreList.forEach {
                VendedorBusinessCard(
                    store = it, navController = navController, user = user, tipo = 1
                )
            }
        } else {
            // Mensaje para cuando no hay otras tiendas
            Text(
                text = "No hay negocios adicionales registrados.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Gray, fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Composable
fun VendedorBusinessCard(store: Store?, navController: NavController, user: User, tipo: Int) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)
            ) {
                // Mostrar el nombre del negocio o el mensaje de configuración
                if (store != null) {
                    Text(
                        text = store.name.ifBlank { "Configure su negocio" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF9B86BE),
                            fontWeight = FontWeight.Bold,
                        )
                    )
                }

                // Icono y texto adicional
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_touch_app_24),
                        contentDescription = null,
                        tint = Color(0xFF72BF85),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (store?.name.isNullOrBlank()) "Crear negocio" else "Presione aquí",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF9B86BE)
                        )
                    )
                }
            }

            // Imagen del tipo de negocio
            Image(
                painter = if (tipo == 1) painterResource(id = R.drawable.shop_open_online_store_svgrepo_com)
                else painterResource(id = R.drawable.seller_in_shop_person_offer_sell_svgrepo_com),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            // Icono de flecha para navegar
            Image(
                painter = painterResource(id = R.drawable.baseline_arrow_circle_right_24),
                contentDescription = "Ir a configuración",
                modifier = Modifier
                    .size(38.dp)
                    .clickable {
                        if (store?.name.isNullOrBlank()) {
                            navController.navigate("configure_business")
                        } else {
                            if (store != null) {
                                navController.navigate("business/${store.id}/${user.id}")
                            }
                        }
                    },
                alignment = Alignment.Center,
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF72BF85))
            )
        }
    }
}