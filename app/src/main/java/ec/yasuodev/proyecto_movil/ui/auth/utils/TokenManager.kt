package ec.yasuodev.proyecto_movil.ui.auth.utils

import android.content.Context

object TokenManager {

    private const val ACCESS_TOKEN_KEY = "accessToken"
    private const val TOKEN = "token"

    // Función para guardar el token en SharedPreferences
    fun saveToken(context: Context,accessToken:String ,token: String) {
        val sharedPreferenceHelper = SharedPreferenceHelper(context)
        sharedPreferenceHelper.saveStringData(ACCESS_TOKEN_KEY, accessToken)
        sharedPreferenceHelper.saveStringData(TOKEN, token)
    }

    // Función para obtener el token desde SharedPreferences
    fun getAccessToken(context: Context): String? {
        val sharedPreferenceHelper = SharedPreferenceHelper(context)
        return sharedPreferenceHelper.getStringData(ACCESS_TOKEN_KEY)
    }

    fun getToken(context: Context): String? {
        val sharedPreferenceHelper = SharedPreferenceHelper(context)
        return sharedPreferenceHelper.getStringData(TOKEN)
    }

    // Función para limpiar el token almacenado
    fun clearToken(context: Context) {
        val sharedPreferenceHelper = SharedPreferenceHelper(context)
        sharedPreferenceHelper.removeStringData(ACCESS_TOKEN_KEY)
        sharedPreferenceHelper.removeStringData(TOKEN)
    }

    // Función para verificar si hay un token válido almacenado
    fun isTokenValid(context: Context): Boolean {
        val token = getAccessToken(context)
        return !token.isNullOrEmpty()
    }
}
