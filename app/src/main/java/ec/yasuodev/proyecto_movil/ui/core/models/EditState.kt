package ec.yasuodev.proyecto_movil.ui.core.models

sealed class EditState {
    data object Loading : EditState()
    data class Success(val message:String) : EditState()
    data class Error(val message:String) : EditState()
}