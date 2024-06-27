package ec.yasuodev.proyecto_movil.ui.core.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.shared.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bienvenido ${user.nickName}", style = MaterialTheme.typography.titleLarge
                    )
                },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HomeContent(viewModel, navController, user)
        }
    }
}

@Composable
fun HomeContent(viewModel: HomeViewModel, navController: NavController, user: User) {
    val store by viewModel.store.observeAsState(Store("", "", "", ""))
    Column(modifier = Modifier.fillMaxSize()) {
        DefaultCard(store, navController, user)
    }
}

@Composable
fun DefaultCard(store: Store, navController: NavController, user: User) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("business/${store.id}/${user.id}")
            },
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .size(200.dp)
        ) {
            Column {
                Text(
                    text = "${store.name} hoy",
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.padding(7.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ingresos",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "Egresos",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.Red
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "45.27 $",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue
                    )
                    Text(
                        text = "8.50 $",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.Red
                    )
                }
            }
        }
    }
}