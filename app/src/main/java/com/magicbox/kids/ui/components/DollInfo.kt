package com.magicbox.kids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.magicbox.kids.data.Doll
import com.magicbox.kids.ui.theme.Palette

@Composable
fun RarityChip(doll: Doll) {
    val c = Palette.rarity(doll.rarity)
    Text(
        text = if (doll.rarity.key == "secret") "✨ ${doll.rarity.labelFa}" else doll.rarity.labelFa,
        style = MaterialTheme.typography.labelMedium,
        color = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(c)
            .padding(horizontal = 12.dp, vertical = 3.dp),
    )
}

/** What a doll teaches: an English word, or a Persian verse/proverb. */
@Composable
fun DollLesson(doll: Doll, modifier: Modifier = Modifier) {
    val speaker = LocalSpeaker.current
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(doll.nameFa, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        RarityChip(doll)
        Spacer(Modifier.height(10.dp))
        if (doll.word != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    doll.word,
                    style = MaterialTheme.typography.displayLarge.copy(textDirection = TextDirection.Ltr),
                    color = Palette.Lavender,
                )
                Spacer(Modifier.width(10.dp))
                Text("= ${doll.wordFa}", style = MaterialTheme.typography.headlineMedium, color = Palette.InkSoft)
            }
            if (doll.sentence != null) {
                Text(
                    doll.sentence,
                    style = MaterialTheme.typography.titleLarge.copy(textDirection = TextDirection.Ltr),
                    textAlign = TextAlign.Center,
                )
                Text(doll.sentenceFa ?: "", style = MaterialTheme.typography.bodyLarge, color = Palette.InkSoft)
            }
            Spacer(Modifier.height(10.dp))
            Row {
                KidButton("کلمه", emoji = "🔊", height = 48.dp, color = Palette.Sky) { speaker.speakEnglish(doll.word) }
                Spacer(Modifier.width(10.dp))
                if (doll.sentence != null) {
                    KidButton("جمله", emoji = "🗣️", height = 48.dp, color = Palette.Mint) {
                        speaker.speakEnglish(doll.sentence)
                    }
                }
            }
        } else if (doll.textFa != null) {
            Text(
                doll.textFa.replace(" / ", "\n"),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = Palette.Lavender,
            )
            if (doll.sourceFa != null) {
                Text("— ${doll.sourceFa}", style = MaterialTheme.typography.labelLarge, color = Palette.InkSoft)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "💡 ${doll.meaningFa ?: ""}",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            if (speaker.canSpeakPersian) {
                Spacer(Modifier.height(10.dp))
                KidButton("بخوان", emoji = "🔊", height = 48.dp, color = Palette.Sky) {
                    speaker.speakPersian(doll.textFa.replace(" / ", "، "))
                }
            }
        }
    }
}
