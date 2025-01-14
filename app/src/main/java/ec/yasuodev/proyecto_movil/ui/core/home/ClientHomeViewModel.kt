package ec.yasuodev.proyecto_movil.ui.core.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenDecoding
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.shared.models.User
import ec.yasuodev.proyecto_movil.ui.shared.models.generateUUID
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClientHomeViewModel : ViewModel() {
    private val _token = MutableLiveData<String>()
    private val _user = MutableLiveData<User>()
    val _userID = MutableLiveData<String>()
    private val _store = MutableLiveData<Store>()
    private val _storeList = MutableLiveData<List<Store>>()
    private val _addState = MutableLiveData<AddState>()
    val addState: LiveData<AddState> = _addState
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name
    private val _owner = MutableLiveData<String>()
    val owner: LiveData<String> = _owner
    private val _business_image = MutableLiveData<String>()
    val business_image: LiveData<String> = _business_image

    val token: LiveData<String> = _token
    val user: LiveData<User> = _user
    val userID: LiveData<String> = _userID
    val store: LiveData<Store> = _store
    val storeList: LiveData<List<Store>> = _storeList


    open fun fetchToken(context: Context):String? {
        _token.value = TokenManager.getToken(context)
        getUserID()
        return _userID.value.toString()
    }

    suspend fun loading() {
        delay(4000)
    }

    fun addStore(name: String, context: Context) {
        val userId = fetchToken(context)
        viewModelScope.launch {
            if (name.isEmpty()) {
                _addState.value = AddState.Error("El nombre de la tienda no puede estar vacío")
                return@launch
            } else {
                val store = Store(
                    id = generateUUID(),
                    name = name,
                    owner = userId.toString(),
                    business_image = "",
                    status = false
                )
                try {
                    SupabaseClient.client.from("business").insert(
                        store
                    )
                    _addState.value = AddState.Success("Tienda agregada")
                } catch (e: Exception) {
                    _addState.value = AddState.Error("Error al agregar la tienda")
                }
            }
        }
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

    fun fetchStore() {
        // Si ya hay datos cargados en _storeList, no vuelvas a cargar
        if (!_storeList.value.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                // Consulta para obtener todas las tiendas de la tabla `business`
                val response: List<Store> = SupabaseClient.client.from("business").select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "owner",
                        "business_image"
                    )
                ).decodeList() // Decodifica una lista de objetos `Store`
                // Identificar el negocio principal
                val mainStoreId = _store.value?.id ?: "" // Obtiene el ID del negocio principal
                val mainStore = response.find { it.id == mainStoreId } // Encuentra la tienda principal en la lista

                // Filtrar para excluir el negocio principal
                _storeList.value = response.filter { it.id != mainStoreId } // Filtra la lista

                // Captura y registra la tienda excluida (opcional)
                mainStore?.let {
                    _store.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}