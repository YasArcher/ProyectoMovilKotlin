package ec.yasuodev.proyecto_movil.ui.core.profile

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.core.models.EditState
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class EditProfileViewModel : ViewModel() {
    private val _editState = MutableLiveData<EditState>()
    val editState: LiveData<EditState> = _editState
    var id = ""
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name
    private val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String> = _lastName
    private val _nickName = MutableLiveData<String>()
    val nickName: LiveData<String> = _nickName
    private val _image = MutableLiveData<String>()
    val image: LiveData<String> = _image
    private val _editEnable = MutableLiveData<Boolean>()
    val editEnable: LiveData<Boolean> = _editEnable
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _selectedImageUri = MutableLiveData<Uri>()
    val selectedImageUri: LiveData<Uri> = _selectedImageUri

    fun onEditMessageName(name: String? = null): String {
        return if (name.isNullOrEmpty()) {
            "Ingrese un nombre"
        } else {
            "Nombre válido"
        }
    }

    fun imageExist(image: String): String{
        _image.value = image
        return image
    }

    fun onEditMessageLastName(lastName: String? = null): String {
        return if (lastName.isNullOrEmpty()) {
            "Ingrese un apellido"
        } else {
            "Apellido válido"
        }
    }

    fun onEditMessageNickName(nickName: String? = null): String {
        return if (nickName.isNullOrEmpty()) {
            "Ingrese un nombre de usuario"
        } else {
            "Nombre de usuario válido"
        }
    }

    fun onEditChanged(name: String, lastName: String, nickName: String, selectedImageUri: Uri) {
        _name.value = name
        _lastName.value = lastName
        _nickName.value = nickName
        _selectedImageUri.value = selectedImageUri
        validateForm()
    }

    private fun validateForm() {
        _editEnable.value = !(_name.value.isNullOrEmpty() || _lastName.value.isNullOrEmpty() || _nickName.value.isNullOrEmpty())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUser(imageBytes: ByteArray = ByteArray(0)) {
        val imageName = if (_selectedImageUri.value == Uri.EMPTY) {
            _image.value
        } else {
            LocalDateTime.now().run {
                year.toString() +
                        monthValue.toString().padStart(2, '0') +
                        dayOfMonth.toString().padStart(2, '0') +
                        hour.toString().padStart(2, '0') +
                        minute.toString().padStart(2, '0') +
                        second.toString().padStart(2, '0')
            }
        }
        val bucket = SupabaseClient.client.storage.from("storeApp")
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("users").update(
                    {
                        set("name", _name.value)
                        set("lastName", _lastName.value)
                        set("nickName", _nickName.value)
                        set("image", imageName)
                    }
                ) {
                    filter {
                        eq("id", id)
                    }
                }
                if (imageBytes.isNotEmpty()) {
                    if (_image.value != "noImage") {
                        _selectedImageUri.value?.let {
                            bucket.delete("users/${_image.value}.jpg")
                            bucket.upload("users/${imageName}.jpg", imageBytes, upsert = false)
                            _image.value = imageName!!
                        }
                    } else {
                        _selectedImageUri.value?.let {
                            bucket.upload("users/${id}.jpg", imageBytes, upsert = false)
                        }
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

    fun onImageSelected(it: Uri) {
        _selectedImageUri.value = it
        validateForm()
    }
}
