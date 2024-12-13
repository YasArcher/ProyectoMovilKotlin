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
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class HomeViewModel : ViewModel() {
    private val _token = MutableLiveData<String>()
    private val _user = MutableLiveData<User>()
    private val _userID = MutableLiveData<String>()
    private val _store = MutableLiveData<Store>()
    private val _storeList = MutableLiveData<List<Store>>()

    val token: LiveData<String> = _token
    val user: LiveData<User> = _user
    val userID: LiveData<String> = _userID
    val store: LiveData<Store> = _store
    val storeList: LiveData<List<Store>> = _storeList


    open fun fetchToken(context: Context) {
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
            fetchManagmentSores(_userID.value!!)
        }
    }

    private fun fetchUserName() {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("users").select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "lastname",
                        "email",
                        "nickname",
                        "image",
                        "rol"
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

    private fun fetchManagmentSores(managerID: String) {
        viewModelScope.launch {
            try {
                val rpcParams = mapOf(
                    "manager_id" to managerID
                )
                val response: List<Store> = SupabaseClient.client.postgrest.rpc("get_stores_by_manager", rpcParams)
                    .decodeList()

                _storeList.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    open fun fetchStore() {
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