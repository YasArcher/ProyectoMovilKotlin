package ec.yasuodev.proyecto_movil.ui.auth.utils

import com.auth0.android.jwt.JWT

object TokenDecoding {

    /**
     * Decodes a JWT and returns its claims as a map.
     *
     * @param token The JWT token to be decoded.
     * @return A map containing the claims of the token.
     */
    fun decodeJWT(token: String): Map<String, Any?> {
        return try {
            val jwt = JWT(token)
            jwt.claims.mapValues { it.value.asString() }
        } catch (e: Exception) {
            emptyMap() // Return an empty map in case of an error
        }
    }

    /**
     * Extracts the userID from a decoded JWT token.
     *
     * @param decodedToken The decoded JWT token.
     * @return The user ID extracted from the token.
     */
    fun extractUserID(decodedToken: Map<String, Any?>): String {
        return decodedToken["userId"] as? String ?: "No UserID found"
    }
}
