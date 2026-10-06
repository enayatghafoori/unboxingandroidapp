package com.magicbox.kids.audio

import android.media.AudioManager
import android.media.ToneGenerator

/** Short feedback sounds. */
interface Sfx {
    var enabled: Boolean
    fun tap()
    fun correct()
    fun wrong()
    fun reveal()
    fun release()
}

/** [Sfx] using built-in system tones, so the app ships without audio files. */
class ToneSfx : Sfx {

    private val tone: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 70)
    } catch (e: RuntimeException) {
        null
    }
    override var enabled = true

    override fun tap() = play(ToneGenerator.TONE_PROP_BEEP, 40)
    override fun correct() = play(ToneGenerator.TONE_PROP_ACK, 150)
    override fun wrong() = play(ToneGenerator.TONE_PROP_NACK, 200)
    override fun reveal() = play(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300)

    private fun play(type: Int, durationMs: Int) {
        if (enabled) tone?.startTone(type, durationMs)
    }

    override fun release() {
        tone?.release()
    }
}
