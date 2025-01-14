package ec.yasuodev.proyecto_movil.ui.core.products

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenDecoding
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.core.models.EditState
import ec.yasuodev.proyecto_movil.ui.shared.models.ProductCategory
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CategoriesViewModel: ViewModel() {
    private val _categories = MutableLiveData<List<ProductCategory>>()
    val categories: MutableLiveData<List<ProductCategory>> = _categories
    private val _store = MutableLiveData<String>()
    val store: MutableLiveData<String> = _store
    private val _nombre = MutableLiveData<String>()
    val nombre: MutableLiveData<String> = _nombre
    private val _descripcion = MutableLiveData<String>()
    val descripcion: MutableLiveData<String> = _descripcion
    private val _isValid = MutableLiveData<Boolean>()
    val isValid: MutableLiveData<Boolean> = _isValid
    private val _addState = MutableLiveData<AddState>()
    val addState: MutableLiveData<AddState> = _addState
    private val _editState = MutableLiveData<EditState>()
    val editState: MutableLiveData<EditState> = _editState
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    suspend fun onAddEditSelected(){
        _isLoading.value = true
        delay(4000)
        isLoading.value = false
    }

    fun fetchCategories() {
        // Lógica para obtener las categorías
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("categories").select(
                    columns = Columns.list(
                        "id",
                        "category_name",
                        "description",
                        "is_active",
                        "store"
                    )
                ) {
                    filter {
                        eq("store", _store.value?: "")
                    }
                }.decodeList<ProductCategory>()
                _categories.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onCategoryChanged(name: String, description: String) {
        _nombre.value = name
        _descripcion.value = description
        _isValid.value = isValidCategory(name, description)
    }

    fun onMessage(line: String? = null): String {
        if (line.isNullOrEmpty()) {
            return "No dejar vacio este campo"
        }
        return "Campo lleno"
    }

    private fun isValidCategory(name: String, description: String): Boolean {
        return name.isNotEmpty() && description.isNotEmpty()
    }
}