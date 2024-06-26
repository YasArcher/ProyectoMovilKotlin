package ec.yasuodev.proyecto_movil.ui.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Sale(
    val id: String,
    val total: Double,
    val product: String,
    val quantity: Int,
    val id_business: String,
    val seled_by: String
)