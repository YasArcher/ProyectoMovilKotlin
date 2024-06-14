package ec.yasuodev.proyecto_movil.ui.auth.reset

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ResetViewModel  : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _resetEnable = MutableLiveData<Boolean>()
    val resetEnable: LiveData<Boolean> = _resetEnable
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _userState = MutableLiveData<UserState>(UserState.Loading)
    val userState: LiveData<UserState> = _userState

    fun onResetMessageEmail(email: String? = null): String {
        if (email.isNullOrEmpty()) {
            return "Ingrese un correo"
        }
        if (isValidEmail(email)) {
            return "Correo válido"
        } else {
            return "Correo inválido"
        }
    }

    fun onResetChanged(email: String) {
        _email.value = email
        _resetEnable.value = isValidEmail(email)
    }

    fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    suspend fun onResetSelected() {
        _isLoading.value = true
        delay(4000)
        _isLoading.value = false
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
}