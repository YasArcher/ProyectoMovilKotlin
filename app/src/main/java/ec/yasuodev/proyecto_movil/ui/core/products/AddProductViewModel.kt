package ec.yasuodev.proyecto_movil.ui.core.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class AddProductViewModel : ViewModel() {
    var store = ""
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
    private val _addState = MutableLiveData<AddState>()
    val addState: LiveData<AddState> = _addState

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

    fun onAddChanged(name: String, price: String, stock: String) {
        _name.value = name
        _price.value = price
        _stock.value = stock
        _editEnable.value = isValidName(name) && isValidPrice(price) && isValidStock(stock)
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

    fun addProduct() {
        viewModelScope.launch {
            val product = Product(
                id = generateUUID(),
                name = name.value!!,
                store = store,
                price = price.value!!.toFloat(),
                stock = stock.value!!.toInt()
            )
            try {
                SupabaseClient.client.from("products").insert(
                    product
                )
                _addState.value = AddState.Success("Producto agregado")
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al agregar producto")
            }
        }
    }

    suspend fun onAddSelected() {
        _isLoading.value = true
        delay(4000)
        _isLoading.value = false
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}