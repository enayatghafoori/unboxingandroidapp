package com.magicbox.kids.data

import org.json.JSONArray
import org.json.JSONObject

object CatalogParser {

    fun parse(json: String): Catalog {
        val root = JSONObject(json)
        return Catalog(
            boxCost = root.getInt("boxCost"),
            shardsPerDuplicate = rarityMap(root.getJSONObject("shardsPerDuplicate")),
            craftCost = rarityMap(root.getJSONObject("craftCost")),
            dropRates = rarityMap(root.getJSONObject("dropRates")),
            series = root.getJSONArray("series").objects().map { s ->
                Series(
                    id = s.getString("id"),
                    titleFa = s.getString("titleFa"),
                    titleEn = s.getString("titleEn"),
                    subjectFa = s.getString("subjectFa"),
                    kind = if (s.getString("kind") == "literature") SeriesKind.LITERATURE else SeriesKind.ENGLISH,
                    color = s.getString("color").removePrefix("#").toLong(16),
                    emoji = s.getString("emoji"),
                )
            },
            dolls = root.getJSONArray("dolls").objects().map { d ->
                Doll(
                    id = d.getString("id"),
                    seriesId = d.getString("series"),
                    rarity = Rarity.fromKey(d.getString("rarity")),
                    emoji = d.getString("emoji"),
                    nameFa = d.getString("nameFa"),
                    nameEn = d.getString("nameEn"),
                    word = d.optStringOrNull("word"),
                    wordFa = d.optStringOrNull("wordFa"),
                    sentence = d.optStringOrNull("sentence"),
                    sentenceFa = d.optStringOrNull("sentenceFa"),
                    textFa = d.optStringOrNull("textFa"),
                    sourceFa = d.optStringOrNull("sourceFa"),
                    meaningFa = d.optStringOrNull("meaningFa"),
                )
            },
            words = root.getJSONArray("words").objects().map {
                Word(it.getString("en"), it.getString("fa"), it.getString("emoji"))
            },
            rhymes = root.getJSONArray("rhymes").objects().map {
                Rhyme(it.getString("word"), it.getString("answer"), it.getJSONArray("wrong").strings())
            },
            verses = root.getJSONArray("verses").objects().map {
                Verse(it.getString("first"), it.getString("second"), it.getString("poet"))
            },
            proverbs = root.getJSONArray("proverbs").objects().map {
                Proverb(it.getString("text"), it.getString("meaning"))
            },
        )
    }

    private fun rarityMap(obj: JSONObject): Map<Rarity, Int> =
        Rarity.entries.associateWith { obj.getInt(it.key) }

    private fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }

    private fun JSONArray.strings(): List<String> = (0 until length()).map { getString(it) }

    private fun JSONObject.optStringOrNull(key: String): String? =
        if (has(key) && !isNull(key)) getString(key) else null
}
