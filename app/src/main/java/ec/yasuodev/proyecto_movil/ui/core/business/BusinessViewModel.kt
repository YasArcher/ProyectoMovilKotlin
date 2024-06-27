package ec.yasuodev.proyecto_movil.ui.core.business

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.shared.models.AuxiliarSaleProduct
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.Purchase
import ec.yasuodev.proyecto_movil.ui.shared.models.Sale
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.shared.models.generateUUID
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BusinessViewModel: ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _store = MutableLiveData<Store>()
    val store: LiveData<Store> = _store
    private val _income = MutableLiveData<Double>(0.0)
    val income: LiveData<Double> = _income
    private val _expenditures= MutableLiveData<Double>(0.0)
    val expenditures: LiveData<Double> = _expenditures
    private val _salesList = MutableLiveData<List<Sale>>(emptyList())
    val salesList: LiveData<List<Sale>> = _salesList
    private val _purchasesList = MutableLiveData<List<Purchase>>(emptyList())
    val purchasesList: LiveData<List<Purchase>> = _purchasesList
    private val _productsModel = MutableLiveData<List<AuxiliarSaleProduct>>(emptyList())
    val productsModel: LiveData<List<AuxiliarSaleProduct>> = _productsModel
    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products
    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog
    private val _filteredProducts = MutableLiveData<List<Product>>(emptyList())
    val filteredProducts: LiveData<List<Product>> = _filteredProducts
    private val _addState = MutableLiveData<AddState>()
    val addState: LiveData<AddState> = _addState

    fun filterProducts(query: String) {
        val products = _products.value ?: return
        _filteredProducts.value = if (query.isEmpty()) {
            products
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
    fun showDialog(show: Boolean) {
        _showDialog.value = show
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchBusiness(id: String ) {
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
                        eq("id", id)
                    }
                }.decodeSingle<Store>()
                _store.value = response
            } catch (e: Exception) {
                println(e)
            }
        }
        fetchProducts(id)
    }
    suspend fun onAddSelected() {
        _isLoading.value = true
        delay(4000)
        _isLoading.value = false
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchProducts(store_id: String){
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
                        eq("store", store_id)
                    }
                }.decodeList<Product>()
                _products.value = response
                getSalesByDate(store_id, java.time.LocalDate.now().toString())
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addSaleProduct(product: Product, quantity: Int, seled_by: String){
        viewModelScope.launch {
            val date = java.time.LocalDate.now().toString()
            try {
                val sale = Sale(
                    id = generateUUID(),
                    created_at = date ,
                    total = (product.price * quantity).toDouble(),
                    product = product.id,
                    quantity = quantity,
                    id_business = product.store,
                    seled_by = seled_by
                )
                SupabaseClient.client.from("sales").insert(
                    sale
                )
                val updatedSalesList = _salesList.value?.toMutableList() ?: mutableListOf()
                updatedSalesList.add(sale)
                _salesList.value = updatedSalesList
                makeAuxiliarSaleProduct()
                _addState.value = AddState.Success("Producto vendido")
            }catch (e: Exception){
                _addState.value = AddState.Error("Error al vender producto")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addPurchase(amount: Double, reason: String, store_id: String){
        viewModelScope.launch {
            try {
                val purchase = Purchase(
                    id = generateUUID(),
                    created_at = java.time.LocalDate.now().toString(),
                    business_id = store_id,
                    amount = amount,
                    reason = reason
                )
                SupabaseClient.client.from("purchases").insert(
                    purchase
                )
                val updatedPurchasesList = _purchasesList.value?.toMutableList() ?: mutableListOf()
                updatedPurchasesList.add(purchase)
                _purchasesList.value = updatedPurchasesList
                _addState.value = AddState.Success("Gasto registrado")
                getExpendituresByDate(store_id, java.time.LocalDate.now().toString())
            }catch (e: Exception){
                _addState.value = AddState.Error("Error al registrar gasto")
            }
        }
    }

    private fun getSalesByDate(store_id: String, date: String){
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
                        eq("id_business", store_id)
                    }
                }.decodeList<Sale>()
                val salesList = mutableListOf<Sale>()
                var totalIncome = 0.0

                response.forEach {
                    salesList.add(it)
                    totalIncome += it.total
                }
                _salesList.value = salesList
                _income.value = totalIncome
                makeAuxiliarSaleProduct()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    fun getExpendituresByDate(store_id: String, date: String){
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("purchases").select(
                    columns = Columns.list(
                        "id",
                        "created_at",
                        "business_id",
                        "amount",
                        "reason"
                    )
                ) {
                    filter {
                        eq("created_at", date)
                        eq("business_id", store_id)
                    }
                }.decodeList<Purchase>()
                val expendituresList = mutableListOf<Purchase>()
                var totalExpenditures = 0.0
                response.forEach {
                    expendituresList.add(it)
                    totalExpenditures += it.amount
                }
                _purchasesList.value = expendituresList
                _expenditures.value = totalExpenditures
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    private fun makeAuxiliarSaleProduct() {
        val products = _products.value
        val sales = _salesList.value
        val auxiliarSaleProductList = mutableListOf<AuxiliarSaleProduct>()

        if (products != null && sales != null) {
            sales.forEach { sale ->
                val product = products.find { it.id == sale.product }
                if (product != null) {
                    val auxiliarSaleProduct = AuxiliarSaleProduct(
                        id = sale.id,
                        product = sale.product,
                        productName = product.name,
                        quantity = sale.quantity,
                        total = sale.total,
                        id_business = sale.id_business,
                        seled_by = sale.seled_by,
                        productPrice = product.price,
                        productStock = product.stock
                    )
                    auxiliarSaleProductList.add(auxiliarSaleProduct)
                }
            }
        }
        _productsModel.value = auxiliarSaleProductList
    }
}