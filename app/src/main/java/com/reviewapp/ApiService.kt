package com.reviewapp

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object ApiService {
    private const val BASE_URL = "https://openrouter.ai/api/v1"
    private const val API_KEY = "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"
    private const val MODEL = "nvidia/nemotron-3.5-embed-1b"
    private val client = OkHttpClient()
    private val gson = Gson()
    private val mainHandler = Handler(Looper.getMainLooper())

    data class AiRequest(val model: String, val messages: List<Message>)
    data class Message(val role: String, val content: String)
    data class AiResponse(val choices: List<Choice>)
    data class Choice(val message: Message)

    interface AiCallback {
        fun onSuccess(text: String)
        fun onError(error: String)
    }

    fun generateReview(prompt: String, callback: AiCallback) {
        val json = gson.toJson(AiRequest(MODEL, listOf(Message("user", prompt))))
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL/chat/completions")
            .header("Authorization", "Bearer $API_KEY")
            .header("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mainHandler.post { callback.onError(e.message ?: "Network error") }
            }

            override fun onResponse(call: Call, response: Response) {
                val respBody = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    mainHandler.post { callback.onError("Server error: $respBody") }
                    return
                }
                try {
                    val aiResp = gson.fromJson(respBody, AiResponse::class.java)
                    val text = aiResp.choices.firstOrNull()?.message?.content ?: "कोई रिव्यू नहीं मिला"
                    mainHandler.post { callback.onSuccess(text) }
                } catch (e: Exception) {
                    mainHandler.post { callback.onError("Parse error: ${e.message}") }
                }
            }
        })
    }

    fun generateReply(reviewText: String, callback: AiCallback) {
        val prompt = "यह एक गूगल बिजनेस रिव्यू है: '$reviewText'. इस रिव्यू का एक छोटा, विनम्र और पेशेवर जवाब दें।"
        generateReview(prompt, callback)
    }
}