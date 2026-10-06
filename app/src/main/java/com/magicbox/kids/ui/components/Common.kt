package com.magicbox.kids.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magicbox.kids.ui.theme.Palette
import kotlin.random.Random

/** Soft pastel gradient used behind every screen. */
@Composable
fun ScreenBackground(
    top: Color = Palette.LavenderLight,
    bottom: Color = Palette.Background,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(top, bottom))),
        content = content,
    )
}

/** Big, bouncy, rounded button that kids can hit easily. */
@Composable
fun KidButton(
    text: String,
    modifier: Modifier = Modifier,
    emoji: String? = null,
    color: Color = Palette.Lavender,
    enabled: Boolean = true,
    height: Dp = 64.dp,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, spring(dampingRatio = 0.45f), label = "press")
    val haptics = LocalHapticFeedback.current
    val sfx = LocalSfx.current
    val bg = if (enabled) color else Color(0xFFCFC8DA)
    Row(
        modifier = modifier
            .scale(scale)
            .shadow(if (enabled) 6.dp else 0.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(bg.copy(alpha = 0.85f), bg)))
            .clickable(interactionSource = interaction, indication = null, enabled = enabled) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                sfx.tap()
                onClick()
            }
            .padding(horizontal = 20.dp)
            .height(height),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (emoji != null) {
            Text(emoji, fontSize = 26.sp)
            Spacer(Modifier.width(10.dp))
        }
        Text(text, style = MaterialTheme.typography.titleLarge, color = Color.White, textAlign = TextAlign.Center)
    }
}

/** Little pill showing a counter such as stars or shards. */
@Composable
fun CounterPill(emoji: String, value: Int, modifier: Modifier = Modifier, color: Color = Palette.Sun) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .border(2.dp, color, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.width(6.dp))
        Text(value.fa(), style = MaterialTheme.typography.titleMedium)
    }
}

/** Top bar with a back button, a title and the child's wallet. */
@Composable
fun TopBar(title: String, stars: Int?, shards: Int? = null, onBack: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                // Back points to the right in a right-to-left layout.
                Text("➜", fontSize = 22.sp, color = Palette.Lavender, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(10.dp))
        }
        Text(title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
        if (shards != null) {
            CounterPill("💎", shards, color = Palette.Sky)
            Spacer(Modifier.width(6.dp))
        }
        if (stars != null) CounterPill("⭐", stars)
    }
}

/** White rounded card. */
@Composable
fun KidCard(
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    var m = modifier
        .shadow(4.dp, shape)
        .clip(shape)
        .background(Color.White)
    if (borderColor != null) m = m.border(3.dp, borderColor, shape)
    if (onClick != null) m = m.clickable(onClick = onClick)
    Box(m.padding(14.dp)) { content() }
}

/** Gentle up-and-down float for idle items. */
@Composable
fun rememberFloatOffset(amplitude: Float = 8f, durationMs: Int = 1600): Float {
    val transition = rememberInfiniteTransition(label = "float")
    val v by transition.animateFloat(
        initialValue = -amplitude,
        targetValue = amplitude,
        animationSpec = infiniteRepeatable(tween(durationMs, easing = LinearEasing), RepeatMode.Reverse),
        label = "floatValue",
    )
    return v
}

private data class Particle(
    val x: Float, val vx: Float, val vy: Float, val color: Color, val size: Float, val spin: Float,
)

/** One burst of confetti; restart it by changing [key]. */
@Composable
fun ConfettiBurst(key: Any, modifier: Modifier = Modifier) {
    val particles = remember(key) {
        val colors = listOf(Palette.Pink, Palette.Sun, Palette.Mint, Palette.Sky, Palette.Lavender, Palette.Peach)
        List(90) {
            Particle(
                x = Random.nextFloat(),
                vx = (Random.nextFloat() - 0.5f) * 0.6f,
                vy = 0.4f + Random.nextFloat() * 0.9f,
                color = colors.random(),
                size = 8f + Random.nextFloat() * 14f,
                spin = Random.nextFloat() * 720f,
            )
        }
    }
    val progress = remember(key) { Animatable(0f) }
    LaunchedEffect(key) { progress.animateTo(1f, tween(2600, easing = LinearEasing)) }
    Canvas(modifier.fillMaxSize()) {
        val t = progress.value
        if (t >= 1f) return@Canvas
        particles.forEach { p ->
            val x = (p.x + p.vx * t) * size.width
            val y = (-0.1f + p.vy * t + 0.6f * t * t) * size.height
            rotate(p.spin * t, pivot = Offset(x, y)) {
                drawRect(
                    color = p.color.copy(alpha = 1f - t * 0.6f),
                    topLeft = Offset(x, y),
                    size = Size(p.size, p.size * 0.6f),
                )
            }
        }
    }
}

/** Row of 0-3 rating stars. */
@Composable
fun RatingStars(rating: Int, size: Dp = 28.dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { i ->
            Text(
                if (i < rating) "⭐" else "☆",
                fontSize = (size.value).sp,
                color = if (i < rating) Palette.Sun else Palette.InkSoft,
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
fun CenteredColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) { content() }
}
