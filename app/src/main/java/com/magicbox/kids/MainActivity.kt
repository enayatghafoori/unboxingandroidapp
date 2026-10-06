package com.magicbox.kids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.magicbox.kids.audio.ToneSfx
import com.magicbox.kids.audio.TtsSpeaker
import com.magicbox.kids.ui.MagicBoxApp
import com.magicbox.kids.ui.components.LocalSfx
import com.magicbox.kids.ui.components.LocalSpeaker
import com.magicbox.kids.ui.theme.MagicBoxTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val vm: AppViewModel by viewModels()
    private lateinit var speaker: TtsSpeaker
    private lateinit var sfx: ToneSfx

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        speaker = TtsSpeaker(this)
        sfx = ToneSfx()

        // Count screen time only while the app is actually on screen.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (true) {
                    delay(USAGE_TICK_SECONDS * 1000L)
                    vm.addUsage(USAGE_TICK_SECONDS)
                }
            }
        }

        setContent {
            MagicBoxTheme {
                CompositionLocalProvider(LocalSpeaker provides speaker, LocalSfx provides sfx) {
                    MagicBoxApp(vm)
                }
            }
        }
    }

    override fun onDestroy() {
        speaker.shutdown()
        sfx.release()
        super.onDestroy()
    }

    private companion object {
        const val USAGE_TICK_SECONDS = 15
    }
}
