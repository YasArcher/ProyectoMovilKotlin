package ec.yasuodev.proyecto_movil.ui.core.business

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.Sale
import ec.yasuodev.proyecto_movil.ui.shared.models.generateUUID
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class SalesViewModel(private val context: Context) : ViewModel() {
    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _filteredProducts = MutableLiveData<List<Product>>(emptyList())
    val filteredProducts: LiveData<List<Product>> = _filteredProducts

    private val _salesList = MutableLiveData<List<Sale>>(emptyList())
    val salesList: LiveData<List<Sale>> = _salesList

    private val _addState = MutableLiveData<AddState>()
    val addState: LiveData<AddState> = _addState

    // Filtra productos por nombre y stock
    fun filterProducts(query: String) {
        val products = _products.value ?: return
        _filteredProducts.value = if (query.isEmpty()) {
            products.filter { it.stock > 0 }
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) && it.stock > 0 }
        }
    }

    // Obtiene los productos disponibles desde la base de datos
    fun fetchProducts(storeId: String) {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("products").select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "store",
                        "price",
                        "stock"
                    )
                ) {
                    filter {
                        eq("store", storeId)
                    }
                }.decodeList<Product>()
                _products.value = response
            } catch (e: Exception) {
                println("Error fetching products: ${e.message}")
            }
        }
    }

    // Agrega un producto como venta
    @RequiresApi(Build.VERSION_CODES.O)
    fun addSaleProduct(product: Product, quantity: Int, sellerId: String) {
        viewModelScope.launch {
            try {
                val sale = Sale(
                    id = generateUUID(),
                    created_at = java.time.LocalDate.now().toString(),
                    total = product.price * quantity,
                    product = product.id,
                    quantity = quantity,
                    id_business = product.store,
                    seled_by = sellerId
                )
                SupabaseClient.client.from("sales").insert(sale)

                // Actualiza el stock del producto
                val newStock = product.stock - quantity
                val updatedProduct = product.copy(stock = newStock)
                SupabaseClient.client.from("products").update(updatedProduct) {
                    filter { eq("id", product.id) }
                }

                // Actualiza listas locales
                val updatedSalesList = _salesList.value?.toMutableList() ?: mutableListOf()
                updatedSalesList.add(sale)
                _salesList.value = updatedSalesList

                val updatedProducts = _products.value?.map {
                    if (it.id == product.id) updatedProduct else it
                }
                _products.value = updatedProducts!!

                _addState.value = AddState.Success("Producto vendido con éxito")
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al agregar la venta: ${e.message}")
            }
        }
    }

    // Obtiene las ventas de un día específico
    fun getSalesByDate(storeId: String, date: String) {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("sales").select(
                    columns = Columns.list(
                        "id",
                        "created_at",
                        "total",
                        "product",
                        "quantity",
                        "id_business",
                        "seled_by"
                    )
                ) {
                    filter {
                        eq("created_at", date)
                        eq("id_business", storeId)
                    }
                }.decodeList<Sale>()
                _salesList.value = response
            } catch (e: Exception) {
                println("Error fetching sales: ${e.message}")
            }
        }
    }
}
