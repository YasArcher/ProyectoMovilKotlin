package ec.yasuodev.proyecto_movil.ui.core.products

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.ProductCategory
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
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

    private val _category = MutableLiveData<String>()
    val category: LiveData<String> = _category

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _editEnable = MutableLiveData<Boolean>()
    val editEnable: LiveData<Boolean> = _editEnable

    private val _addState = MutableLiveData<AddState>()
    val addState: LiveData<AddState> = _addState

    private val _categories = MutableLiveData<List<ProductCategory>>(emptyList())
    val categories: LiveData<List<ProductCategory>> = _categories

    fun onEditMessageName(name: String? = null): String {
        return if (name.isNullOrEmpty()) "Ingrese un nombre" else "Nombre válido"
    }

    fun onEditMessagePrice(price: String? = null): String {
        return if (price.isNullOrEmpty()) "Ingrese un precio" else "Precio válido"
    }

    fun onEditMessageStock(stock: String? = null): String {
        return if (stock.isNullOrEmpty()) "Ingrese un stock" else "Stock válido"
    }

    fun onEditMessageCategory(category: String? = null): String {
        return if (category.isNullOrEmpty()) "Ingrese una categoría" else "Categoría válida"
    }

    fun onAddChanged(name: String, price: String, stock: String, category: String) {
        _name.value = name
        _price.value = price
        _stock.value = stock
        _category.value = category
        // Si quieres que la categoría sea obligatoria, inclúyela en la validación:
        _editEnable.value = (
                isValidName(name) &&
                        isValidPrice(price) &&
                        isValidStock(stock) &&
                        isValidCategory(category)
                )
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

    fun isValidCategory(category: String): Boolean {
        return category.isNotEmpty()
    }

    fun addProduct() {
        viewModelScope.launch {
            val product = Product(
                id = generateUUID(),
                name = name.value!!,
                store = store,
                price = price.value!!.toDouble(),
                stock = stock.value!!.toInt(),
                category = category.value!!
            )
            try {
                val insertedProducts = SupabaseClient.client
                    .postgrest["products"]
                    .insert(product)
                    .decodeList<Product>()

                if (insertedProducts.isNotEmpty()) {
                    _addState.value = AddState.Success("Producto agregado correctamente")
                } else {
                    _addState.value = AddState.Error("No se devolvió ningún registro insertado.")
                }
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al agregar producto: ${e.message}")
                Log.e("AddProductViewModel", "Error al agregar producto", e)
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val result = SupabaseClient.client
                    .postgrest["product_category"]
                    .select()
                    .decodeList<ProductCategory>()
                Log.d("AddProductViewModel", "fetchCategories => $result")
                _categories.value = result
            } catch (e: Exception) {
                Log.e("AddProductViewModel", "Error al obtener categorías", e)
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
