package ec.yasuodev.proyecto_movil.ui.core.business

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.shared.models.User
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class ProfileBusinessViewModel(private val context: Context) : ViewModel() {

    private val _store = MutableStateFlow<Store?>(null)
    private val _owner = MutableStateFlow<User?>(null)
    val store: StateFlow<Store?> get() = _store
    val owner: StateFlow<User?> get() = _owner

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchBusiness(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
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
                        eq("id", id)
                    }
                }.decodeSingle<Store>()
                _store.value = response
            } catch (e: Exception) {
                println(e)
                _store.value = null // Manejar el caso de error.
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun fetchOwner(id: String) {
        viewModelScope.launch {
            try {
                val owner = SupabaseClient.client.from("users").select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "lastname",
                        "email"
                    )
                ) {
                    filter {
                        eq("id", id)
                    }
                }.decodeSingle<User>()
                _owner.value = owner
            }catch (e: Exception) {
                println(e)
                _owner.value = null
            }
        }
    }
}
