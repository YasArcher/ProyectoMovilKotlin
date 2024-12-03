package ec.yasuodev.proyecto_movil.ui.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val store: String,
    val price: Double,
    val stock: Int,
    val imageUrl: String? = null
)

