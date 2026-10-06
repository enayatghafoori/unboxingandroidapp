package com.magicbox.kids

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.magicbox.kids.data.Catalog
import com.magicbox.kids.data.CatalogParser
import com.magicbox.kids.data.GameRules
import com.magicbox.kids.data.Progress
import com.magicbox.kids.data.UnboxResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val catalog: Catalog = application.assets.open("content/catalog.json")
        .bufferedReader().use { CatalogParser.parse(it.readText()) }

    private val prefs = application.getSharedPreferences("magicbox", Context.MODE_PRIVATE)

    private val _progress = MutableStateFlow(Progress.fromJson(prefs.getString(KEY, null)))
    val progress: StateFlow<Progress> = _progress.asStateFlow()

    fun today(): Long = GameRules.today()

    private fun update(transform: (Progress) -> Progress) {
        val next = transform(_progress.value)
        _progress.value = next
        prefs.edit().putString(KEY, next.toJson()).apply()
    }

    fun openBox(seriesId: String, free: Boolean): UnboxResult? {
        val (result, next) = GameRules.openBox(catalog, _progress.value, seriesId, free, today())
            ?: return null
        update { next }
        return result
    }

    fun craft(dollId: String): Boolean {
        val next = GameRules.craft(catalog, _progress.value, dollId) ?: return false
        update { next }
        return true
    }

    /** Returns the stars earned. */
    fun recordGame(gameId: String, correct: Int, total: Int, learned: Collection<String>): Int {
        update { GameRules.recordGame(it, gameId, correct, total, learned) }
        return GameRules.starsFor(correct, total)
    }

    fun addUsage(seconds: Int) = update { GameRules.addUsage(it, seconds, today()) }

    fun setSound(on: Boolean) = update { it.copy(soundOn = on) }

    fun setDailyLimit(minutes: Int) = update { it.copy(dailyLimitMinutes = minutes) }

    fun unlockForToday() = update { it.copy(limitBypassDay = today()) }

    fun resetProgress() = update { Progress(stars = Progress.STARTING_STARS, soundOn = it.soundOn) }

    private companion object {
        const val KEY = "progress"
    }
}
