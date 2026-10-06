package com.magicbox.kids.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.magicbox.kids.data.Catalog
import com.magicbox.kids.data.Progress
import com.magicbox.kids.ui.components.AssetImage
import com.magicbox.kids.ui.components.KidButton
import com.magicbox.kids.ui.components.KidCard
import com.magicbox.kids.ui.components.ScreenBackground
import com.magicbox.kids.ui.components.TopBar
import com.magicbox.kids.ui.components.fa
import com.magicbox.kids.ui.components.rememberFloatOffset
import com.magicbox.kids.ui.theme.Palette

@Composable
fun ShopScreen(
    catalog: Catalog,
    progress: Progress,
    freeBoxReady: Boolean,
    onOpen: (seriesId: String, free: Boolean) -> Unit,
    onBack: () -> Unit,
) {
    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar("جعبه‌ها", stars = progress.stars, shards = progress.shards, onBack = onBack)
            if (freeBoxReady) {
                Text(
                    "🎉 امروز یک جعبهٔ رایگان داری! یکی را انتخاب کن.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Palette.Pink,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(catalog.series, key = { it.id }) { series ->
                    val dolls = catalog.dollsIn(series.id)
                    val owned = dolls.count { progress.owns(it.id) }
                    val color = Color(series.color)
                    KidCard(Modifier.fillMaxWidth(), borderColor = color) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AssetImage(
                                path = series.imagePath,
                                fallbackEmoji = "🎁",
                                modifier = Modifier.size(120.dp).offset(y = rememberFloatOffset(5f).dp),
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("${series.emoji} ${series.titleFa}", style = MaterialTheme.typography.titleLarge)
                                Text(series.subjectFa, style = MaterialTheme.typography.bodyMedium, color = Palette.InkSoft)
                                Text(
                                    "جمع‌شده: ${owned.fa()} از ${dolls.size.fa()}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = color,
                                )
                                Spacer(Modifier.height(8.dp))
                                if (freeBoxReady) {
                                    KidButton(
                                        text = "رایگان",
                                        emoji = "🎁",
                                        color = Palette.Pink,
                                        height = 52.dp,
                                        modifier = Modifier.fillMaxWidth(),
                                    ) { onOpen(series.id, true) }
                                    Spacer(Modifier.height(8.dp))
                                }
                                KidButton(
                                    text = "${catalog.boxCost.fa()} ستاره",
                                    emoji = "⭐",
                                    color = color,
                                    height = 52.dp,
                                    enabled = progress.stars >= catalog.boxCost,
                                    modifier = Modifier.fillMaxWidth(),
                                ) { onOpen(series.id, false) }
                            }
                        }
                    }
                }
                item {
                    if (!freeBoxReady && progress.stars < catalog.boxCost) {
                        Text(
                            "ستاره کم داری! با بازی کردن ستاره جمع کن ⭐",
                            style = MaterialTheme.typography.titleMedium,
                            color = Palette.InkSoft,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
