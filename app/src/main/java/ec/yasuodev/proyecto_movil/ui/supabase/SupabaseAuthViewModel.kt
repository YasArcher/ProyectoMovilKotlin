package ec.yasuodev.proyecto_movil.ui.supabase

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.auth.utils.SharedPreferenceHelper
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch

class SupabaseAuthViewModel: ViewModel() {
    private val _userState = MutableLiveData<UserState>(UserState.Loading)
    val userState: LiveData<UserState> = _userState
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
                _userState.value = UserState.Error("Error al iniciar sesión: Revise sus credenciales")
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