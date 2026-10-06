package com.magicbox.kids.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.magicbox.kids.data.Catalog
import com.magicbox.kids.data.Doll
import com.magicbox.kids.data.Progress
import com.magicbox.kids.data.Rarity
import com.magicbox.kids.data.SeriesKind
import com.magicbox.kids.data.UnboxResult
import com.magicbox.kids.ui.components.AssetImage
import com.magicbox.kids.ui.components.ConfettiBurst
import com.magicbox.kids.ui.components.DollLesson
import com.magicbox.kids.ui.components.KidButton
import com.magicbox.kids.ui.components.KidCard
import com.magicbox.kids.ui.components.LocalSfx
import com.magicbox.kids.ui.components.LocalSpeaker
import com.magicbox.kids.ui.components.ScreenBackground
import com.magicbox.kids.ui.components.TopBar
import com.magicbox.kids.ui.components.fa
import com.magicbox.kids.ui.theme.Palette
import kotlinx.coroutines.launch

private enum class Stage { SHAKE, CUT, OPEN, REVEAL }

private const val SHAKES_NEEDED = 3

@Composable
fun UnboxScreen(
    catalog: Catalog,
    progress: Progress,
    seriesId: String,
    free: Boolean,
    openBox: (String, Boolean) -> UnboxResult?,
    onAnother: () -> Unit,
    onCollection: () -> Unit,
    onHome: () -> Unit,
) {
    val series = catalog.series(seriesId)
    // The box is paid for exactly once, even if the screen is recreated.
    var dollId by rememberSaveable { mutableStateOf<String?>(null) }
    var isNew by rememberSaveable { mutableStateOf(false) }
    var shardsGained by rememberSaveable { mutableIntStateOf(0) }
    var failed by rememberSaveable { mutableStateOf(false) }
    var stageName by rememberSaveable { mutableStateOf(Stage.SHAKE.name) }
    val stage = Stage.valueOf(stageName)

    LaunchedEffect(Unit) {
        if (dollId == null && !failed) {
            val result = openBox(seriesId, free)
            if (result == null) {
                failed = true
            } else {
                dollId = result.doll.id
                isNew = result.isNew
                shardsGained = result.shardsGained
            }
        }
    }

    val bg = series?.let { Color(it.color) } ?: Palette.Lavender
    ScreenBackground(top = bg.copy(alpha = 0.35f)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(series?.titleFa ?: "", stars = progress.stars, shards = progress.shards, onBack = onHome)
            val doll = dollId?.let { catalog.doll(it) }
            when {
                failed -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("😅 ستاره کافی نداری!", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(16.dp))
                    KidButton("برگشت", emoji = "🏠", onClick = onHome)
                }
                doll == null -> Unit
                stage == Stage.REVEAL -> Reveal(
                    doll = doll,
                    isNew = isNew,
                    shardsGained = shardsGained,
                    isEnglish = series?.kind == SeriesKind.ENGLISH,
                    canAffordAnother = progress.stars >= catalog.boxCost,
                    onAnother = onAnother,
                    onCollection = onCollection,
                    onHome = onHome,
                )
                else -> BoxStages(
                    boxPath = series?.imagePath ?: "",
                    stage = stage,
                    accent = bg,
                    onStage = { stageName = it.name },
                )
            }
        }
    }
}

@Composable
private fun BoxStages(boxPath: String, stage: Stage, accent: Color, onStage: (Stage) -> Unit) {
    val haptics = LocalHapticFeedback.current
    val sfx = LocalSfx.current
    val scope = rememberCoroutineScope()
    val wobble = remember { Animatable(0f) }
    val lift = remember { Animatable(0f) }
    var shakes by remember { mutableIntStateOf(0) }
    var cut by remember { mutableFloatStateOf(0f) }

    val breathe by rememberInfiniteTransition(label = "breathe").animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "breatheValue",
    )

    val instruction = when (stage) {
        Stage.SHAKE -> "جعبه را تکان بده! (${(SHAKES_NEEDED - shakes).fa()} بار دیگر بزن)"
        Stage.CUT -> "✂️ با انگشت روی نوار چسب بکش تا پاره شود"
        Stage.OPEN -> "حالا در جعبه را بزن تا باز شود!"
        Stage.REVEAL -> ""
    }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(instruction, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(300.dp)
                .rotate(wobble.value)
                .scale(if (stage == Stage.SHAKE) breathe else 1f)
                .graphicsLayer {
                    translationY = -lift.value * 900f
                    alpha = 1f - lift.value
                    rotationZ = lift.value * 25f
                }
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    when (stage) {
                        Stage.SHAKE -> scope.launch {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            sfx.tap()
                            shakes++
                            wobble.animateTo(0f, keyframes {
                                durationMillis = 450
                                -14f at 60
                                12f at 140
                                -9f at 220
                                6f at 300
                                -3f at 380
                            })
                            if (shakes >= SHAKES_NEEDED) onStage(Stage.CUT)
                        }
                        Stage.OPEN -> scope.launch {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            sfx.reveal()
                            lift.animateTo(1f, tween(650))
                            onStage(Stage.REVEAL)
                        }
                        else -> Unit
                    }
                }
                .pointerInput(stage) {
                    if (stage != Stage.CUT) return@pointerInput
                    detectDragGestures { change, drag ->
                        change.consume()
                        val before = cut
                        cut = (cut + kotlin.math.abs(drag.x) / (size.width * 0.9f)).coerceAtMost(1f)
                        if ((before * 5).toInt() != (cut * 5).toInt()) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        if (cut >= 1f && before < 1f) {
                            sfx.tap()
                            onStage(Stage.OPEN)
                        }
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            AssetImage(boxPath, fallbackEmoji = "🎁", modifier = Modifier.fillMaxSize())
            if (stage == Stage.SHAKE || stage == Stage.CUT) {
                TapeStrip(cut = cut, accent = accent)
            }
        }
    }
}

/** A dashed tape across the box; the cut part disappears as the child swipes. */
@Composable
private fun TapeStrip(cut: Float, accent: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val y = size.height * 0.52f
        val startX = size.width * 0.08f
        val endX = size.width * 0.92f
        val cutX = startX + (endX - startX) * cut
        drawLine(
            color = Color.White.copy(alpha = 0.95f),
            start = Offset(cutX, y),
            end = Offset(endX, y),
            strokeWidth = 34f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = accent,
            start = Offset(cutX, y),
            end = Offset(endX, y),
            strokeWidth = 8f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 16f)),
        )
        if (cut > 0f) {
            drawCircle(Color.White, radius = 22f, center = Offset(cutX, y))
        }
    }
}

@Composable
private fun Reveal(
    doll: Doll,
    isNew: Boolean,
    shardsGained: Int,
    isEnglish: Boolean,
    canAffordAnother: Boolean,
    onAnother: () -> Unit,
    onCollection: () -> Unit,
    onHome: () -> Unit,
) {
    val speaker = LocalSpeaker.current
    val glowColor = Palette.rarity(doll.rarity)
    val pop = remember { Animatable(0f) }
    var showInfo by remember { mutableStateOf(false) }
    val spin by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "glowSpin",
    )

    LaunchedEffect(doll.id) {
        pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow))
        showInfo = true
        if (isEnglish && doll.word != null) {
            speaker.speakEnglish("Hi! I am ${doll.nameEn}. ${doll.word}!")
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                when {
                    doll.rarity == Rarity.SECRET && isNew -> "✨ وای! عروسک مخفی! ✨"
                    isNew -> "🎉 عروسک جدید!"
                    else -> "تکراری بود! +${shardsGained.fa()} تکهٔ الماس 💎"
                },
                style = MaterialTheme.typography.headlineMedium,
                color = if (isNew) Palette.Pink else Palette.Sky,
                textAlign = TextAlign.Center,
            )
            Box(Modifier.size(280.dp), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .size(260.dp)
                        .rotate(spin)
                        .scale(pop.value)
                        .background(
                            Brush.sweepGradient(
                                listOf(glowColor.copy(alpha = 0.55f), Color.Transparent, glowColor.copy(alpha = 0.55f),
                                    Color.Transparent, glowColor.copy(alpha = 0.55f)),
                            ),
                            CircleShape,
                        ),
                )
                AssetImage(
                    doll.imagePath,
                    fallbackEmoji = doll.emoji,
                    modifier = Modifier.size(240.dp).scale(pop.value),
                )
            }
            AnimatedVisibility(showInfo, enter = fadeIn() + slideInVertically { it / 3 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KidCard(Modifier.fillMaxWidth(), borderColor = glowColor) { DollLesson(doll) }
                    Spacer(Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        KidButton("کلکسیون", emoji = "🧸", color = Palette.Mint, height = 56.dp, onClick = onCollection)
                        KidButton("خانه", emoji = "🏠", color = Palette.Lavender, height = 56.dp, onClick = onHome)
                    }
                    Spacer(Modifier.height(10.dp))
                    KidButton(
                        "یک جعبهٔ دیگر",
                        emoji = "🎁",
                        color = Palette.Pink,
                        enabled = canAffordAnother,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onAnother,
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
        ConfettiBurst(key = doll.id)
    }
}
