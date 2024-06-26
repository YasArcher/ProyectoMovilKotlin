package ec.yasuodev.proyecto_movil.ui.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val id: String,
    val name: String,
    val owner: String,
    val business_image: String
)