package com.magicbox.kids.ui.components

import androidx.compose.runtime.staticCompositionLocalOf
import com.magicbox.kids.audio.Sfx
import com.magicbox.kids.audio.Speaker

val LocalSpeaker = staticCompositionLocalOf<Speaker> { error("Speaker not provided") }
val LocalSfx = staticCompositionLocalOf<Sfx> { error("Sfx not provided") }

private const val PERSIAN_DIGITS = "۰۱۲۳۴۵۶۷۸۹"

/** Shows numbers with Persian digits. */
fun Int.fa(): String = toString().map { c -> if (c in '0'..'9') PERSIAN_DIGITS[c - '0'] else c }.joinToString("")
