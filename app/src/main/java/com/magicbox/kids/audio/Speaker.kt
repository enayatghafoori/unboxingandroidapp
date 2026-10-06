package com.magicbox.kids.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Reads words aloud. */
interface Speaker {
    var enabled: Boolean
    /** False when the device has no Persian voice, so the UI can hide the button. */
    val canSpeakPersian: Boolean
    fun speakEnglish(text: String)
    fun speakPersian(text: String): Boolean
    fun shutdown()
}

/** [Speaker] backed by the device's text-to-speech engine. */
class TtsSpeaker(context: Context) : Speaker, TextToSpeech.OnInitListener {

    private val tts = TextToSpeech(context.applicationContext, this)
    private var ready = false
    private var persianAvailable = false
    override var enabled = true

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        ready = true
        tts.setSpeechRate(0.85f)
        persianAvailable = tts.isLanguageAvailable(PERSIAN) >= TextToSpeech.LANG_AVAILABLE
    }

    override val canSpeakPersian: Boolean get() = persianAvailable

    override fun speakEnglish(text: String) = speak(text, Locale.US)

    override fun speakPersian(text: String): Boolean {
        if (!persianAvailable) return false
        speak(text, PERSIAN)
        return true
    }

    private fun speak(text: String, locale: Locale) {
        if (!ready || !enabled) return
        tts.setLanguage(locale)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text.hashCode().toString())
    }

    override fun shutdown() {
        tts.stop()
        tts.shutdown()
    }

    private companion object {
        val PERSIAN: Locale = Locale("fa", "IR")
    }
}
