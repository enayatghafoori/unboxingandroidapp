package com.magicbox.kids.data

enum class Rarity(val key: String, val labelFa: String) {
    COMMON("common", "معمولی"),
    RARE("rare", "کمیاب"),
    SECRET("secret", "مخفی");

    companion object {
        fun fromKey(key: String): Rarity = entries.first { it.key == key }
    }
}

enum class SeriesKind { ENGLISH, LITERATURE }

data class Series(
    val id: String,
    val titleFa: String,
    val titleEn: String,
    val subjectFa: String,
    val kind: SeriesKind,
    val color: Long,
    val emoji: String,
) {
    val imagePath: String get() = "images/boxes/$id.webp"
}

data class Doll(
    val id: String,
    val seriesId: String,
    val rarity: Rarity,
    val emoji: String,
    val nameFa: String,
    val nameEn: String,
    /** English series: the word the doll teaches. */
    val word: String? = null,
    val wordFa: String? = null,
    val sentence: String? = null,
    val sentenceFa: String? = null,
    /** Literature series: a verse or proverb, its source and a kid-friendly meaning. */
    val textFa: String? = null,
    val sourceFa: String? = null,
    val meaningFa: String? = null,
) {
    val imagePath: String get() = "images/dolls/$id.webp"
}

data class Word(val en: String, val fa: String, val emoji: String)

data class Rhyme(val word: String, val answer: String, val wrong: List<String>)

data class Verse(val first: String, val second: String, val poet: String)

data class Proverb(val text: String, val meaning: String)

data class Catalog(
    val boxCost: Int,
    val shardsPerDuplicate: Map<Rarity, Int>,
    val craftCost: Map<Rarity, Int>,
    val dropRates: Map<Rarity, Int>,
    val series: List<Series>,
    val dolls: List<Doll>,
    val words: List<Word>,
    val rhymes: List<Rhyme>,
    val verses: List<Verse>,
    val proverbs: List<Proverb>,
) {
    private val dollsById = dolls.associateBy { it.id }
    private val seriesById = series.associateBy { it.id }

    fun doll(id: String): Doll? = dollsById[id]
    fun series(id: String): Series? = seriesById[id]
    fun dollsIn(seriesId: String): List<Doll> = dolls.filter { it.seriesId == seriesId }
}
