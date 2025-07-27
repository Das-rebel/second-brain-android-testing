package com.secondbrain.app.data.remote

// Temporarily disabled Supabase imports for build stability
// import io.github.jan.supabase.createSupabaseClient
// import io.github.jan.supabase.postgrest.Postgrest
// import io.github.jan.supabase.auth.Auth

/**
 * Supabase client configuration for Second Brain app.
 * TODO: Re-enable when Supabase dependencies are properly resolved
 */
object SupabaseClient {
    private const val SUPABASE_URL = "https://czkkzstoejzcejearcth.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6a2t6c3RvZWp6Y2VqZWFyY3RoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjE4MTQyNzIsImV4cCI6MjAzNzM5MDI3Mn0.BgOdRQBvBc_6R1cflUQLl0qe1V4q0q_y9PuVYALg_7c"
    
    // Placeholder for when Supabase is properly integrated
    val client: Any? = null
    
    /*
    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Auth)
    }
    */
}