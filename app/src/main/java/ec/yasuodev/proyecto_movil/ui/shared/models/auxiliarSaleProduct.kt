package ec.yasuodev.proyecto_movil.ui.shared.models

data class AuxiliarSaleProduct (
    var id: String = generateUUID(),
    var total: Double = 0.0,
    var product: String = "",
    var productName: String = "",
    var quantity: Int = 0,
    var id_business: String = "",
    var seled_by: String = "",
    var productPrice : Double = 0.0,
    var productStock : Int = 0,
    var created_at: String = ""
)

fun generateUUID(): String {
    return java.util.UUID.randomUUID().toString()
}