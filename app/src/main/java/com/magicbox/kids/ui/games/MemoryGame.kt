package com.magicbox.kids.ui.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magicbox.kids.data.MemoryCard
import com.magicbox.kids.ui.components.LocalSfx
import com.magicbox.kids.ui.components.LocalSpeaker
import com.magicbox.kids.ui.components.fa
import com.magicbox.kids.ui.theme.Palette
import kotlinx.coroutines.delay

/** Flip two cards; match each picture with its English word. */
@Composable
fun MemoryGame(cards: List<MemoryCard>, onFinish: GameFinished) {
    val speaker = LocalSpeaker.current
    val sfx = LocalSfx.current
    val open = remember { mutableStateListOf<Int>() }
    val matched = remember { mutableStateListOf<Int>() }
    var mistakes by remember { mutableIntStateOf(0) }
    val pairs = cards.size / 2

    LaunchedEffect(open.size) {
        if (open.size != 2) return@LaunchedEffect
        val (a, b) = open[0] to open[1]
        if (cards[a].pairId == cards[b].pairId) {
            sfx.correct()
            matched += cards[a].pairId
            cards.firstOrNull { it.pairId == cards[a].pairId && !it.isEmoji }?.let { speaker.speakEnglish(it.text) }
            delay(400)
        } else {
            mistakes++
            delay(900)
        }
        if (matched.size == pairs) {
            delay(600)
            // A few misses are part of the game; after that each two misses cost a point.
            val score = (pairs - maxOf(0, mistakes - 4) / 2).coerceIn(2, pairs)
            val learned = cards.filter { !it.isEmoji }.map { it.text }
            onFinish(score, pairs, learned)
        } else {
            // Last statement: clearing restarts this effect, so nothing may follow it.
            open.clear()
        }
    }

    Column(Modifier.fillMaxSize()) {
        Text(
            "جفت‌های پیدا شده: ${matched.size.fa()} از ${pairs.fa()}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(cards) { i, card ->
                val faceUp = i in open || card.pairId in matched
                val rotation by animateFloatAsState(if (faceUp) 180f else 0f, tween(350), label = "flip")
                Box(
                    Modifier
                        .aspectRatio(0.8f)
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 12f * density
                        }
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            when {
                                card.pairId in matched -> Palette.Correct.copy(alpha = 0.2f)
                                rotation > 90f -> Color.White
                                else -> Palette.Lavender
                            },
                        )
                        .border(3.dp, Palette.LavenderLight, RoundedCornerShape(18.dp))
                        .clickable(enabled = !faceUp && open.size < 2) {
                            sfx.tap()
                            open += i
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    // Content is mirrored back so it reads correctly after the flip.
                    Box(Modifier.graphicsLayer { rotationY = if (rotation > 90f) 180f else 0f }) {
                        when {
                            rotation <= 90f -> Text("❓", fontSize = 34.sp)
                            card.isEmoji -> Text(card.text, fontSize = 44.sp)
                            else -> Text(
                                card.text,
                                style = MaterialTheme.typography.titleLarge.copy(textDirection = TextDirection.Ltr),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}
