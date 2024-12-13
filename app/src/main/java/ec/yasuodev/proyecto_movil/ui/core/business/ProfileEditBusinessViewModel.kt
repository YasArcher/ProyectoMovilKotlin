package ec.yasuodev.proyecto_movil.ui.core.business

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.yasuodev.proyecto_movil.ui.shared.models.Store
import ec.yasuodev.proyecto_movil.ui.shared.models.User
import ec.yasuodev.proyecto_movil.ui.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.LocalDateTime

class ProfileEditBusinessViewModel(private val context: Context) : ViewModel() {
    private val _store = MutableStateFlow<Store?>(null)
    val store: StateFlow<Store?> get() = _store
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> get() = _selectedImageUri
    private val _owner = MutableStateFlow<User?>(null)
    val owner: StateFlow<User?> get() = _owner

    fun fetchBusiness(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = SupabaseClient.client.from("business").select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "owner",
                        "business_image"
                    )
                ) {
                    filter {
                        eq("id", id)
                    }
                }.decodeSingle<Store>()
                _store.value = response
            } catch (e: Exception) {
                println("Error fetching business: ${e.message}")
                _store.value = null // Manejar el caso de error.
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchOwner(id: String) {
        viewModelScope.launch {
            try {
                val owner = SupabaseClient.client.from("users").select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "lastname",
                        "email"
                    )
                ) {
                    filter {
                        eq("id", id)
                    }
                }.decodeSingle<User>()
                _owner.value = owner
            }catch (e: Exception) {
                println(e)
                _owner.value = null
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun uploadImage() {
        viewModelScope.launch {
            _selectedImageUri.value?.let { uri ->
                try {
                    val imageBytes = uriToByteArray(uri)
                    val imageName = "business_image_${System.currentTimeMillis()}.jpg"
                    val bucket = SupabaseClient.client.storage.from("storeApp")
                    bucket.upload("business/$imageName", imageBytes, upsert = true)
                    println("Image uploaded successfully: $imageName")
                } catch (e: Exception) {
                    println("Error uploading image: ${e.message}")
                }
            } ?: println("No image selected to upload")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateBusinessWithImage(newName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val imageBytes = _selectedImageUri.value?.let { uriToByteArray(it) } ?: ByteArray(0)

                val imageName = if (_selectedImageUri.value == null) {
                    _store.value?.business_image ?: "noImage"
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

                // Update business in database
                SupabaseClient.client.from("business").update(
                    mapOf(
                        "name" to newName,
                        "business_image" to imageName
                    )
                ) {
                    filter {
                        eq("id", _store.value?.id ?: "")
                    }
                }

                // Handle image upload if new image was selected
                if (imageBytes.isNotEmpty()) {
                    if (_store.value?.business_image != "noImage") {
                        try {
                            bucket.delete("business/${_store.value?.business_image}.jpg")
                        } catch (e: Exception) {
                            println("Error deleting old image: ${e.message}")
                        }
                    }
                    bucket.upload("business/$imageName.jpg", imageBytes, upsert = false)
                }

                // Update local store state
                _store.value = _store.value?.copy(
                    name = newName,
                    business_image = imageName
                )

                println("Business updated successfully")
            } catch (e: Exception) {
                println("Error updating business: ${e.message}")
            } finally {
                _isLoading.value = false
                _selectedImageUri.value = null // Reset selected image
            }
        }
    }

    private suspend fun uriToByteArray(uri: Uri): ByteArray {
        return withContext(Dispatchers.IO) {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Could not open URI")
            val outputStream = ByteArrayOutputStream()
            inputStream.use { input ->
                input.copyTo(outputStream)
            }
            outputStream.toByteArray()
        }
    }

    fun deleteBusiness(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = SupabaseClient.client.from("business").delete {
                    filter {
                        eq("id", id)
                    }
                }
                println("Business deleted successfully: $response")
                _store.value = null
            } catch (e: Exception) {
                println("Error deleting business: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateBusiness(imageBytes: ByteArray = ByteArray(0)) {
        val imageName = if (_selectedImageUri.value == null || _selectedImageUri.value == Uri.EMPTY) {
            _store.value?.business_image ?: "noImage"
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
            _isLoading.value = true
            try {
                SupabaseClient.client.from("business").update(
                    mapOf(
                        "name" to (_store.value?.name ?: ""),
                        "business_image" to imageName
                    )
                ) {
                    filter {
                        eq("id", _store.value?.id ?: "")
                    }
                }

                if (imageBytes.isNotEmpty()) {
                    if (_store.value?.business_image != "noImage") {
                        bucket.delete("business/${_store.value?.business_image}.jpg")
                    }
                    bucket.upload("business/$imageName.jpg", imageBytes, upsert = false)
                }

                println("Business updated successfully")
            } catch (e: Exception) {
                println("Error updating business: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
