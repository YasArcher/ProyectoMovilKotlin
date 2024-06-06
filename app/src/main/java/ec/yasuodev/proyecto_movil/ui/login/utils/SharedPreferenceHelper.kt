package ec.yasuodev.proyecto_movil.ui.login.utils

import android.content.Context

class SharedPreferenceHelper (private val context: Context) {
    companion object{
        private const val PREFS_NAME = "ec.yasuodev.proyecto_movil.ui.login.utils"
    }
    fun saveStringData(key: String, data: String){
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, data).apply()
    }
    fun getStringData(key: String): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }
}