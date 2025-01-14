package ec.yasuodev.proyecto_movil.ui.core.products

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenDecoding
import ec.yasuodev.proyecto_movil.ui.auth.utils.TokenManager
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class ProductsViewModel: ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    private val _store = MutableLiveData<Store>()
    val store: LiveData<Store> = _store

    fun fetchStore(context: Context) {
        val token = TokenManager.getToken(context)
        val userID = TokenDecoding.decodeJWT(token ?: "")["sub"] as? String ?: "No UserID found"
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
                        eq("owner", userID )
                    }
                }.decodeSingle<Store>()
                _store.value = response
                fetchProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("products").select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "store",
                        "price",
                        "stock",
                        "category"
                    )
                ) {
                    filter {
                        eq("store", _store.value?.id ?: "")
                    }
                }.decodeList<Product>()
                _products.value = response
                Log.d("ProductsViewModel", "Productos: ${_products.value?.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun deleteProduct(id: String) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("products").delete {
                    filter {
                        eq("id", id)
                    }
                }
                fetchProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}