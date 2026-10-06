package com.magicbox.kids.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Reads words aloud with the device's text-to-speech engine. */
class Speaker(context: Context) : TextToSpeech.OnInitListener {

    private val tts = TextToSpeech(context.applicationContext, this)
    private var ready = false
    private var persianAvailable = false
    var enabled = true

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        ready = true
        tts.setSpeechRate(0.85f)
        persianAvailable = tts.isLanguageAvailable(PERSIAN) >= TextToSpeech.LANG_AVAILABLE
    }

    fun speakEnglish(text: String) = speak(text, Locale.US)

    /** Returns false when the device has no Persian voice, so the UI can hide the button. */
    fun speakPersian(text: String): Boolean {
        if (!persianAvailable) return false
        speak(text, PERSIAN)
        return true
    }

    val canSpeakPersian: Boolean get() = persianAvailable

    private fun speak(text: String, locale: Locale) {
        if (!ready || !enabled) return
        tts.setLanguage(locale)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text.hashCode().toString())
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }

    private companion object {
        val PERSIAN: Locale = Locale("fa", "IR")
    }
}
