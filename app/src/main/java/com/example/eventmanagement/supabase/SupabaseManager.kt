package com.example.eventmanagement.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object SupabaseManager {

    private const val SUPABASE_URL = "https://teyesvenndfavnipnjyl.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRleWVzdmVubmRmYXZuaXBuanlsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ4NjEwOTIsImV4cCI6MjA4MDQzNzA5Mn0.yJEhFEx9lWFlexz3wnlrRMm26OJhJatrXAUtifJFbmQ"

    private var httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                }
            )
        }
    }

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        httpClient = this@SupabaseManager.httpClient
    }
}
