package ec.yasuodev.proyecto_movil.ui.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class User (
    val id:String,
    val name: String,
    val lastName: String,
    val email: String,
    val nickName: String,
    val image: String
)