package ec.yasuodev.proyecto_movil.ui.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class User (
    val id:String,
    val name: String,
    val lastname: String,
    val email: String,
    val nickname: String,
    val image: String,
    val rol: String? = null
)