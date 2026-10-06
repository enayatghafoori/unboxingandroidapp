package com.magicbox.kids.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magicbox.kids.ui.components.ScreenBackground
import com.magicbox.kids.ui.components.rememberFloatOffset
import com.magicbox.kids.ui.theme.Palette

/** Shown when the daily time limit set by a parent runs out. */
@Composable
fun BreakScreen(onParents: () -> Unit) {
    ScreenBackground(top = Color(0xFF2E2A5C), bottom = Color(0xFF5B4B9A)) {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("🌙", fontSize = 110.sp, modifier = Modifier.offset(y = rememberFloatOffset(10f).dp))
            Spacer(Modifier.height(16.dp))
            Text(
                "وقت استراحته!",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "عروسک‌هایت هم خسته شده‌اند و می‌خواهند بخوابند.\nفردا دوباره با هم بازی می‌کنیم! 💤",
                style = MaterialTheme.typography.titleMedium,
                color = Palette.LavenderLight,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(40.dp))
            TextButton(onClick = onParents) {
                Text("👨‍👩‍👧 والدین", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
