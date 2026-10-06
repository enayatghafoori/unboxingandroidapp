package com.magicbox.kids.audio

import android.media.AudioManager
import android.media.ToneGenerator

/** Tiny built-in sound effects, so the app ships without audio files. */
class Sfx {

    private val tone: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 70)
    } catch (e: RuntimeException) {
        null
    }
    var enabled = true

    fun tap() = play(ToneGenerator.TONE_PROP_BEEP, 40)
    fun correct() = play(ToneGenerator.TONE_PROP_ACK, 150)
    fun wrong() = play(ToneGenerator.TONE_PROP_NACK, 200)
    fun reveal() = play(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300)

    private fun play(type: Int, durationMs: Int) {
        if (enabled) tone?.startTone(type, durationMs)
    }

    fun release() {
        tone?.release()
    }
}
