package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    suspend fun askBuddy(prompt: String, systemPrompt: String? = null): String {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return generateLocalFallback(prompt)
        }

        return try {
            val systemInstruction = systemPrompt?.let {
                GeminiContent(parts = listOf(GeminiPart(text = it)))
            }
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                ),
                systemInstruction = systemInstruction
            )
            val response = api.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            text?.trim() ?: generateLocalFallback(prompt)
        } catch (e: Exception) {
            generateLocalFallback(prompt)
        }
    }

    private fun generateLocalFallback(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("buddy") -> {
                "Hi there! 🤖 I'm EduAI Buddy, your super-smart learning friend! Ask me anything about science, math, stories, or how AI works!"
            }
            lower.contains("blue") && lower.contains("sky") -> {
                "The sky is blue because sunlight reaches Earth's atmosphere and is scattered in all directions by gases and particles. Blue light is scattered more than other colors because it travels as shorter, smaller waves! This is called Rayleigh scattering. ☀️🌈"
            }
            lower.contains("math") || lower.contains("fraction") || lower.contains("+") || lower.contains("*") -> {
                "Math is like a fun superpower puzzle! 🧩 When solving math problems, break them into smaller steps: 1) Identify what you know, 2) Decide which operation to use (+, -, ×, ÷), and 3) Check your work! Let's solve it together step-by-step!"
            }
            lower.contains("story") || lower.contains("space") || lower.contains("dragon") || lower.contains("dino") -> {
                "Once upon a time in a galaxy full of glowing stars, a curious young explorer named Alex teamed up with Sparky the AI Robot to build a starship powered by curiosity! Together they discovered that learning new things is the greatest adventure in the cosmos. 🚀✨"
            }
            lower.contains("ai") || lower.contains("neural") || lower.contains("robot") || lower.contains("machine") -> {
                "Artificial Intelligence (AI) works like a digital brain! 🧠 It learns by looking at thousands of examples (data), finding patterns, and making predictions. Just like how you learned to recognize cats by seeing lots of cats, AI learns from data!"
            }
            lower.contains("fly") || lower.contains("plane") || lower.contains("bird") -> {
                "Birds and planes fly because of a science principle called Lift! 🦅 Air moves faster over the curved top of a wing than under it, creating lower pressure above. This pressure difference pushes the wing up into the sky!"
            }
            lower.contains("sleep") || lower.contains("dream") -> {
                "Your brain needs sleep to recharge, organize everything you learned during the day, and fix your energy batteries! 😴 It's like saving your game progress at night so you start fresh tomorrow!"
            }
            else -> {
                "That's a fantastic question! 🌟 As your AI Study Buddy, I love exploring new topics with you. Did you know that every question you ask trains your brain neurons to grow stronger? Keep asking questions—curiosity is your superpower!"
            }
        }
    }
}
