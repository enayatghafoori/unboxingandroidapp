package com.magicbox.kids.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.magicbox.kids.data.Catalog
import com.magicbox.kids.data.GameInfo
import com.magicbox.kids.data.Progress
import com.magicbox.kids.ui.components.KidButton
import com.magicbox.kids.ui.components.KidCard
import com.magicbox.kids.ui.components.RatingStars
import com.magicbox.kids.ui.components.ScreenBackground
import com.magicbox.kids.ui.components.SectionTitle
import com.magicbox.kids.ui.components.TopBar
import com.magicbox.kids.ui.components.fa
import com.magicbox.kids.ui.theme.Palette
import kotlin.random.Random

/** Simple grown-up check so children don't wander into settings. */
@Composable
fun ParentGate(onPassed: () -> Unit, onCancel: () -> Unit) {
    val a = remember { Random.nextInt(6, 10) }
    val b = remember { Random.nextInt(6, 10) }
    val answer = a * b
    val options = remember { (listOf(answer, answer + a, answer - b, answer + 3).distinct()).shuffled() }
    var wrong by remember { mutableStateOf(false) }
    ScreenBackground {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("👨‍👩‍👧 بخش والدین", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(12.dp))
            Text("برای ورود به این سؤال جواب دهید:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("${a.fa()} × ${b.fa()} = ؟", style = MaterialTheme.typography.displayLarge, color = Palette.Lavender)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                options.forEach { n ->
                    KidButton(n.fa(), color = Palette.Sky, height = 56.dp) {
                        if (n == answer) onPassed() else wrong = true
                    }
                }
            }
            if (wrong) {
                Spacer(Modifier.height(12.dp))
                Text("پاسخ درست نبود.", color = Palette.Wrong, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(24.dp))
            TextButton(onClick = onCancel) { Text("برگشت", style = MaterialTheme.typography.titleMedium) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParentScreen(
    catalog: Catalog,
    progress: Progress,
    today: Long,
    onSound: (Boolean) -> Unit,
    onDailyLimit: (Int) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
) {
    var confirmReset by rememberSaveable { mutableStateOf(false) }
    val owned = catalog.dolls.count { progress.owns(it.id) }
    val minutesToday = progress.usageSecondsOn(today) / 60

    ScreenBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar("بخش والدین", stars = null, onBack = onBack)
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SectionTitle("گزارش پیشرفت")
                KidCard(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatLine("⭐ ستاره‌ها", progress.stars.fa())
                        StatLine("🎁 جعبه‌های باز شده", progress.boxesOpened.fa())
                        StatLine("🧸 عروسک‌ها", "${owned.fa()} از ${catalog.dolls.size.fa()}")
                        StatLine("🎮 بازی‌های انجام‌شده", progress.gamesPlayed.fa())
                        StatLine("🔤 کلمه‌های انگلیسی یادگرفته", progress.learnedWords.size.fa())
                        StatLine("⏱️ استفادهٔ امروز", "${minutesToday.fa()} دقیقه")
                    }
                }
                if (progress.learnedWords.isNotEmpty()) {
                    KidCard(Modifier.fillMaxWidth()) {
                        Column {
                            Text("کلمه‌هایی که درست جواب داده:", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                progress.learnedWords.sorted().joinToString(" · "),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Palette.InkSoft,
                            )
                        }
                    }
                }
                KidCard(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("بهترین امتیاز بازی‌ها", style = MaterialTheme.typography.titleMedium)
                        GameInfo.entries.forEach { g ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${g.emoji} ${g.titleFa}", modifier = Modifier.weight(1f))
                                RatingStars(progress.bestScores[g.id] ?: 0, size = 16.dp)
                            }
                        }
                    }
                }

                SectionTitle("تنظیمات")
                KidCard(Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔊 صدا و تلفظ کلمه‌ها", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            Switch(checked = progress.soundOn, onCheckedChange = onSound)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("⏱️ محدودیت زمان روزانه", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "وقتی زمان تمام شود، صفحهٔ «وقت استراحت» نمایش داده می‌شود.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Palette.InkSoft,
                        )
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0, 15, 30, 45, 60).forEach { m ->
                                FilterChip(
                                    selected = progress.dailyLimitMinutes == m,
                                    onClick = { onDailyLimit(m) },
                                    label = { Text(if (m == 0) "بدون محدودیت" else "${m.fa()} دقیقه") },
                                )
                            }
                        }
                    }
                }
                KidCard(Modifier.fillMaxWidth()) {
                    Text(
                        "🔒 این برنامه هیچ خرید درون‌برنامه‌ای، تبلیغ یا ارسال اطلاعاتی ندارد. " +
                            "جعبه‌ها فقط با ستاره‌هایی باز می‌شوند که کودک با یادگیری به دست می‌آورد.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Palette.InkSoft,
                    )
                }
                KidButton(
                    "شروع دوباره (پاک کردن پیشرفت)",
                    emoji = "🗑️",
                    color = Palette.Wrong,
                    height = 52.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) { confirmReset = true }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("پاک کردن همه‌چیز؟") },
            text = { Text("همهٔ ستاره‌ها، عروسک‌ها و گزارش‌ها پاک می‌شوند و قابل برگشت نیستند.", textAlign = TextAlign.Start) },
            confirmButton = {
                TextButton(onClick = { confirmReset = false; onReset() }) { Text("بله، پاک کن", color = Palette.Wrong) }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("انصراف") } },
        )
    }
}

@Composable
private fun StatLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}
