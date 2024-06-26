package ec.yasuodev.proyecto_movil.ui.core.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenDecoding
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.shared.models.User
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _token = MutableLiveData<String>()
    private val _user = MutableLiveData<User>()
    private val _userID = MutableLiveData<String>()
    private val _store = MutableLiveData<Store>()

    val token: LiveData<String> = _token
    val user: LiveData<User> = _user
    val userID: LiveData<String> = _userID
    val store: LiveData<Store> = _store


    fun fetchToken(context: Context) {
        _token.value = TokenManager.getToken(context)
        getUserID()
    }

    suspend fun loading() {
        delay(4000)
    }

    private fun getUserID() {
        val decodedToken = TokenDecoding.decodeJWT(_token.value ?: "")
        _userID.value = decodedToken.get("sub") as? String ?: "No UserID found"
        if (_userID.value != null) {
            fetchUserName()
        }
    }

    private fun fetchUserName() {
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
                        eq("id", _userID.value ?: "")
                    }
                }.decodeSingle<User>()
                _user.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchStore() {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("business").select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "owner",
                        "business_image"
                    )
                ) {
                    filter {
                        eq("owner", _userID.value ?: "")
                    }
                }.decodeSingle<Store>()
                _store.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}