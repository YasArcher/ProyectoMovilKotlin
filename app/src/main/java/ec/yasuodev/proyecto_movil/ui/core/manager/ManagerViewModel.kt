package ec.yasuodev.proyecto_movil.ui.core.manager

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.launch

class ManagerViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _managerList = MutableLiveData<List<User>>()
    val managerList: LiveData<List<User>> = _managerList
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    private val _store = MutableLiveData<Store>()
    val store: LiveData<Store> = _store
    private val _userID = MutableLiveData<String>()
    val userID: LiveData<String> = _userID
    private val _storeID = MutableLiveData<String>()
    val storeID: LiveData<String> = _storeID
    fun fetchUser(context: Context) {
        val token = TokenManager.getToken(context)
        val userID = TokenDecoding.decodeJWT(token ?: "")["sub"] as? String ?: "No UserID found"
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("users").select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "lastname",
                        "email",
                        "nickname",
                        "image"
                    )
                ) {
                    filter {
                        eq("id", userID ?: "")
                    }
                }.decodeSingle<User>()
                _user.value = response
                _userID.value = response.id
                fetchStore()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchStore() {
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
                _storeID.value = response.id
                fetchManagmentStores()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchManagmentStores() {
        viewModelScope.launch {
            try {
                val rpcParams = mapOf(
                    "store_id" to storeID.value
                )
                val response: List<User> =
                    SupabaseClient.client.postgrest.rpc("get_managers_by_store", rpcParams)
                        .decodeList()

                _managerList.value = response
            } catch (e: Exception) {
                Log.e("ManagerViewModel", "Error fetching managers", e)
            }
        }
    }

    fun deleteManager(managerID: String, storeID: String) {
        println("Deleting manager $managerID from store $storeID*******************************************")
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("managers").delete {
                    filter {
                        eq("id_user", managerID)
                        eq("id_store", storeID)
                    }
                }
                fetchStore()
            } catch (e: Exception) {
                println("Error deleting manager $managerID from store $storeID*******************************************${e.message}")
            }
        }
    }

    fun addManagerByEmail(storeID: String, userEmail: String) {
        Log.d("ManagerViewModel", "Adding manager with email $userEmail to store $storeID")
        viewModelScope.launch {
            try {
                val rpcParams = mapOf(
                    "store_id" to storeID,
                    "user_email" to userEmail
                )
                SupabaseClient.client.postgrest.rpc("add_manager_by_email", rpcParams)
                fetchManagmentStores()
            } catch (e: Exception) {

            }
        }
    }

}