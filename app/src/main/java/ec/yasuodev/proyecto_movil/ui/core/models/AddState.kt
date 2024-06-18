package ec.yasuodev.proyecto_movil.ui.core.models

sealed class AddState {
    data object Loading : AddState()
    data class Success(val message: String) : AddState()
    data class Error(val message: String) : AddState()
}