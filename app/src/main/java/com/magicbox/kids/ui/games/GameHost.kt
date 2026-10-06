package com.magicbox.kids.ui.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.magicbox.kids.data.Catalog
import com.magicbox.kids.data.GameInfo
import com.magicbox.kids.data.GameRules
import com.magicbox.kids.data.Progress
import com.magicbox.kids.data.QuestionFactory
import com.magicbox.kids.ui.components.ConfettiBurst
import com.magicbox.kids.ui.components.KidButton
import com.magicbox.kids.ui.components.RatingStars
import com.magicbox.kids.ui.components.ScreenBackground
import com.magicbox.kids.ui.components.TopBar
import com.magicbox.kids.ui.components.fa
import com.magicbox.kids.ui.theme.Palette

/** Called by every mini-game when it ends. */
typealias GameFinished = (correct: Int, total: Int, learned: List<String>) -> Unit

private data class Outcome(val correct: Int, val total: Int, val stars: Int)

@Composable
fun GameHost(
    game: GameInfo,
    catalog: Catalog,
    progress: Progress,
    onRecord: (gameId: String, correct: Int, total: Int, learned: List<String>) -> Int,
    onExit: () -> Unit,
) {
    // Bumping the round number rebuilds the questions for a fresh play.
    var round by remember { mutableIntStateOf(0) }
    var outcome by remember { mutableStateOf<Outcome?>(null) }
    val factory = remember { QuestionFactory(catalog) }

    val finish: GameFinished = { correct, total, learned ->
        val stars = onRecord(game.id, correct, total, learned)
        outcome = Outcome(correct, total, stars)
    }

    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar(game.titleFa, stars = progress.stars, onBack = onExit)
            Box(Modifier.weight(1f).fillMaxWidth().navigationBarsPadding()) {
                val result = outcome
                if (result != null) {
                    ResultView(result, onAgain = { outcome = null; round++ }, onExit = onExit)
                } else {
                    androidx.compose.runtime.key(round) {
                        when (game) {
                            GameInfo.LISTEN -> ChoiceGame(remember { factory.listen() }, finish)
                            GameInfo.PICTURE -> ChoiceGame(remember { factory.picture() }, finish)
                            GameInfo.RHYME -> ChoiceGame(remember { factory.rhyme() }, finish)
                            GameInfo.VERSE -> ChoiceGame(remember { factory.verse() }, finish)
                            GameInfo.PROVERB -> ChoiceGame(remember { factory.proverb() }, finish)
                            GameInfo.SPELL -> SpellGame(remember { factory.spell() }, finish)
                            GameInfo.MEMORY -> MemoryGame(remember { factory.memory() }, finish)
                            GameInfo.CATCH -> CatchGame(remember { factory.catch() }, finish)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultView(result: Outcome, onAgain: () -> Unit, onExit: () -> Unit) {
    val rating = GameRules.ratingFor(result.correct, result.total)
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                when (rating) {
                    3 -> "عالی بود! 🏆"
                    2 -> "آفرین! 👏"
                    1 -> "خوب بود! 🙂"
                    else -> "دوباره تلاش کن! 💪"
                },
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(Modifier.height(12.dp))
            RatingStars(rating, size = 48.dp)
            Spacer(Modifier.height(12.dp))
            Text(
                "${result.correct.fa()} جواب درست از ${result.total.fa()}",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                "+${result.stars.fa()} ستاره ⭐",
                style = MaterialTheme.typography.headlineMedium,
                color = Palette.Sun,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.dp))
            KidButton("دوباره بازی کن", emoji = "🔁", color = Palette.Pink, modifier = Modifier.fillMaxWidth(), onClick = onAgain)
            Spacer(Modifier.height(12.dp))
            KidButton("بازی‌های دیگر", emoji = "🎮", color = Palette.Lavender, modifier = Modifier.fillMaxWidth(), onClick = onExit)
        }
        if (rating >= 2) ConfettiBurst(key = result)
    }
}
