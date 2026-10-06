package com.magicbox.kids.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magicbox.kids.data.CatchRound
import com.magicbox.kids.ui.components.KidButton
import com.magicbox.kids.ui.components.LocalSfx
import com.magicbox.kids.ui.components.LocalSpeaker
import com.magicbox.kids.ui.theme.Palette
import kotlinx.coroutines.delay

private const val FALL_MS = 6500f
private const val STAGGER_MS = 700f
private val BUBBLE = 96.dp

private val bubbleColors = listOf(Palette.Pink, Palette.Sky, Palette.Mint, Palette.Peach)

/** Word bubbles fall from the sky; pop the one that matches the picture. */
@Composable
fun CatchGame(rounds: List<CatchRound>, onFinish: GameFinished) {
    val speaker = LocalSpeaker.current
    val sfx = LocalSfx.current
    var index by remember { mutableIntStateOf(0) }
    var correct by remember { mutableIntStateOf(0) }
    var elapsed by remember { mutableFloatStateOf(0f) }
    // null while the round is running, otherwise the tapped bubble (-1 = missed).
    var result by remember { mutableStateOf<Int?>(null) }
    val learned = remember { mutableStateListOf<String>() }

    if (rounds.isEmpty()) {
        LaunchedEffect(Unit) { onFinish(0, 0, emptyList()) }
        return
    }
    val round = rounds[index]
    val targetIndex = round.choices.indexOf(round.target)
    // The target is never the first to fall, so the child has time to read.
    val order = remember(index) {
        val others = round.choices.indices.filter { it != targetIndex }.shuffled()
        (listOf(others.first(), targetIndex) + others.drop(1)).withIndex().associate { (slot, choice) -> choice to slot }
    }

    LaunchedEffect(index) {
        elapsed = 0f
        delay(300)
        speaker.speakEnglish(round.target.en)
        val start = withFrameMillis { it }
        while (result == null) {
            withFrameMillis { now -> elapsed = (now - start).toFloat() }
            val targetY = (elapsed - order.getValue(targetIndex) * STAGGER_MS) / FALL_MS
            if (targetY > 1f && result == null) {
                result = -1
                sfx.wrong()
            }
        }
    }
    LaunchedEffect(result) {
        val r = result ?: return@LaunchedEffect
        if (r == targetIndex) {
            correct++
            learned += round.target.en
            sfx.correct()
            speaker.speakEnglish(round.target.en)
        } else if (r >= 0) {
            sfx.wrong()
        }
        delay(1300)
        if (index + 1 >= rounds.size) {
            onFinish(correct, rounds.size, learned.toList())
        } else {
            elapsed = 0f
            index++
            result = null
        }
    }

    Column(Modifier.fillMaxSize()) {
        RoundHeader(index, rounds.size)
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(round.target.emoji, fontSize = 56.sp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("حباب درست را بگیر!", style = MaterialTheme.typography.titleLarge)
                Text("( ${round.target.fa} )", style = MaterialTheme.typography.titleMedium, color = Palette.InkSoft)
            }
            KidButton("", emoji = "🔊", color = Palette.Sky, height = 52.dp) { speaker.speakEnglish(round.target.en) }
        }
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(Brush.verticalGradient(listOf(Color(0xFFDDF2FF), Color(0xFFFFF4FB)))),
        ) {
            val lane = maxWidth / round.choices.size
            val bubble = minOf(lane - 6.dp, BUBBLE)
            val travel = maxHeight - bubble
            round.choices.forEachIndexed { i, word ->
                val t = ((elapsed - order.getValue(i) * STAGGER_MS) / FALL_MS)
                if (t < 0f || t > 1.05f) return@forEachIndexed
                val finished = result != null
                val color = when {
                    finished && i == targetIndex -> Palette.Correct
                    finished && i == result -> Palette.Wrong
                    else -> bubbleColors[i % bubbleColors.size]
                }
                Box(
                    Modifier
                        .offset(x = lane * i + (lane - bubble) / 2, y = travel * t.coerceAtMost(1f))
                        .size(bubble)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color.White, color)))
                        .border(3.dp, Color.White, CircleShape)
                        .clickable(enabled = !finished) { result = i },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        word.en,
                        style = MaterialTheme.typography.titleMedium.copy(textDirection = TextDirection.Ltr),
                        color = Palette.Ink,
                        maxLines = 1,
                        softWrap = false,
                        fontSize = if (word.en.length > 7) 13.sp else 17.sp,
                    )
                }
            }
        }
    }
}
