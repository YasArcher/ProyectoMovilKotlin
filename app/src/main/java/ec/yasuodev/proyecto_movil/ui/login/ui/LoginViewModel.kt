package ec.yasuodev.proyecto_movil.ui.login.ui

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.login.model.UserState
import ec.yasuodev.proyecto_movil.ui.login.utils.SharedPreferenceHelper
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    fun onLoginMessageEmail(email: String): String {
        if (email.isNullOrEmpty()) {
            return "Ingrese un correo"
        }
        if (isValidEmail(email)) {
            return "Correo válido"
        } else {
            return "Correo inválido"
        }
    }

    fun onLoginMessagePassword(password: String): String {
        if (password.isNullOrEmpty()) {
            return "Ingrese una contraseña"
        }
        if (isValidPassword(password)) {
            return "Contraseña válida"
        } else {
            return "Contraseña no inválida: asegurese de usar 6 a 15 caracteres"
        }
    }

    fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean = password.length in 6..15

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
                saveToken(context)
                _userState.value = UserState.Success("Inicio de sesión exitoso")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error al iniciar sesión: ${e.message}")
            }
        }
    }

    fun signUp(
        context: Context,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                saveToken(context)
                _userState.value = UserState.Success("Registro exitoso")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error al registrarse: ${e.message}")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
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
                val token = getToken(context)
                if (token.isNullOrEmpty()) {
                    _userState.value = UserState.Error("No se ha iniciado sesión")
                } else {
                    SupabaseClient.client.auth.retrieveUser(token)
                    SupabaseClient.client.auth.refreshCurrentSession()
                    saveToken(context)
                    _userState.value = UserState.Success("Sesión verificada")
                }

            } catch (e: Exception) {
                _userState.value = UserState.Error("Error al verificar la sesión: ${e.message}")
            }
        }
    }

    private fun saveToken(context: Context) {
        viewModelScope.launch {
            val token = SupabaseClient.client.auth.currentSessionOrNull()
            val sharedPreferenceHelper = SharedPreferenceHelper(context)
            sharedPreferenceHelper.saveStringData("accessToken", token.toString())
        }
    }

    private fun getToken(context: Context): String? {
        val sharedPreferenceHelper = SharedPreferenceHelper(context)
        return sharedPreferenceHelper.getStringData("accessToken")
    }
}