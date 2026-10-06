package com.magicbox.kids.data

import java.util.TimeZone
import kotlin.random.Random

data class UnboxResult(val doll: Doll, val isNew: Boolean, val shardsGained: Int)

/** Pure game-economy rules, kept free of Android so they can be unit tested. */
object GameRules {

    private const val DAY_MS = 24L * 60 * 60 * 1000

    /** Local calendar day number, used for the daily free box and screen-time limit. */
    fun today(nowMs: Long = System.currentTimeMillis(), tz: TimeZone = TimeZone.getDefault()): Long =
        (nowMs + tz.getOffset(nowMs)) / DAY_MS

    fun pickRarity(rates: Map<Rarity, Int>, random: Random): Rarity {
        val total = rates.values.sum()
        var roll = random.nextInt(total)
        for (rarity in Rarity.entries) {
            val weight = rates[rarity] ?: 0
            if (roll < weight) return rarity
            roll -= weight
        }
        return Rarity.COMMON
    }

    /**
     * Opens one box of [seriesId]. Returns null if the child cannot pay for it.
     * Duplicates turn into shards so a box never feels wasted, and a duplicate gets one
     * "lucky" retry toward a doll the child is still missing.
     */
    fun openBox(
        catalog: Catalog,
        progress: Progress,
        seriesId: String,
        free: Boolean,
        today: Long,
        random: Random = Random.Default,
    ): Pair<UnboxResult, Progress>? {
        if (free && !progress.freeBoxAvailable(today)) return null
        if (!free && progress.stars < catalog.boxCost) return null
        val pool = catalog.dollsIn(seriesId)
        if (pool.isEmpty()) return null

        val rarity = pickRarity(catalog.dropRates, random)
        val sameRarity = pool.filter { it.rarity == rarity }.ifEmpty { pool }
        var doll = sameRarity.random(random)
        if (progress.owns(doll.id) && random.nextBoolean()) {
            val missing = sameRarity.filterNot { progress.owns(it.id) }
            if (missing.isNotEmpty()) doll = missing.random(random)
        }

        val isNew = !progress.owns(doll.id)
        val shards = if (isNew) 0 else catalog.shardsPerDuplicate[doll.rarity] ?: 0
        val updated = progress.copy(
            stars = if (free) progress.stars else progress.stars - catalog.boxCost,
            lastFreeBoxDay = if (free) today else progress.lastFreeBoxDay,
            owned = progress.owned + (doll.id to (progress.owned[doll.id] ?: 0) + 1),
            shards = progress.shards + shards,
            boxesOpened = progress.boxesOpened + 1,
        )
        return UnboxResult(doll, isNew, shards) to updated
    }

    /** Builds a missing doll from shards. Returns null if not allowed. */
    fun craft(catalog: Catalog, progress: Progress, dollId: String): Progress? {
        val doll = catalog.doll(dollId) ?: return null
        if (progress.owns(dollId)) return null
        val cost = catalog.craftCost[doll.rarity] ?: return null
        if (progress.shards < cost) return null
        return progress.copy(
            shards = progress.shards - cost,
            owned = progress.owned + (dollId to 1),
        )
    }

    /** Two stars per correct answer, plus a bonus for a perfect round. */
    fun starsFor(correct: Int, total: Int): Int =
        correct * 2 + if (total > 0 && correct == total) 5 else 0

    fun recordGame(
        progress: Progress,
        gameId: String,
        correct: Int,
        total: Int,
        learned: Collection<String>,
    ): Progress {
        val earned = starsFor(correct, total)
        val best = maxOf(progress.bestScores[gameId] ?: 0, ratingFor(correct, total))
        return progress.copy(
            stars = progress.stars + earned,
            gamesPlayed = progress.gamesPlayed + 1,
            bestScores = progress.bestScores + (gameId to best),
            learnedWords = progress.learnedWords + learned,
        )
    }

    /** 0-3 stars shown on the result screen. */
    fun ratingFor(correct: Int, total: Int): Int {
        if (total <= 0) return 0
        val ratio = correct.toFloat() / total
        return when {
            ratio >= 0.95f -> 3
            ratio >= 0.7f -> 2
            ratio >= 0.4f -> 1
            else -> 0
        }
    }

    fun addUsage(progress: Progress, seconds: Int, today: Long): Progress =
        if (progress.usageDay == today) {
            progress.copy(usageSecondsToday = progress.usageSecondsToday + seconds)
        } else {
            progress.copy(usageDay = today, usageSecondsToday = seconds)
        }
}
