package com.magicbox.kids.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magicbox.kids.data.SpellRound
import com.magicbox.kids.ui.components.KidButton
import com.magicbox.kids.ui.components.LocalSfx
import com.magicbox.kids.ui.components.LocalSpeaker
import com.magicbox.kids.ui.theme.Palette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Tap the scrambled letters in order to build the English word. */
@Composable
fun SpellGame(rounds: List<SpellRound>, onFinish: GameFinished) {
    val speaker = LocalSpeaker.current
    val sfx = LocalSfx.current
    val scope = rememberCoroutineScope()
    var index by remember { mutableIntStateOf(0) }
    var correct by remember { mutableIntStateOf(0) }
    var mistakes by remember { mutableIntStateOf(0) }
    val used = remember { mutableStateListOf<Int>() }
    val learned = remember { mutableStateListOf<String>() }
    val shake = remember { Animatable(0f) }

    if (rounds.isEmpty()) {
        LaunchedEffect(Unit) { onFinish(0, 0, emptyList()) }
        return
    }
    val round = rounds[index]
    val word = round.word.en
    val filled = used.size
    val done = filled == word.length

    LaunchedEffect(index) {
        delay(300)
        speaker.speakEnglish(word)
    }
    LaunchedEffect(done) {
        if (!done) return@LaunchedEffect
        // One slip is fine for little fingers.
        if (mistakes <= 1) {
            correct++
            learned += word
        }
        sfx.correct()
        speaker.speakEnglish(word)
        delay(1300)
        if (index + 1 >= rounds.size) {
            onFinish(correct, rounds.size, learned.toList())
        } else {
            used.clear()
            mistakes = 0
            index++
        }
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RoundHeader(index, rounds.size)
        Spacer(Modifier.height(12.dp))
        Text(round.word.emoji, fontSize = 96.sp)
        Text("( ${round.word.fa} )", style = MaterialTheme.typography.titleLarge, color = Palette.InkSoft)
        Spacer(Modifier.height(8.dp))
        KidButton("بشنو", emoji = "🔊", color = Palette.Sky, height = 48.dp) { speaker.speakEnglish(word) }
        Spacer(Modifier.height(20.dp))

        // English is written left to right, even inside the Persian UI.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                Modifier.offset(x = shake.value.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                word.forEachIndexed { i, ch ->
                    val shown = i < filled
                    Box(
                        Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (shown) Palette.Mint else Color.White)
                            .border(3.dp, if (shown) Palette.Mint else Palette.LavenderLight, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (shown) Text(ch.toString(), style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                round.tiles.forEachIndexed { tileIndex, ch ->
                    val isUsed = tileIndex in used
                    Box(
                        Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isUsed) Color.Transparent else Palette.Sun)
                            .clickable(enabled = !isUsed && !done) {
                                if (ch == word[filled]) {
                                    used += tileIndex
                                    sfx.tap()
                                } else {
                                    mistakes++
                                    sfx.wrong()
                                    scope.launch {
                                        shake.animateTo(0f, keyframes {
                                            durationMillis = 400
                                            -12f at 50
                                            12f at 150
                                            -8f at 250
                                            8f at 330
                                        })
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (!isUsed) Text(ch.toString(), style = MaterialTheme.typography.headlineMedium, color = Palette.Ink)
                    }
                }
            }
        }
    }
}
