package ec.yasuodev.proyecto_movil.ui.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage


// Create Supabase client
object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://vgnnieizrwmjemlnziaj.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZnbm5pZWl6cndtamVtbG56aWFqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTIwMDA2MDIsImV4cCI6MjAyNzU3NjYwMn0.ItXBN_QYXZsrJL_cJdPWumCxsISYxGzTatcn4hysWOI"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }

}
