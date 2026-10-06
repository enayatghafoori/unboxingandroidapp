package com.magicbox.kids.data

import kotlin.random.Random

/** One option of a multiple-choice question. */
data class ChoiceOption(
    val label: String,
    val emoji: String? = null,
    val ltr: Boolean = false,
)

/** What to read aloud for a question; [english] picks the TTS voice. */
data class Speech(val text: String, val english: Boolean)

data class ChoiceQuestion(
    val instructionFa: String,
    val promptBig: String? = null,
    val promptBigLtr: Boolean = false,
    val promptSmall: String? = null,
    val speech: Speech? = null,
    /** Hide the big prompt so the child has to listen. */
    val listenOnly: Boolean = false,
    val options: List<ChoiceOption>,
    val answerIndex: Int,
    /** English word recorded as learned when answered correctly. */
    val learnedKey: String? = null,
)

data class SpellRound(val word: Word, val tiles: List<Char>)

data class MemoryCard(val pairId: Int, val text: String, val isEmoji: Boolean)

data class CatchRound(val target: Word, val choices: List<Word>)

/** Builds rounds for each mini-game from the catalog. Pure and seedable. */
class QuestionFactory(private val catalog: Catalog, private val random: Random = Random.Default) {

    private fun <T> shuffledOptions(correct: T, wrong: List<T>): Pair<List<T>, Int> {
        val all = (wrong + correct).shuffled(random)
        return all to all.indexOf(correct)
    }

    private fun otherWords(word: Word, count: Int): List<Word> =
        catalog.words.filter { it.en != word.en }.shuffled(random).take(count)

    fun listen(rounds: Int = 8): List<ChoiceQuestion> =
        catalog.words.shuffled(random).take(rounds).map { word ->
            val (opts, answer) = shuffledOptions(word, otherWords(word, 3))
            ChoiceQuestion(
                instructionFa = "گوش کن و تصویر درست را پیدا کن",
                speech = Speech(word.en, english = true),
                listenOnly = true,
                options = opts.map { ChoiceOption(label = "", emoji = it.emoji) },
                answerIndex = answer,
                learnedKey = word.en,
            )
        }

    fun picture(rounds: Int = 8): List<ChoiceQuestion> =
        catalog.words.shuffled(random).take(rounds).map { word ->
            val (opts, answer) = shuffledOptions(word, otherWords(word, 2))
            ChoiceQuestion(
                instructionFa = "اسم انگلیسی این تصویر چیست؟",
                promptBig = word.emoji,
                promptSmall = word.fa,
                speech = Speech(word.en, english = true),
                options = opts.map { ChoiceOption(label = it.en, ltr = true) },
                answerIndex = answer,
                learnedKey = word.en,
            )
        }

    fun rhyme(rounds: Int = 8): List<ChoiceQuestion> =
        catalog.rhymes.shuffled(random).take(rounds).map { r ->
            val (opts, answer) = shuffledOptions(r.answer, r.wrong)
            ChoiceQuestion(
                instructionFa = "کدام کلمه با این کلمه هم‌قافیه است؟",
                promptBig = r.word,
                options = opts.map { ChoiceOption(it) },
                answerIndex = answer,
            )
        }

    fun verse(rounds: Int = 6): List<ChoiceQuestion> =
        catalog.verses.shuffled(random).take(rounds).map { v ->
            val wrong = catalog.verses.filter { it != v }.shuffled(random).take(2).map { it.second }
            val (opts, answer) = shuffledOptions(v.second, wrong)
            ChoiceQuestion(
                instructionFa = "مصراع دوم این شعر کدام است؟",
                promptBig = v.first,
                promptSmall = v.poet,
                options = opts.map { ChoiceOption(it) },
                answerIndex = answer,
            )
        }

    fun proverb(rounds: Int = 6): List<ChoiceQuestion> =
        catalog.proverbs.shuffled(random).take(rounds).map { p ->
            val wrong = catalog.proverbs.filter { it != p }.shuffled(random).take(2).map { it.meaning }
            val (opts, answer) = shuffledOptions(p.meaning, wrong)
            ChoiceQuestion(
                instructionFa = "معنی این ضرب‌المثل چیست؟",
                promptBig = p.text,
                options = opts.map { ChoiceOption(it) },
                answerIndex = answer,
            )
        }

    fun spell(rounds: Int = 6): List<SpellRound> =
        catalog.words.filter { it.en.length in 3..6 }.shuffled(random).take(rounds).map { word ->
            var tiles = word.en.toList().shuffled(random)
            // Don't hand the child an already-solved word.
            repeat(5) { if (tiles.joinToString("") == word.en) tiles = tiles.shuffled(random) }
            SpellRound(word, tiles)
        }

    fun memory(pairs: Int = 6): List<MemoryCard> =
        catalog.words.shuffled(random).take(pairs).flatMapIndexed { i, word ->
            listOf(MemoryCard(i, word.emoji, isEmoji = true), MemoryCard(i, word.en, isEmoji = false))
        }.shuffled(random)

    fun catch(rounds: Int = 8): List<CatchRound> =
        catalog.words.shuffled(random).take(rounds).map { word ->
            CatchRound(word, (otherWords(word, 3) + word).shuffled(random))
        }
}
