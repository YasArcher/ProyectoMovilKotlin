package ec.yasuodev.proyecto_movil.ui.core.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.core.models.EditState
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EditProfileViewModel: ViewModel(){
    private val _editState = MutableLiveData<EditState>()
    val editState: LiveData<EditState> = _editState
    var id = ""
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name
    private val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String> = _lastName
    private val _nickName = MutableLiveData<String>()
    val nickName: LiveData<String> = _nickName
    private val _editEnable = MutableLiveData<Boolean>()
    val editEnable: LiveData<Boolean> = _editEnable
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun onEditMessageName(name: String? = null): String {
        if (name.isNullOrEmpty()) {
            return "Ingrese un nombre"
        }else{
            return "Nombre válido"
        }
    }
    fun onEditMessageLastName(lastName: String? = null): String {
        if (lastName.isNullOrEmpty()) {
            return "Ingrese un apellido"
        }else{
            return "Apellido válido"
        }
    }
    fun onEditMessageNickName(nickName: String? = null): String {
        if (nickName.isNullOrEmpty()) {
            return "Ingrese un nombre de usuario"
        }else{
            return "Nombre de usuario válido"
        }
    }
    fun onEditChanged(name: String, lastName: String, nickName: String) {
        _name.value = name
        _lastName.value = lastName
        _nickName.value = nickName
        _editEnable.value = name.isNotEmpty() && lastName.isNotEmpty() && nickName.isNotEmpty()
    }

    fun updateUser() {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.client.from("users").update(
                    {
                        set("name", _name.value)
                        set("lastName", _lastName.value)
                        set("nickName", _nickName.value)
                    }
                ) {
                    filter {
                        eq("id",  id)
                    }
                }
                _editState.value = EditState.Success("Actualizacion exitosa")
            } catch (e: Exception) {
                e.printStackTrace()
                _editState.value = EditState.Error("Error al actualizar")
            }
        }
    }

    suspend fun onEditSelected() {
        _isLoading.value = true
        delay(4000)
        _isLoading.value = false
    }
    fun isValidName(name: String): Boolean = name.isNotEmpty()
    fun isValidLastName(lastName: String): Boolean = lastName.isNotEmpty()
    fun isValidNickName(nickName: String): Boolean = nickName.isNotEmpty()
}