package com.magicbox.kids.data

enum class GameCategory(val titleFa: String) {
    ENGLISH("یادگیری انگلیسی"),
    LITERATURE("ادبیات فارسی"),
}

enum class GameInfo(
    val id: String,
    val titleFa: String,
    val descriptionFa: String,
    val emoji: String,
    val category: GameCategory,
) {
    LISTEN("listen", "گوش کن و پیدا کن", "کلمه را بشنو و تصویرش را انتخاب کن", "👂", GameCategory.ENGLISH),
    PICTURE("picture", "اسم تصویر", "اسم انگلیسی تصویر را پیدا کن", "🖼️", GameCategory.ENGLISH),
    CATCH("catch", "بارش کلمه", "حباب کلمهٔ درست را قبل از افتادن بگیر", "🫧", GameCategory.ENGLISH),
    SPELL("spell", "هجی کن", "حروف را به ترتیب بچین تا کلمه ساخته شود", "🔤", GameCategory.ENGLISH),
    MEMORY("memory", "کارت‌های حافظه", "تصویر و کلمهٔ انگلیسی‌اش را جفت کن", "🃏", GameCategory.ENGLISH),
    RHYME("rhyme", "قافیه‌باز", "کلمهٔ هم‌قافیه را پیدا کن", "🎵", GameCategory.LITERATURE),
    VERSE("verse", "مصراع گمشده", "شعر شاعران بزرگ را کامل کن", "📜", GameCategory.LITERATURE),
    PROVERB("proverb", "ضرب‌المثل‌یاب", "معنی ضرب‌المثل را پیدا کن", "💡", GameCategory.LITERATURE);

    companion object {
        fun fromId(id: String): GameInfo? = entries.firstOrNull { it.id == id }
    }
}
