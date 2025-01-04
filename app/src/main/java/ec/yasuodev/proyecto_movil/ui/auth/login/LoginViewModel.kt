package ec.yasuodev.proyecto_movil.ui.auth.login

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class LoginViewModel : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password
    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _loginEnable
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _userState = MutableLiveData<UserState>(UserState.Loading)
    val userState: LiveData<UserState> = _userState
    private val _passwordVisible = MutableLiveData<Boolean>(false)
    val passwordVisible: LiveData<Boolean> = _passwordVisible
    private val _rol = MutableLiveData<String>()
    val rol: LiveData<String> = _rol

    fun togglePasswordVisibility() {
        _passwordVisible.value = _passwordVisible.value != true
    }

    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPassword(password)
    }

    fun onLoginMessageEmail(email: String? = null): String {
        if (email.isNullOrEmpty()) {
            return "Ingrese un correo"
        }
        if (isValidEmail(email)) {
            return "Correo válido"
        } else {
            return "Correo inválido"
        }
    }

    fun onLoginMessagePassword(password: String? = null): String {
        if (password.isNullOrEmpty()) {
            return "Ingrese una contraseña"
        }
        if (isValidPassword(password)) {
            return "Contraseña válida"
        } else {
            return "Contraseña no inválida: asegurese de usar 8 a 15 caracteres"
        }
    }

    fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean = password.length in 8..15

    suspend fun onLoginSelected() {
        _isLoading.value = true
        delay(4000)
        _isLoading.value = false
    }

    fun navigateBasedOnRole(role: String, navController: NavController) {
        Log.d("DEBUG", "Rol: $role")
        when (role) {
            "client" -> navController.navigate("clientNavGraph") {
                Log.d("DEBUG", "navegando a cliente")
                popUpTo("login") { inclusive = true }
            }
            "seller" -> navController.navigate("sellerNavGraph") {
                Log.d("DEBUG", "navegando a vendedor")
                popUpTo("login") { inclusive = true }
            }
            else -> navController.navigate("home")
        }
    }

    fun fetchUserRole(userId: String) {
        viewModelScope.launch {
            try {
                // Realiza la consulta a la tabla `users` para obtener el rol del usuario
                val response = SupabaseClient.client.from("users").select(
                    columns = Columns.list("rol")
                ) {
                    filter {
                        eq("id", userId) // Filtra por el ID del usuario
                    }
                }.decodeSingle<Map<String, String>>() // Decodifica la respuesta como un mapa
                _userState.value = UserState.Success("Rol Obtenido")
                _rol.value = response["rol"] ?: "unknown" // Obtén el rol del mapa, o usa "unknown" por defecto
            } catch (e: Exception) {
                _userState.value = UserState.Error("Rol no Obtenido"+ e.message)
            }
        }
    }

    fun signIn(
        context: Context,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                val sessionDetails = SupabaseClient.client.auth.currentSessionOrNull()
                if (sessionDetails != null) {
                    TokenManager.saveToken(
                        context,
                        sessionDetails.toString(),
                        sessionDetails.accessToken
                    )

                    // Obtener el rol del usuario
                    sessionDetails.user?.let {
                        fetchUserRole(it.id)
                    }
                }
            } catch (e: Exception) {
                _userState.value =
                    UserState.Error("Error al iniciar sesión: ${e.message}")
            }
        }
    }
}