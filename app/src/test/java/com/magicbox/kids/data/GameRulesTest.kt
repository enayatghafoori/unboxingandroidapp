package com.magicbox.kids.data

import java.io.File
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRulesTest {

    private val catalog: Catalog = CatalogParser.parse(
        listOf("src/main/assets/content/catalog.json", "app/src/main/assets/content/catalog.json")
            .map(::File).first { it.exists() }.readText(),
    )

    @Test
    fun catalogIsConsistent() {
        assertEquals(3, catalog.series.size)
        catalog.series.forEach { s ->
            val dolls = catalog.dollsIn(s.id)
            assertEquals("each series has 8 dolls", 8, dolls.size)
            assertEquals("each series has one secret", 1, dolls.count { it.rarity == Rarity.SECRET })
        }
        catalog.dolls.forEach { d ->
            assertTrue("${d.id} teaches something", d.word != null || d.textFa != null)
        }
        assertEquals(catalog.dolls.size, catalog.dolls.map { it.id }.toSet().size)
    }

    @Test
    fun paidBoxCostsStarsAndGivesDoll() {
        val start = Progress(stars = 40)
        val (result, after) = GameRules.openBox(catalog, start, "forest", free = false, today = 10, random = Random(1))!!
        assertEquals(10, after.stars)
        assertTrue(result.isNew)
        assertEquals("forest", result.doll.seriesId)
        assertEquals(1, after.owned[result.doll.id])
        assertEquals(1, after.boxesOpened)
    }

    @Test
    fun cannotOpenWithoutStars() {
        assertNull(GameRules.openBox(catalog, Progress(stars = 5), "forest", free = false, today = 10))
    }

    @Test
    fun freeBoxOncePerDay() {
        val (_, after) = GameRules.openBox(catalog, Progress(stars = 0), "fruits", free = true, today = 7)!!
        assertEquals(0, after.stars)
        assertFalse(after.freeBoxAvailable(7))
        assertNull(GameRules.openBox(catalog, after, "fruits", free = true, today = 7))
        assertNotNull(GameRules.openBox(catalog, after, "fruits", free = true, today = 8))
    }

    @Test
    fun duplicatesGiveShards() {
        val all = catalog.dollsIn("tales").associate { it.id to 1 }
        val (result, after) = GameRules.openBox(catalog, Progress(stars = 30, owned = all), "tales", false, 1, Random(3))!!
        assertFalse(result.isNew)
        assertEquals(catalog.shardsPerDuplicate.getValue(result.doll.rarity), after.shards)
        assertEquals(2, after.owned[result.doll.id])
    }

    @Test
    fun craftingUsesShards() {
        val doll = catalog.dollsIn("forest").first { it.rarity == Rarity.COMMON }
        val cost = catalog.craftCost.getValue(Rarity.COMMON)
        assertNull(GameRules.craft(catalog, Progress(shards = cost - 1), doll.id))
        val after = GameRules.craft(catalog, Progress(shards = cost + 1), doll.id)!!
        assertEquals(1, after.shards)
        assertTrue(after.owns(doll.id))
        assertNull(GameRules.craft(catalog, after.copy(shards = 100), doll.id))
    }

    @Test
    fun gameRewards() {
        assertEquals(21, GameRules.starsFor(8, 8))
        assertEquals(10, GameRules.starsFor(5, 8))
        val after = GameRules.recordGame(Progress(stars = 0), "listen", 8, 8, listOf("cat", "dog"))
        assertEquals(21, after.stars)
        assertEquals(3, after.bestScores["listen"])
        assertEquals(setOf("cat", "dog"), after.learnedWords)
    }

    @Test
    fun dailyLimit() {
        var p = Progress(dailyLimitMinutes = 1)
        p = GameRules.addUsage(p, 45, today = 3)
        assertFalse(p.isLimitReached(3))
        p = GameRules.addUsage(p, 15, today = 3)
        assertTrue(p.isLimitReached(3))
        assertFalse("new day resets", p.isLimitReached(4))
        assertFalse("parent unlock", p.copy(limitBypassDay = 3).isLimitReached(3))
    }

    @Test
    fun progressRoundTrips() {
        val p = Progress(
            stars = 12, shards = 3, owned = mapOf("forest_cat" to 2), lastFreeBoxDay = 5,
            learnedWords = setOf("cat"), gamesPlayed = 4, bestScores = mapOf("spell" to 2),
            boxesOpened = 6, soundOn = false, dailyLimitMinutes = 30, usageDay = 5,
            usageSecondsToday = 90, limitBypassDay = 2,
        )
        assertEquals(p, Progress.fromJson(p.toJson()))
        assertEquals(Progress.STARTING_STARS, Progress.fromJson(null).stars)
        assertEquals(Progress.STARTING_STARS, Progress.fromJson("not json").stars)
    }

    @Test
    fun questionsAreWellFormed() {
        val f = QuestionFactory(catalog, Random(42))
        listOf(f.listen(), f.picture(), f.rhyme(), f.verse(), f.proverb()).forEach { qs ->
            assertTrue(qs.isNotEmpty())
            qs.forEach { q ->
                assertTrue(q.answerIndex in q.options.indices)
                assertEquals(q.options.size, q.options.toSet().size)
            }
        }
        f.spell().forEach { r -> assertEquals(r.word.en.toList().sorted(), r.tiles.sorted()) }
        val cards = f.memory()
        assertEquals(12, cards.size)
        cards.groupBy { it.pairId }.values.forEach { assertEquals(2, it.size) }
        f.catch().forEach { r -> assertTrue(r.target in r.choices) }
    }
}
