package com.magicbox.kids.ui.games

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magicbox.kids.data.ChoiceOption
import com.magicbox.kids.data.ChoiceQuestion
import com.magicbox.kids.ui.components.KidButton
import com.magicbox.kids.ui.components.LocalSfx
import com.magicbox.kids.ui.components.LocalSpeaker
import com.magicbox.kids.ui.components.fa
import com.magicbox.kids.ui.theme.Palette
import kotlinx.coroutines.delay

@Composable
fun RoundHeader(index: Int, total: Int) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(
            "مرحلهٔ ${(index + 1).fa()} از ${total.fa()}",
            style = MaterialTheme.typography.labelLarge,
            color = Palette.InkSoft,
        )
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (index + 1f) / total },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50)),
            color = Palette.Mint,
            trackColor = Color.White,
        )
    }
}

/** Shared engine for every multiple-choice mini-game. */
@Composable
fun ChoiceGame(questions: List<ChoiceQuestion>, onFinish: GameFinished) {
    val speaker = LocalSpeaker.current
    val sfx = LocalSfx.current
    val haptics = LocalHapticFeedback.current
    var index by remember { mutableIntStateOf(0) }
    var correct by remember { mutableIntStateOf(0) }
    var picked by remember { mutableStateOf<Int?>(null) }
    val learned = remember { mutableStateListOf<String>() }

    if (questions.isEmpty()) {
        LaunchedEffect(Unit) { onFinish(0, 0, emptyList()) }
        return
    }
    val q = questions[index]

    LaunchedEffect(index) {
        delay(350)
        q.speech?.let { if (it.english) speaker.speakEnglish(it.text) else speaker.speakPersian(it.text) }
    }
    LaunchedEffect(picked) {
        val p = picked ?: return@LaunchedEffect
        val right = p == q.answerIndex
        if (right) {
            correct++
            q.learnedKey?.let { learned += it }
            sfx.correct()
            q.speech?.let { if (it.english) speaker.speakEnglish(it.text) }
        } else {
            sfx.wrong()
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        delay(if (right) 1100 else 1800)
        if (index + 1 >= questions.size) {
            onFinish(correct, questions.size, learned.toList())
        } else {
            picked = null
            index++
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        RoundHeader(index, questions.size)
        AnimatedContent(targetState = index, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "question") { i ->
            val question = questions[i]
            Column(
                Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(question.instructionFa, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                if (question.listenOnly) {
                    KidButton("دوباره بشنو", emoji = "🔊", color = Palette.Sky, height = 72.dp) {
                        question.speech?.let { speaker.speakEnglish(it.text) }
                    }
                } else {
                    question.promptBig?.let { big ->
                        val isEmoji = big.length <= 4 && big.none { it.isLetter() }
                        Text(
                            big,
                            fontSize = if (isEmoji) 96.sp else 26.sp,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                textDirection = if (question.promptBigLtr) TextDirection.Ltr else TextDirection.Content,
                            ),
                            color = Palette.Lavender,
                            textAlign = TextAlign.Center,
                            lineHeight = if (isEmoji) 110.sp else 40.sp,
                        )
                    }
                    question.promptSmall?.let {
                        Text("( $it )", style = MaterialTheme.typography.titleMedium, color = Palette.InkSoft)
                    }
                }
                Spacer(Modifier.height(20.dp))
                val emojiGrid = question.options.all { it.emoji != null && it.label.isEmpty() }
                if (emojiGrid) {
                    question.options.chunked(2).forEachIndexed { row, pair ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            pair.forEachIndexed { col, opt ->
                                val optIndex = row * 2 + col
                                OptionTile(opt, optIndex, question.answerIndex, picked.takeIf { i == index }, Modifier.weight(1f).aspectRatio(1f)) {
                                    if (picked == null && i == index) picked = optIndex
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                } else {
                    question.options.forEachIndexed { optIndex, opt ->
                        OptionTile(opt, optIndex, question.answerIndex, picked.takeIf { i == index }, Modifier.fillMaxWidth().heightIn(min = 64.dp)) {
                            if (picked == null && i == index) picked = optIndex
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionTile(
    option: ChoiceOption,
    index: Int,
    answer: Int,
    picked: Int?,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val border = when {
        picked == null -> Palette.LavenderLight
        index == answer -> Palette.Correct
        index == picked -> Palette.Wrong
        else -> Palette.LavenderLight
    }
    val bg = when {
        picked != null && index == answer -> Palette.Correct.copy(alpha = 0.15f)
        picked != null && index == picked -> Palette.Wrong.copy(alpha = 0.15f)
        else -> Color.White
    }
    Box(
        modifier
            .clip(RoundedCornerShape(22.dp))
            .background(bg)
            .border(4.dp, border, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (option.emoji != null && option.label.isEmpty()) {
            Text(option.emoji, fontSize = 64.sp)
        } else {
            Text(
                option.label,
                style = MaterialTheme.typography.titleLarge.copy(
                    textDirection = if (option.ltr) TextDirection.Ltr else TextDirection.Content,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}
