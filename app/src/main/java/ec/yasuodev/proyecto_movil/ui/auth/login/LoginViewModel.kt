package ec.yasuodev.proyecto_movil.ui.auth.login

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
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
                        sessionDetails.accessToken.toString()
                    )
                    val store =  Store(
                        id = generateUUID(),
                        name = "Mi negocio",
                        owner = sessionDetails.user!!.id
                    )
                    SupabaseClient.client.from("business").insert(store)
                    _userState.value = UserState.Success("Inicio de sesión exitoso")
                }
            } catch (e: Exception) {
                _userState.value =
                    UserState.Error("Error al iniciar sesión: Revise sus credenciales")
            }
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
                TokenManager.clearToken(context)
                _userState.value = UserState.Success("Cierre de sesión exitoso")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error al cerrar sesión: ${e.message}")
            }
        }
    }

    fun isUserLoggedIn(
        context: Context
    ) {
        viewModelScope.launch {
            try {
                if (TokenManager.isTokenValid(context)) {
                    TokenManager.getAccessToken(context)
                        ?.let { SupabaseClient.client.auth.retrieveUser(it) }
                    val sessionDetails = SupabaseClient.client.auth.currentSessionOrNull()
                    if (sessionDetails != null) {
                        TokenManager.saveToken(
                            context,
                            sessionDetails.toString(),
                            sessionDetails.accessToken.toString()
                        )
                        _userState.value = UserState.Success("Inicio de sesión exitoso")
                    }
                    _userState.value = UserState.Success("Sesión verificada")
                } else {
                    _userState.value = UserState.Error("No se ha iniciado sesión")
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error al verificar la sesión: ${e.message}")
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.resetPasswordForEmail(email)
                _userState.value = UserState.Success("Correo de recuperación enviado")
            } catch (e: Exception) {
                _userState.value =
                    UserState.Error("Error al enviar correo de recuperación: ${e.message}")
            }
        }
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}