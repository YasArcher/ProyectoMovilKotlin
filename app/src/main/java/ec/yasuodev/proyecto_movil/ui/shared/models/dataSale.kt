package ec.yasuodev.proyecto_movil.ui.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class SaleTable(
    val id: String,
    val total: Double,
    val product: String,
    val quantity: Int,
    val created_at: String,
    val id_business: String,
    val seled_by: String,
    val state: String,
    val price: Double,
    val invoice_id: String
)
fun SaleTable.toSale(): Sale {
    return Sale(
        id = this.id,
        created_at = this.created_at,
        product = this.product,
        quantity = this.quantity,
        id_business = this.id_business,
        seled_by = this.seled_by,
        invoice_id = this.invoice_id.takeIf { it.isNotEmpty() }, // Asegurarse de manejar nulos
        state = this.state,
        total = this.total
    )
}
