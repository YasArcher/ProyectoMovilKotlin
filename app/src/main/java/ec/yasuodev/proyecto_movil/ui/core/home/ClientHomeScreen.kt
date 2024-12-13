package ec.yasuodev.proyecto_movil.ui.core.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.shared.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(viewModel: HomeViewModel, navController: NavController) {
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel) {
        if (TokenManager.getToken(context) == null) {
            navController.navigate("login")
        }
        viewModel.fetchToken(context)
        viewModel.fetchStore()
    }
    val user by viewModel.user.observeAsState(User("", "", "", "", "", ""))

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                HomeTopBar(user)
                Spacer(modifier = Modifier.height(8.dp))
                ClientHomeContent(viewModel, navController, user, context)
            }
        }
    }
}

@Composable
fun HomeTopBar(user: User) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(225.dp)
            .background(Color(0xFF9B86BE), shape = RoundedCornerShape(bottomEnd = 125.dp, bottomStart = 125.dp)) // Fondo morado con bordes redondeados en la parte inferior
            .padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxHeight() // Llena todo el alto del Box
            .padding(start = 38.dp),
            verticalArrangement = Arrangement.Center // Centra el contenido verticalmente
        ){
            Text(
                text = "Hola ${user.nickname}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFFBBBBBB),
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "Inicio",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
            )
        }
    }
}

@Composable
fun ClientHomeContent(viewModel: HomeViewModel, navController: NavController, user: User, context: Context) {
    val store by viewModel.store.observeAsState(Store("", "", "", ""))
    val storeList by viewModel.storeList.observeAsState(listOf())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .offset(y = (-40).dp), // Ajuste para superponer las tarjetas con el encabezado
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio automático entre tarjetas
    ) {
        // Verifica si `store` tiene datos antes de mostrar la tarjeta
        if (store.id.isNotBlank()) {
            BusinessCard(store, navController, user, 1)
        }

        // Renderiza cada tarjeta de la lista de `storeList` desde el ViewModel
        storeList.forEach {
            BusinessCard(it, navController, user, 1)
        }


    }
}


@Composable
fun BusinessCard(store: Store, navController: NavController, user: User, tipo: Int) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp),
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
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = store.name, // Cambia el texto fijo por el nombre del negocio
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF9B86BE),
                        fontWeight = FontWeight.Bold,
                    )
                )


                // Agrega el icono pequeño al lado izquierdo del texto
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_touch_app_24), // Reemplaza con el ID de tu icono pequeño
                        contentDescription = null,
                        tint = Color(0xFF72BF85),
                        modifier = Modifier.size(16.dp) // Tamaño del icono
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // Espacio entre el icono y el texto
                    Text(
                        text = "Presione aquí",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF9B86BE)
                        )
                    )
                }
            }

            Image(
                painter = if (tipo == 1) painterResource(id = R.drawable.shop_open_online_store_svgrepo_com)
                else painterResource(id = R.drawable.seller_in_shop_person_offer_sell_svgrepo_com),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(100.dp))
            Image(
                painter = painterResource(id = R.drawable.baseline_arrow_circle_right_24),
                contentDescription = "Ir a ventas",
                modifier = Modifier
                    .size(38.dp)
                    .clickable {
                        navController.navigate("business/${store.id}/${user.id}")
                    },
                alignment = Alignment.Center,
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF72BF85))
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ClientHomeScreenPreview() {
//    val navController = rememberNavController()
//    val fakeViewModel = object : HomeViewModel() {
//        override fun fetchStore() {}
//        override fun fetchToken(context: Context) {}
//        // Aquí podrías agregar datos simulados si es necesario
//    }
//    ClientHomeScreen(viewModel = fakeViewModel, navController = navController)
//}
