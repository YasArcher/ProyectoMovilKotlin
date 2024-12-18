package ec.yasuodev.proyecto_movil.ui.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Invoice(
    val id: String,
    val client: String,
    val business: String,
    val value: Double,
)