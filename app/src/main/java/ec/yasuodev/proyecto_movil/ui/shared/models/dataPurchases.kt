package ec.yasuodev.proyecto_movil.ui.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Purchase(
    val id: String,
    val reason: String,
    val created_at: String,
    val business_id: String,
    val amount: Double
)