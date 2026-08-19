package com.example.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.*
import java.util.Locale

class TTSManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    var isInitialized by mutableStateOf(false)
    var isSpeaking by mutableStateOf(false)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setSpeechRate(0.9f) // Slightly slower, friendly pace for kids
                tts?.setPitch(1.1f)      // Cheerful pitch for mascot
                isInitialized = true
            }
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            tts?.stop()
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "EduBuddyTTS")
            isSpeaking = true
        }
    }

    fun stop() {
        tts?.stop()
        isSpeaking = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}

@Composable
fun rememberTTSManager(context: Context): TTSManager {
    val ttsManager = remember { TTSManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }
    return ttsManager
}
