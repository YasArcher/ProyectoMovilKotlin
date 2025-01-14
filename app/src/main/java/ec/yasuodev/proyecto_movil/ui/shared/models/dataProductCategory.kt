package ec.yasuodev.proyecto_movil.ui.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class ProductCategory(
    val id: String,            // UUID en la DB
    val category_name: String
)
