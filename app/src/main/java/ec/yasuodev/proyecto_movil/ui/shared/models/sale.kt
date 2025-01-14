package ec.yasuodev.proyecto_movil.ui.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Sale(
    val id: String,            // UUID de la venta
    val created_at: String,    // Fecha de creación
    val product: String,       // UUID del producto
    val quantity: Int,         // Cantidad vendida
    val id_business: String,   // UUID del negocio
    val seled_by: String,      // UUID del vendedor
    val invoice_id: String?,   // UUID de la factura (puede ser nulo)
    val state: String,         // Estado de la venta (e.g., "pagado")
    val total: Double,         // Total de la venta
)