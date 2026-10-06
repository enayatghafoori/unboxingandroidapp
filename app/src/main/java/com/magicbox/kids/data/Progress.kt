package com.magicbox.kids.data

import org.json.JSONArray
import org.json.JSONObject

/** Everything the app remembers about the child. Stored on the device only. */
data class Progress(
    val stars: Int = 0,
    val shards: Int = 0,
    /** Doll id -> how many copies were unboxed. */
    val owned: Map<String, Int> = emptyMap(),
    val lastFreeBoxDay: Long = -1,
    val learnedWords: Set<String> = emptySet(),
    val gamesPlayed: Int = 0,
    val bestScores: Map<String, Int> = emptyMap(),
    val boxesOpened: Int = 0,
    val soundOn: Boolean = true,
    /** 0 means no daily limit. */
    val dailyLimitMinutes: Int = 0,
    val usageDay: Long = -1,
    val usageSecondsToday: Int = 0,
    /** A parent unlocked the app for the rest of this day. */
    val limitBypassDay: Long = -1,
) {
    fun owns(dollId: String): Boolean = (owned[dollId] ?: 0) > 0

    fun freeBoxAvailable(today: Long): Boolean = lastFreeBoxDay != today

    fun usageSecondsOn(today: Long): Int = if (usageDay == today) usageSecondsToday else 0

    fun isLimitReached(today: Long): Boolean =
        dailyLimitMinutes > 0 &&
            limitBypassDay != today &&
            usageSecondsOn(today) >= dailyLimitMinutes * 60

    fun toJson(): String = JSONObject().apply {
        put("stars", stars)
        put("shards", shards)
        put("owned", JSONObject().apply { owned.forEach { (k, v) -> put(k, v) } })
        put("lastFreeBoxDay", lastFreeBoxDay)
        put("learnedWords", JSONArray().apply { learnedWords.forEach { put(it) } })
        put("gamesPlayed", gamesPlayed)
        put("bestScores", JSONObject().apply { bestScores.forEach { (k, v) -> put(k, v) } })
        put("boxesOpened", boxesOpened)
        put("soundOn", soundOn)
        put("dailyLimitMinutes", dailyLimitMinutes)
        put("usageDay", usageDay)
        put("usageSecondsToday", usageSecondsToday)
        put("limitBypassDay", limitBypassDay)
    }.toString()

    companion object {
        /** New players get enough stars for their first box on top of the free daily one. */
        const val STARTING_STARS = 30

        fun fromJson(json: String?): Progress {
            if (json.isNullOrBlank()) return Progress(stars = STARTING_STARS)
            return try {
                val o = JSONObject(json)
                Progress(
                    stars = o.optInt("stars", STARTING_STARS),
                    shards = o.optInt("shards", 0),
                    owned = o.optJSONObject("owned")?.toIntMap() ?: emptyMap(),
                    lastFreeBoxDay = o.optLong("lastFreeBoxDay", -1),
                    learnedWords = o.optJSONArray("learnedWords")?.let { a ->
                        (0 until a.length()).map { a.getString(it) }.toSet()
                    } ?: emptySet(),
                    gamesPlayed = o.optInt("gamesPlayed", 0),
                    bestScores = o.optJSONObject("bestScores")?.toIntMap() ?: emptyMap(),
                    boxesOpened = o.optInt("boxesOpened", 0),
                    soundOn = o.optBoolean("soundOn", true),
                    dailyLimitMinutes = o.optInt("dailyLimitMinutes", 0),
                    usageDay = o.optLong("usageDay", -1),
                    usageSecondsToday = o.optInt("usageSecondsToday", 0),
                    limitBypassDay = o.optLong("limitBypassDay", -1),
                )
            } catch (e: Exception) {
                Progress(stars = STARTING_STARS)
            }
        }

        private fun JSONObject.toIntMap(): Map<String, Int> =
            keys().asSequence().associateWith { getInt(it) }
    }
}
