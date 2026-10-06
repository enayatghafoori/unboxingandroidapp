package com.magicbox.kids.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.magicbox.kids.data.Catalog
import com.magicbox.kids.data.Doll
import com.magicbox.kids.data.Progress
import com.magicbox.kids.data.Rarity
import com.magicbox.kids.ui.components.AssetImage
import com.magicbox.kids.ui.components.ConfettiBurst
import com.magicbox.kids.ui.components.DollLesson
import com.magicbox.kids.ui.components.KidButton
import com.magicbox.kids.ui.components.KidCard
import com.magicbox.kids.ui.components.ScreenBackground
import com.magicbox.kids.ui.components.TopBar
import com.magicbox.kids.ui.components.fa
import com.magicbox.kids.ui.theme.Palette

@Composable
fun CollectionScreen(
    catalog: Catalog,
    progress: Progress,
    onCraft: (String) -> Boolean,
    onBack: () -> Unit,
) {
    var seriesId by rememberSaveable { mutableStateOf(catalog.series.first().id) }
    var selected by remember { mutableStateOf<Doll?>(null) }
    var justCrafted by remember { mutableStateOf<String?>(null) }

    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar("کلکسیون من", stars = progress.stars, shards = progress.shards, onBack = onBack)
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                catalog.series.forEach { s ->
                    val active = s.id == seriesId
                    val color = Color(s.color)
                    Text(
                        "${s.emoji} ${s.titleFa}",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (active) Color.White else color,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (active) color else Color.White)
                            .border(2.dp, color, RoundedCornerShape(50))
                            .clickable { seriesId = s.id }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }
            val dolls = catalog.dollsIn(seriesId)
            val owned = dolls.count { progress.owns(it.id) }
            Text(
                "${owned.fa()} از ${dolls.size.fa()} عروسک" + if (owned == dolls.size) " — کلکسیون کامل شد! 🏆" else "",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(dolls, key = { it.id }) { doll ->
                    DollTile(doll, count = progress.owned[doll.id] ?: 0) { selected = doll }
                }
            }
        }
        justCrafted?.let { ConfettiBurst(key = it) }
    }

    selected?.let { doll ->
        Dialog(onDismissRequest = { selected = null }) {
            KidCard(Modifier.fillMaxWidth(), borderColor = Palette.rarity(doll.rarity)) {
                Column(
                    Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (progress.owns(doll.id)) {
                        AssetImage(doll.imagePath, doll.emoji, Modifier.size(200.dp))
                        DollLesson(doll)
                    } else {
                        val cost = catalog.craftCost[doll.rarity] ?: 0
                        AssetImage(doll.imagePath, doll.emoji, Modifier.size(160.dp), silhouette = true)
                        Text(
                            if (doll.rarity == Rarity.SECRET) "این عروسک مخفی است! 🤫" else "هنوز این عروسک را نداری",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "می‌توانی با ${cost.fa()} تکهٔ الماس 💎 آن را بسازی.\nتکه‌ها از عروسک‌های تکراری جمع می‌شوند.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = Palette.InkSoft,
                        )
                        Spacer(Modifier.height(12.dp))
                        KidButton(
                            "ساختن",
                            emoji = "💎",
                            color = Palette.Sky,
                            enabled = progress.shards >= cost,
                        ) {
                            if (onCraft(doll.id)) justCrafted = doll.id
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    KidButton("بستن", color = Palette.Lavender, height = 48.dp) { selected = null }
                }
            }
        }
    }
}

@Composable
private fun DollTile(doll: Doll, count: Int, onClick: () -> Unit) {
    val owned = count > 0
    val color = Palette.rarity(doll.rarity)
    Box(
        Modifier
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(20.dp))
            .background(if (owned) Color.White else Color.White.copy(alpha = 0.55f))
            .border(3.dp, if (owned) color else color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(6.dp),
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            AssetImage(
                doll.imagePath,
                fallbackEmoji = doll.emoji,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                silhouette = !owned,
            )
            Text(
                if (owned || doll.rarity != Rarity.SECRET) doll.nameFa else "؟؟؟",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
        if (count > 1) {
            Text(
                "×${count.fa()}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(50))
                    .background(color)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
    }
}
