package com.magicbox.kids.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.magicbox.kids.R
import com.magicbox.kids.data.Rarity

object Palette {
    val Background = Color(0xFFFFF4E8)
    val Lavender = Color(0xFF8E6CF0)
    val LavenderLight = Color(0xFFE9E1FF)
    val Pink = Color(0xFFFF7EB3)
    val Peach = Color(0xFFFFB38A)
    val Mint = Color(0xFF6FD3A6)
    val Sky = Color(0xFF6EC3F4)
    val Sun = Color(0xFFFFC94D)
    val Ink = Color(0xFF3B2A5C)
    val InkSoft = Color(0xFF7A6A99)
    val Correct = Color(0xFF4CC38A)
    val Wrong = Color(0xFFFF6B6B)
    val Card = Color(0xFFFFFFFF)

    fun rarity(r: Rarity): Color = when (r) {
        Rarity.COMMON -> Sky
        Rarity.RARE -> Pink
        Rarity.SECRET -> Sun
    }
}

val Vazirmatn = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
    Font(R.font.vazirmatn_black, FontWeight.Black),
)

private val base = TextStyle(fontFamily = Vazirmatn, color = Palette.Ink)

private val typography = Typography(
    displayLarge = base.copy(fontSize = 40.sp, fontWeight = FontWeight.Black),
    headlineLarge = base.copy(fontSize = 30.sp, fontWeight = FontWeight.Black),
    headlineMedium = base.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
    titleLarge = base.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
    titleMedium = base.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
    bodyLarge = base.copy(fontSize = 17.sp),
    bodyMedium = base.copy(fontSize = 15.sp),
    labelLarge = base.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
    labelMedium = base.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
)

private val colors = lightColorScheme(
    primary = Palette.Lavender,
    onPrimary = Color.White,
    secondary = Palette.Pink,
    onSecondary = Color.White,
    tertiary = Palette.Mint,
    background = Palette.Background,
    onBackground = Palette.Ink,
    surface = Palette.Card,
    onSurface = Palette.Ink,
)

@Composable
fun MagicBoxTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, typography = typography, content = content)
}
