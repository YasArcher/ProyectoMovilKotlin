package ec.yasuodev.proyecto_movil.ui.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Manager(
    val id: String,
    val id_store: String,
    val id_user: String
)