package ec.yasuodev.proyecto_movil.ui.core.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class ClientHomeViewModel : HomeViewModel() {

    // Puedes añadir más LiveData si necesitas almacenar datos específicos del cliente
    private val _clientSpecificStores = MutableLiveData<List<Store>>()
    val clientSpecificStores: LiveData<List<Store>> = _clientSpecificStores

    // Sobrescribe métodos si necesitas lógica específica para el cliente
    override fun fetchStore() {
        viewModelScope.launch {
            try {
                // Aquí puedes agregar lógica específica para obtener las tiendas del cliente
                val response = SupabaseClient.client.from("business").select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "owner",
                        "business_image"
                    )
                ) {
                    filter {
                        eq("owner", _userID.value ?: "") // Cambiar según sea necesario
                    }
                }.decodeList<Store>() // Cambiado para manejar múltiples tiendas
                _clientSpecificStores.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchClientData(context: Context) {
        // Lógica específica para cargar datos iniciales del cliente
        fetchToken(context)
        fetchStore()
    }
}
