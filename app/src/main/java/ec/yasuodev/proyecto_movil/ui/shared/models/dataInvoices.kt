package ec.yasuodev.proyecto_movil.ui.shared.models

data class dataInvoices(
    var id: String = generateUUID(),
    var client : String = "",
    var business_id : String = "",
    var value : Double = 0.0,
    var create_at : String = "",
)