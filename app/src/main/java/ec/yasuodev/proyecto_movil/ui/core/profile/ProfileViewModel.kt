package ec.yasuodev.proyecto_movil.ui.core.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.auth.models.UserState
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenDecoding
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.User
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    private val _userState = MutableLiveData<UserState>(UserState.Loading)
    val userState: LiveData<UserState> = _userState
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    suspend fun onCloseSelected() {
        _isLoading.value = true
        delay(4000)
        _isLoading.value = false
    }

    fun fetchUser(context: Context) {
        val token = TokenManager.getToken(context)
        val userID = TokenDecoding.decodeJWT(token ?: "").get("sub") as? String ?: "No UserID found"
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("users").select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "lastName",
                        "email",
                        "nickName",
                        "image"
                    )
                ) {
                    filter {
                        eq("id", userID ?: "")
                    }
                }.decodeSingle<User>()
                _user.value = response
            } catch (e: Exception) {
                e.printStackTrace()
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
}