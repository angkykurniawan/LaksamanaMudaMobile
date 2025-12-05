package com.example.eventmanagement.autentikasi.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseManager {
    private const val SUPABASE_URL = "https://teyesvenndfavnipnjyl.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRleWVzdmVubmRmYXZuaXBuanlsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ4NjEwOTIsImV4cCI6MjA4MDQzNzA5Mn0.yJEhFEx9lWFlexz3wnlrRMm26OJhJatrXAUtifJFbmQ"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
    }
}