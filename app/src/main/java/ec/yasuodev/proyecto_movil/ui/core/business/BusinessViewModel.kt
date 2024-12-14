package ec.yasuodev.proyecto_movil.ui.core.business

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.R
import ec.yasuodev.proyecto_movil.ui.core.models.AddState
import ec.yasuodev.proyecto_movil.ui.shared.models.AuxiliarSaleProduct
import ec.yasuodev.proyecto_movil.ui.shared.models.Invoice
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

open class BusinessViewModel(private val context: Context) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _store = MutableLiveData<Store>()
    open val store: LiveData<Store> = _store
    private val _income = MutableLiveData<Double>(0.0)
    open val income: LiveData<Double> = _income
    private val _expenditures = MutableLiveData<Double>(0.0)
    open val expenditures: LiveData<Double> = _expenditures
    private val _salesList = MutableLiveData<List<Sale>>(emptyList())
    val salesList: LiveData<List<Sale>> = _salesList
    private val _invoiceList = MutableLiveData<List<Invoice>>(emptyList())
    val invoiceList: LiveData<List<Invoice>> = _invoiceList
    private val _purchasesList = MutableLiveData<List<Purchase>>(emptyList())
    open val purchasesList: LiveData<List<Purchase>> = _purchasesList
    private val _productsModel = MutableLiveData<List<AuxiliarSaleProduct>>(emptyList())
    open val productsModel: LiveData<List<AuxiliarSaleProduct>> = _productsModel
    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products
    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog
    private val _filteredProducts = MutableLiveData<List<Product>>(emptyList())
    val filteredProducts: LiveData<List<Product>> = _filteredProducts
    private val _addState = MutableLiveData<AddState>()
    val addState: LiveData<AddState> = _addState

    private fun showLowStockNotification(productName: String) {
        val channelId = "low_stock_channel"
        val channelName = "Low Stock Alerts"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.visibility_24px)
            .setContentTitle("Stock bajo")
            .setContentText("El stock del producto $productName es de 10 o menos.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(context).notify(1, notification)
    }

    fun filterProducts(query: String) {
        val products = _products.value ?: return
        _filteredProducts.value = if (query.isEmpty()) {
            products.filter { it.stock > 0 }
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) && it.stock > 0 }
        }
    }

    fun showDialog(show: Boolean) {
        _showDialog.value = show
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchBusiness(id: String) {
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
        //fetchProducts(id)
        //getSalesByDate(id, java.time.LocalDate.now().toString())
    }

    suspend fun onAddSelected() {
        _isLoading.value = true
        delay(4000)
        _isLoading.value = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchProducts(store_id: String) {
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
                //getSalesByDate(store_id, java.time.LocalDate.now().toString())
                getInvoicesByDate(store_id, java.time.LocalDate.now().toString())
            } catch (e: Exception) {
                println(e)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun addSaleProduct(product: Product, quantity: Int, seled_by: String) {
        viewModelScope.launch {
            val date = java.time.LocalDate.now().toString()
            try {
                val sale = Sale(
                    id = generateUUID(),
                    created_at = date,
                    total = (product.price * quantity).toDouble(),
                    product = product.id,
                    quantity = quantity,
                    id_business = product.store,
                    seled_by = seled_by,
                    state = "completed",
                    invoice_id = "",
                    price = product.price
                )
                SupabaseClient.client.from("sales").insert(sale)

                val newStock = product.stock - quantity
                val updatedProduct = product.copy(stock = newStock)
                SupabaseClient.client.from("products").update(updatedProduct) {
                    filter {
                        eq("id", product.id)
                    }
                }

                if (newStock <= 10) {
                    showLowStockNotification(product.name)
                }

                val updatedSalesList = _salesList.value?.toMutableList() ?: mutableListOf()
                updatedSalesList.add(sale)
                _salesList.value = updatedSalesList

                val updatedProducts = _products.value?.map {
                    if (it.id == product.id) updatedProduct else it
                }
                _products.value = updatedProducts!!

                // Actualizar ingresos
                _income.value = (_income.value ?: 0.0) + sale.total

                makeAuxiliarSaleProduct()
                _addState.value = AddState.Success("Producto vendido")
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al vender producto")
            } finally {
                resetAddState()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addPurchase(amount: Double, reason: String, store_id: String) {
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

                // Actualizar egresos
                _expenditures.value = (_expenditures.value ?: 0.0) + amount

                _addState.value = AddState.Success("Gasto registrado")
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al registrar gasto")
            } finally {
                resetAddState()
            }
        }
    }

    fun deleteSaleProduct(saleId: String) {
        viewModelScope.launch {
            try {

                val saleToDelete = _salesList.value?.find { it.id == saleId }

                SupabaseClient.client.from("sales").delete {
                    filter {
                        eq("id", saleId)
                    }
                }

                saleToDelete?.let { sale ->
                    val product = _products.value?.find { it.id == sale.product }
                    product?.let {
                        val updatedProduct = it.copy(stock = it.stock + sale.quantity)
                        SupabaseClient.client.from("products").update(updatedProduct) {
                            filter {
                                eq("id", it.id)
                            }
                        }

                        val updatedProducts = _products.value?.map { p ->
                            if (p.id == it.id) updatedProduct else p
                        }
                        _products.value = updatedProducts!!
                    }
                }

                val updatedSalesList = _salesList.value?.toMutableList() ?: mutableListOf()
                updatedSalesList.removeAll { it.id == saleId }
                _salesList.value = updatedSalesList

                val newIncome = updatedSalesList.sumOf { it.total }
                _income.value = newIncome

                makeAuxiliarSaleProduct()
                _addState.value = AddState.Success("Venta eliminada")
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al eliminar venta")
            } finally {
                resetAddState()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSaleProduct(sale: Sale) {
        viewModelScope.launch {
            try {
                val originalSale = _salesList.value?.find { it.id == sale.id }
                val difference = sale.quantity - (originalSale?.quantity ?: 0)
                val totalDifference = (sale.total - (originalSale?.total ?: 0.0))

                SupabaseClient.client.from("sales").update(sale) {
                    filter {
                        eq("id", sale.id)
                    }
                }

                val product = _products.value?.find { it.id == sale.product }
                product?.let {
                    val newStock = it.stock - difference
                    val updatedProduct = it.copy(stock = newStock)
                    SupabaseClient.client.from("products").update(updatedProduct) {
                        filter {
                            eq("id", it.id)
                        }
                    }

                    val updatedProducts = _products.value?.map { p ->
                        if (p.id == it.id) updatedProduct else p
                    }
                    _products.value = updatedProducts!!
                }

                val updatedSalesList = _salesList.value?.toMutableList() ?: mutableListOf()
                val index = updatedSalesList.indexOfFirst { it.id == sale.id }
                updatedSalesList[index] = sale
                _salesList.value = updatedSalesList

                _income.value = (_income.value ?: 0.0) + totalDifference

                makeAuxiliarSaleProduct()
                _addState.value = AddState.Success("Venta actualizada")
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al actualizar venta")
            } finally {
                resetAddState()
            }
        }
    }

    fun getInvoicesByDate(store_id: String, date: String) {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("invoices").select(
                    columns = Columns.list(
                        "id",
                        "client",
                        "business_id",
                        "value",
                        "create_at"
                    )
                ) {
                    filter {
                        eq("create_at", date)
                        eq("business_id", store_id)
                    }
                }.decodeList<Invoice>()
                val invoicesList = mutableListOf<Invoice>()
                var totalIncome = 0.0

                response.forEach {
                    invoicesList.add(it)
                    totalIncome += it.value
                }
                _invoiceList.value = invoicesList
                _income.value = totalIncome
                makeAuxiliarSaleProduct()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    private fun getSalesByDate(store_id: String, date: String) {
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

    fun getExpendituresByDate(store_id: String, date: String) {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("purchases").select(
                    columns = Columns.list(
                        "id",
                        "create_at",
                        "business_id",
                        "amount",
                        "reason"
                    )
                ) {
                    filter {
                        eq("create_at", date)
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
                        productStock = product.stock,
                        created_at = sale.created_at
                    )
                    auxiliarSaleProductList.add(auxiliarSaleProduct)
                }
            }
        }
        _productsModel.value = auxiliarSaleProductList
    }

    fun updatePurchase(purchase: Purchase) {
        viewModelScope.launch {
            try {
                val originalPurchase = _purchasesList.value?.find { it.id == purchase.id }
                val difference = purchase.amount - (originalPurchase?.amount ?: 0.0)

                SupabaseClient.client.from("purchases").update(purchase) {
                    filter {
                        eq("id", purchase.id)
                    }
                }
                val updatedPurchasesList = _purchasesList.value?.toMutableList() ?: mutableListOf()
                val index = updatedPurchasesList.indexOfFirst { it.id == purchase.id }
                if (index != -1) {
                    updatedPurchasesList[index] = purchase
                    _purchasesList.value = updatedPurchasesList
                }

                // Actualizar egresos
                _expenditures.value = (_expenditures.value ?: 0.0) + difference

                _addState.value = AddState.Success("Egreso actualizado")
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al actualizar egreso")
            } finally {
                resetAddState()
            }
        }
    }

    fun deletePurchase(purchaseId: String) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("purchases").delete {
                    filter {
                        eq("id", purchaseId)
                    }
                }
                val updatedPurchasesList = _purchasesList.value?.toMutableList() ?: mutableListOf()
                updatedPurchasesList.removeAll { it.id == purchaseId }
                _purchasesList.value = updatedPurchasesList

                // Recalcular egresos
                val newExpenditures = updatedPurchasesList.sumOf { it.amount }
                _expenditures.value = newExpenditures

                _addState.value = AddState.Success("Egreso eliminado")
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al eliminar egreso")
            } finally {
                resetAddState()
            }
        }
    }

    private fun resetAddState() {
        viewModelScope.launch {
            delay(2000)
            _addState.value = AddState.Loading
        }
    }
}