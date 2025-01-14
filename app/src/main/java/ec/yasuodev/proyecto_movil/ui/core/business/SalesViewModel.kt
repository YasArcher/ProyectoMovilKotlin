package ec.yasuodev.proyecto_movil.ui.core.business

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
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
import ec.yasuodev.proyecto_movil.ui.shared.models.SaleTable
import ec.yasuodev.proyecto_movil.ui.shared.models.generateUUID
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import ec.yasuodev.proyecto_movil.ui.shared.models.Product
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.shared.models.toSale
import kotlinx.coroutines.delay

class SalesViewModel(private val context: Context) : ViewModel() {
    private val _productsModel = MutableLiveData<List<AuxiliarSaleProduct>>(emptyList())
    val productsModel: LiveData<List<AuxiliarSaleProduct>> = _productsModel
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products
    private val _store = MutableLiveData<Store>()
    val store: LiveData<Store> = _store
    private val _income = MutableLiveData<Double>(0.0)
    val income: LiveData<Double> = _income
    private val _filteredProducts = MutableLiveData<List<Product>>(emptyList())
    val filteredProducts: LiveData<List<Product>> = _filteredProducts
    private val _salesList = MutableLiveData<List<SaleTable>>(emptyList())
    val salesList: LiveData<List<SaleTable>> = _salesList
    private val _addState = MutableLiveData<AddState>()
    val addState: LiveData<AddState> = _addState
    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog
    private val _invoiceId = MutableLiveData<String?>()
    val invoiceId: MutableLiveData<String?> = _invoiceId

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

    fun showDialog(show: Boolean) {
        _showDialog.value = show
    }


    // Filtra productos por nombre y stock
    fun filterProducts(query: String) {
        val products = _products.value ?: return
        _filteredProducts.value = if (query.isEmpty()) {
            products.filter { it.stock > 0 }
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) && it.stock > 0 }
        }
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
                Log.d("ERROR", "Mensaje de depuración: Error al obtener la tienda")
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
    private fun fetchProducts(store_id: String) {
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
                        eq("store", store_id)
                    }
                }.decodeList<Product>()
                _products.value = response
            } catch (e: Exception) {
                Log.d("ERROR", "Mensaje de depuración: Error $e")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addProductToTable(product: Product, quantity: Int, seled_by: String) {
        Log.d("INFO", "Añadiendo producto a la tabla")
        if (_invoiceId.value.isNullOrEmpty()) {
            _invoiceId.value = generateUUID()
        }
        viewModelScope.launch {
            try {
                val currentSales = _salesList.value?.toMutableList() ?: mutableListOf()

                // Verificar si el producto ya existe en la tabla
                val existingSale = currentSales.find { it.product == product.id }

                if (existingSale != null) {
                    // Si existe, actualizar cantidad y total
                    val updatedQuantity = existingSale.quantity + quantity
                    val updatedTotal = updatedQuantity * product.price
                    val updatedSale =
                        existingSale.copy(quantity = updatedQuantity, total = updatedTotal)

                    // Reemplazar la venta existente
                    val index = currentSales.indexOf(existingSale)
                    currentSales[index] = updatedSale
                } else {
                    // Si no existe, agregarlo como nueva venta
                    val newSale = SaleTable(
                        id = generateUUID(),
                        created_at = java.time.LocalDate.now().toString(),
                        total = (product.price * quantity).toDouble(),
                        product = product.id,
                        quantity = quantity,
                        id_business = product.store,
                        seled_by = seled_by,
                        state = "pendiente", // Estado temporal
                        price = product.price,
                        invoice_id = _invoiceId.value.toString() // No tiene factura aún
                    )
                    currentSales.add(newSale)
                }

                // Actualizar la lista de ventas
                _salesList.value = currentSales

                // Calcular y actualizar el ingreso acumulado
                val totalIncome = currentSales.sumOf { it.total }
                _income.value = totalIncome

                // Actualizar la vista de productos auxiliares para la tabla
                makeAuxiliarSaleProduct()

                _addState.value = AddState.Success("Producto añadido/actualizado en la tabla")
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al añadir/actualizar producto en la tabla")
            } finally {
                resetAddState()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun confirmSales(seled_by: String) {
        try {
            // Crear la factura
            val invoice = Invoice(
                id = _invoiceId.value.toString(),
                client = seled_by,
                business = _store.value?.id.toString(),
                value = _salesList.value?.sumOf { it.total } ?: 0.0,
                create_at = java.time.LocalDate.now().toString()
            )

            try {
                SupabaseClient.client.from("invoices").insert(invoice)
                Log.d("DEBUG", "Factura creada e insertada en la base de datos: $invoice")
            } catch (e: Exception) {
                Log.d("ERROR", "Error al insertar la factura en la base de datos: ${e.message}")
                _addState.value = AddState.Error("Error al confirmar la factura")
                return
            }

            viewModelScope.launch {
                try {
                    val salesToConfirm = _salesList.value ?: emptyList()
                    if (salesToConfirm.isEmpty()) {
                        Log.d("DEBUG", "No hay productos en la lista de ventas para confirmar")
                        _addState.value = AddState.Error("No hay productos para confirmar la venta")
                        return@launch
                    }

                    salesToConfirm.forEach { saleTable ->
                        try {
                            // Convertir SaleTable a Sale
                            val sale = saleTable.toSale()

                            // Insertar la venta en la base de datos
                            SupabaseClient.client.from("sales").insert(sale)
                            Log.d("DEBUG", "Venta insertada en la base de datos: $sale")

                            try {
                                // Actualizar el stock del producto en la base de datos
                                val product = _products.value?.find { it.id == sale.product }
                                product?.let {
                                    val updatedStock = it.stock - sale.quantity
                                    val updatedProduct = it.copy(stock = updatedStock)

                                    SupabaseClient.client.from("products").update(updatedProduct) {
                                        filter {
                                            eq("id", it.id)
                                        }
                                    }
                                    Log.d(
                                        "DEBUG",
                                        "Producto actualizado: ${updatedProduct.id}, stock actualizado a $updatedStock"
                                    )

                                    // Mostrar notificación si el stock es bajo
                                    if (updatedStock <= 10) {
                                        showLowStockNotification(it.name)
                                        Log.d(
                                            "DEBUG",
                                            "Notificación de stock bajo enviada para: ${it.name}"
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                Log.d(
                                    "ERROR",
                                    "Error al actualizar el stock del producto (${sale.product}): ${e.message}"
                                )
                            }
                        } catch (e: Exception) {
                            Log.d(
                                "ERROR",
                                "Error al insertar la venta en la base de datos: ${e.message}"
                            )
                        }
                    }

                    // Limpiar la lista de ventas y actualizar la UI
                    try {
                        _salesList.value = emptyList()
                        makeAuxiliarSaleProduct()
                        Log.d("DEBUG", "Lista de ventas limpiada y vista auxiliar actualizada")
                    } catch (e: Exception) {
                        Log.d("ERROR", "Error al limpiar la lista de ventas: ${e.message}")
                    }

                    _addState.value = AddState.Success("Venta confirmada con éxito")
                    Log.d("DEBUG", "Venta confirmada con éxito")
                } catch (e: Exception) {
                    Log.d("ERROR", "Error en el proceso de confirmación de ventas: ${e.message}")
                    _addState.value = AddState.Error("Error al confirmar la venta")
                } finally {
                    resetAddState()
                    _invoiceId.value = null
                }
            }
        } catch (e: Exception) {
            Log.d("ERROR", "Error inesperado en confirmSales: ${e.message}")
            _addState.value = AddState.Error("Error inesperado al confirmar la venta")
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

    private fun resetAddState() {
        viewModelScope.launch {
            delay(2000)
            _addState.value = AddState.Loading
        }
    }

    fun removeProductFromTable(productId: String) {
        viewModelScope.launch {
            try {
                // Filtrar la lista actual de ventas para excluir el producto a eliminar
                val updatedSales =
                    _salesList.value?.filter { it.product != productId } ?: emptyList()

                // Actualizar la lista de ventas y recalcular el ingreso
                _salesList.value = updatedSales
                _income.value = updatedSales.sumOf { it.total }

                // Actualizar la vista de productos auxiliares para la tabla
                makeAuxiliarSaleProduct()

                _addState.value = AddState.Success("Producto eliminado de la tabla")
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al eliminar producto de la tabla")
            } finally {
                resetAddState()
            }
        }
    }

    fun editProductInTable(productId: String, newQuantity: Int) {
        viewModelScope.launch {
            try {
                val currentSales = _salesList.value?.toMutableList() ?: mutableListOf()
                val saleToEdit = currentSales.find { it.product == productId }

                if (saleToEdit != null) {
                    val updatedSale = saleToEdit.copy(
                        quantity = newQuantity,
                        total = saleToEdit.price * newQuantity
                    )
                    currentSales[currentSales.indexOf(saleToEdit)] = updatedSale
                    _salesList.value = currentSales

                    // Recalcular ingreso total
                    _income.value = currentSales.sumOf { it.total }

                    // Actualizar la vista de productos auxiliares para la tabla
                    makeAuxiliarSaleProduct()

                    _addState.value = AddState.Success("Producto actualizado en la tabla")
                }
            } catch (e: Exception) {
                _addState.value = AddState.Error("Error al editar producto en la tabla")
            } finally {
                resetAddState()
            }
        }
    }
}