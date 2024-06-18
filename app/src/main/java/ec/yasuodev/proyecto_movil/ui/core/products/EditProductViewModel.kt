package ec.yasuodev.proyecto_movil.ui.core.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.core.models.EditState
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EditProductViewModel : ViewModel() {
    private val _editState = MutableLiveData<EditState>()
    val editState: LiveData<EditState> = _editState
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name
    private val _price = MutableLiveData<String>()
    val price: LiveData<String> = _price
    private val _stock = MutableLiveData<String>()
    val stock: LiveData<String> = _stock
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _editEnable = MutableLiveData<Boolean>()
    val editEnable: LiveData<Boolean> = _editEnable
    var id = ""

    fun onEditMessageName(name: String? = null): String {
        if (name.isNullOrEmpty()) {
            return "Ingrese un nombre"
        } else {
            return "Nombre válido"
        }
    }

    fun onEditMessagePrice(price: String? = null): String {
        if (price.isNullOrEmpty()) {
            return "Ingrese un precio"
        } else {
            return "Precio válido"
        }
    }

    fun onEditMessageStock(stock: String? = null): String {
        if (stock.isNullOrEmpty()) {
            return "Ingrese un stock"
        } else {
            return "Stock válido"
        }
    }

    fun onEditChanged(name: String, price: String, stock: String) {
        _name.value = name
        _price.value = price
        _stock.value = stock
        _editEnable.value = isValidName(name) && isValidPrice(price) && isValidStock(stock)
    }

    suspend fun onEditSelected() {
        _isLoading.value = true
        delay(4000)
        _isLoading.value = false
    }

    fun isValidName(name: String): Boolean {
        return name.isNotEmpty()
    }

    fun isValidPrice(price: String): Boolean {
        return price.toFloatOrNull() != null && price.toFloat() >= 0
    }

    fun isValidStock(stock: String): Boolean {
        return stock.toIntOrNull() != null && stock.toInt() >= 0
    }


    fun updateProduct() {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("products").update(
                    {
                        set("name", _name.value)
                        set("price", _price.value)
                        set("stock", _stock.value)
                    }
                ) {
                    filter {
                        eq("id", id)
                    }
                }
                _editState.value = EditState.Success("Actualizacion exitosa")
            } catch (e: Exception) {
                e.printStackTrace()
                _editState.value = EditState.Error("Error al actualizar")
            }
        }
    }
}