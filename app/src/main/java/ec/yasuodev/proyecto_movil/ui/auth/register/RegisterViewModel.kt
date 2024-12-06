package ec.yasuodev.proyecto_movil.ui.auth.register

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class RegisterViewModel : ViewModel() {
    private val _userState = MutableLiveData<UserState>(UserState.Loading)
    val userState: LiveData<UserState> = _userState

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String> = _confirmPassword

    private val _registerEnable = MutableLiveData<Boolean>()
    val registerEnable: LiveData<Boolean> = _registerEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _passwordVisible = MutableLiveData<Boolean>(false)
    val passwordVisible: LiveData<Boolean> = _passwordVisible

    private val _confirmPasswordVisible = MutableLiveData<Boolean>(false)
    val confirmPasswordVisible: LiveData<Boolean> = _confirmPasswordVisible

    fun togglePasswordVisibility() {
        _passwordVisible.value = _passwordVisible.value != true
    }

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    fun toggleConfirmPasswordVisibility() {
        _confirmPasswordVisible.value = _confirmPasswordVisible.value != true
    }

    fun onRegisterMessagePassword(password: String? = null): String {
        if (password.isNullOrEmpty()) {
            return "Ingrese una contraseña"
        }
        if (isValidPassword(password)) {
            return "Contraseña válida"
        } else {
            return "Contraseña no válida: asegúrese de usar entre 8 y 15 caracteres"
        }
    }

    fun onRegisterMessageConfirmationPassword(confirmPassword: String? = null): String {
        if (confirmPassword.isNullOrEmpty()) {
            return "Confirme la contraseña"
        } else if (confirmPassword != _password.value) {
            return "Las contraseñas no coinciden"
        } else {
            return "Contraseña confirmada"
        }
    }

    fun onRegisterMessageEmail(email: String? = null): String {
        if (email.isNullOrEmpty()) {
            return "Ingrese un correo"
        }
        if (isValidEmail(email)) {
            return "Correo válido"
        } else {
            return "Correo inválido"
        }
    }

    fun onRegisterChanged(email: String, password: String, confirmPassword: String) {
        _email.value = email
        _password.value = password
        _confirmPassword.value = confirmPassword
        _registerEnable.value =
            isValidEmail(email) && isValidPassword(password) && isConfirmPassword(
                password,
                confirmPassword
            )
    }

    fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean = password.length in 8..15

    fun isConfirmPassword(password: String?, confirmPassword: String?): Boolean {
        if (password.isNullOrEmpty() || confirmPassword.isNullOrEmpty()) {
            return false
        }
        return password == confirmPassword
    }

    fun signUp(
        context: Context,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                println("Registrando usuario con email: $email y password: $password")

                SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                _registerSuccess.value = true
                _userState.value = UserState.Success("Registro exitoso")
            } catch (e: Exception) {
                _registerSuccess.value = false
                _userState.value = UserState.Error("Error al registrarse: Correo ya registrado")
                println("error$$$$$$$$$$$"+e)
            }
        }
    }

    suspend fun onRegiserSelected() {
        _isLoading.value = true
        delay(4000)
        _isLoading.value = false
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}
