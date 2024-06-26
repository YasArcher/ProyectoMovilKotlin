package ec.yasuodev.proyecto_movil.ui.core.business

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
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class BusinessViewModel: ViewModel() {
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
    private val _showDialogSale = MutableLiveData<Boolean>()
    val showDialogSale: LiveData<Boolean> = _showDialogSale
    private val _showDialogPurchase = MutableLiveData<Boolean>()
    val showDialogPurchase: LiveData<Boolean> = _showDialogPurchase

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
    fun showDialogSale(show: Boolean) {
        _showDialogSale.value = show
    }
    fun showDialogPurchase(show: Boolean) {
        _showDialogPurchase.value = show
    }

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
                makeAuxiliarSaleProduct()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    fun addSaleProduct(product: Product, quantity: Int){
        viewModelScope.launch {
            try {


            }catch (e: Exception){
                println(e)
            }
        }
    }

    fun getSalesByDate(store_id: String, date: String){
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
                        "reason",
                        "document_image"
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