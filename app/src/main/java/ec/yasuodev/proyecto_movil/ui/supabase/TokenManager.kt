package ec.yasuodev.proyecto_movil.ui.supabase

import android.content.Context
import ec.yasuodev.proyecto_movil.ui.auth.utils.SharedPreferenceHelper
import io.github.jan.supabase.gotrue.auth

object TokenManager {

    private const val ACCESS_TOKEN_KEY = "accessToken"

    // Función para guardar el token en SharedPreferences
    fun saveToken(context: Context, token: String) {
        val sharedPreferenceHelper = SharedPreferenceHelper(context)
        sharedPreferenceHelper.saveStringData(ACCESS_TOKEN_KEY, token)
    }

    // Función para obtener el token desde SharedPreferences
    fun getToken(context: Context): String? {
        val sharedPreferenceHelper = SharedPreferenceHelper(context)
        return sharedPreferenceHelper.getStringData(ACCESS_TOKEN_KEY)
    }

    // Función para limpiar el token almacenado
    fun clearToken(context: Context) {
        val sharedPreferenceHelper = SharedPreferenceHelper(context)
        sharedPreferenceHelper.removeStringData(ACCESS_TOKEN_KEY)
    }

    // Función para verificar si hay un token válido almacenado
    fun isTokenValid(context: Context): Boolean {
        val token = getToken(context)
        // Puedes agregar aquí lógica adicional para verificar la validez del token si es necesario
        return !token.isNullOrEmpty()
    }

    suspend fun refreshAccessToken(context: Context) {
        try {
            val token = getToken(context)
            if (!token.isNullOrEmpty()) {
                SupabaseClient.client.auth.refreshCurrentSession()
                saveToken(context, SupabaseClient.client.auth.currentSessionOrNull().toString())
            }
        } catch (e: Exception) {
            // Manejar errores al refrescar el token
        }
    }
}
