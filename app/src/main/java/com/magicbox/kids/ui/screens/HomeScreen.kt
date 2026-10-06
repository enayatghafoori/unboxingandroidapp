package com.magicbox.kids.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.magicbox.kids.data.Progress
import com.magicbox.kids.ui.components.AssetImage
import com.magicbox.kids.ui.components.CounterPill
import com.magicbox.kids.ui.components.KidButton
import com.magicbox.kids.ui.components.ScreenBackground
import com.magicbox.kids.ui.components.fa
import com.magicbox.kids.ui.components.rememberFloatOffset
import com.magicbox.kids.ui.theme.Palette

@Composable
fun HomeScreen(
    progress: Progress,
    freeBoxReady: Boolean,
    ownedCount: Int,
    totalDolls: Int,
    onBoxes: () -> Unit,
    onGames: () -> Unit,
    onCollection: () -> Unit,
    onParents: () -> Unit,
) {
    ScreenBackground {
        AssetImage(
            path = "images/ui/home_bg.webp",
            fallbackEmoji = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row {
                    CounterPill("⭐", progress.stars)
                    Spacer(Modifier.size(6.dp))
                    CounterPill("💎", progress.shards, color = Palette.Sky)
                }
                TextButton(onClick = onParents) {
                    Text("👨‍👩‍👧 والدین", style = MaterialTheme.typography.labelLarge, color = Palette.InkSoft)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("جعبه جادو", style = MaterialTheme.typography.displayLarge, color = Palette.Lavender)
            Text("باز کن، بازی کن، یاد بگیر!", style = MaterialTheme.typography.titleMedium, color = Palette.InkSoft)

            Box(Modifier.size(220.dp).offset(y = rememberFloatOffset().dp), contentAlignment = Alignment.Center) {
                AssetImage("images/ui/mascot.webp", fallbackEmoji = "🎁", modifier = Modifier.fillMaxSize())
            }

            Text(
                "کلکسیون من: ${ownedCount.fa()} از ${totalDolls.fa()}",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(16.dp))

            KidButton(
                text = if (freeBoxReady) "جعبهٔ رایگان امروز!" else "باز کردن جعبه",
                emoji = "🎁",
                color = Palette.Pink,
                modifier = Modifier.fillMaxWidth(),
                height = 76.dp,
                onClick = onBoxes,
            )
            Spacer(Modifier.height(14.dp))
            KidButton(
                text = "بازی و یادگیری",
                emoji = "🎮",
                color = Palette.Lavender,
                modifier = Modifier.fillMaxWidth(),
                height = 76.dp,
                onClick = onGames,
            )
            Spacer(Modifier.height(14.dp))
            KidButton(
                text = "کلکسیون من",
                emoji = "🧸",
                color = Palette.Mint,
                modifier = Modifier.fillMaxWidth(),
                height = 76.dp,
                onClick = onCollection,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
