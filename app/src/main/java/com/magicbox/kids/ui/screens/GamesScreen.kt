package com.magicbox.kids.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magicbox.kids.data.GameCategory
import com.magicbox.kids.data.GameInfo
import com.magicbox.kids.data.Progress
import com.magicbox.kids.ui.components.AssetImage
import com.magicbox.kids.ui.components.KidCard
import com.magicbox.kids.ui.components.RatingStars
import com.magicbox.kids.ui.components.ScreenBackground
import com.magicbox.kids.ui.components.SectionTitle
import com.magicbox.kids.ui.components.TopBar
import com.magicbox.kids.ui.theme.Palette

@Composable
fun GamesScreen(progress: Progress, onPlay: (GameInfo) -> Unit, onBack: () -> Unit) {
    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar("بازی‌ها", stars = progress.stars, onBack = onBack)
            LazyColumn(
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AssetImage("images/ui/games.webp", "🎲", Modifier.size(90.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "هر جواب درست = ۲ ستاره ⭐\nهمه را درست بگویی = ۵ ستارهٔ جایزه!",
                            style = MaterialTheme.typography.titleMedium,
                            color = Palette.InkSoft,
                        )
                    }
                }
                GameCategory.entries.forEach { category ->
                    item(key = category.name) { SectionTitle(category.titleFa) }
                    items(GameInfo.entries.filter { it.category == category }, key = { it.id }) { game ->
                        KidCard(
                            Modifier.fillMaxWidth(),
                            borderColor = if (category == GameCategory.ENGLISH) Palette.Sky else Palette.Lavender,
                            onClick = { onPlay(game) },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(game.emoji, fontSize = 40.sp)
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(game.titleFa, style = MaterialTheme.typography.titleLarge)
                                    Text(game.descriptionFa, style = MaterialTheme.typography.bodyMedium, color = Palette.InkSoft)
                                }
                                RatingStars(progress.bestScores[game.id] ?: 0, size = 18.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}
