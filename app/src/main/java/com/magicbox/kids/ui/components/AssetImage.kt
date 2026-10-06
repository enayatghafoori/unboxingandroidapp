package com.magicbox.kids.ui.components

import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private object ImageCache {
    private val cache = object : LruCache<String, ImageBitmap>(48 * 1024 * 1024) {
        override fun sizeOf(key: String, value: ImageBitmap) = value.width * value.height * 4
    }
    private val missing = mutableSetOf<String>()

    fun get(path: String): ImageBitmap? = cache.get(path)
    fun isMissing(path: String) = synchronized(missing) { path in missing }
    fun put(path: String, bitmap: ImageBitmap?) {
        if (bitmap != null) cache.put(path, bitmap) else synchronized(missing) { missing += path }
    }
}

/**
 * Draws an image from assets (generated artwork). If the file is not there yet,
 * falls back to a big emoji so the app is fully usable before the art exists.
 */
@Composable
fun AssetImage(
    path: String,
    fallbackEmoji: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    silhouette: Boolean = false,
) {
    val context = LocalContext.current
    val bitmap by produceState(initialValue = ImageCache.get(path), path) {
        if (value != null || ImageCache.isMissing(path)) return@produceState
        value = withContext(Dispatchers.IO) {
            val loaded = try {
                context.assets.open(path).use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
            } catch (e: Exception) {
                null
            }
            ImageCache.put(path, loaded)
            loaded
        }
    }
    val tint = if (silhouette) ColorFilter.tint(androidx.compose.ui.graphics.Color(0x33000000)) else null
    val image = bitmap
    if (image != null) {
        Image(
            bitmap = image,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
            colorFilter = tint,
        )
    } else {
        BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
            val side = when {
                constraints.hasBoundedWidth && constraints.hasBoundedHeight -> minOf(maxWidth, maxHeight)
                constraints.hasBoundedWidth -> maxWidth
                constraints.hasBoundedHeight -> maxHeight
                else -> 96.dp
            }
            val size = with(LocalDensity.current) { (side * 0.62f).toSp() }
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (silhouette) "❔" else fallbackEmoji,
                    style = TextStyle(fontSize = size),
                )
            }
        }
    }
}
