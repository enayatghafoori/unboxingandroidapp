package com.magicbox.kids.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.magicbox.kids.AppViewModel
import com.magicbox.kids.data.GameInfo
import com.magicbox.kids.ui.components.LocalSfx
import com.magicbox.kids.ui.components.LocalSpeaker
import com.magicbox.kids.ui.games.GameHost
import com.magicbox.kids.ui.screens.BreakScreen
import com.magicbox.kids.ui.screens.CollectionScreen
import com.magicbox.kids.ui.screens.GamesScreen
import com.magicbox.kids.ui.screens.HomeScreen
import com.magicbox.kids.ui.screens.ParentGate
import com.magicbox.kids.ui.screens.ParentScreen
import com.magicbox.kids.ui.screens.ShopScreen
import com.magicbox.kids.ui.screens.UnboxScreen

private object Routes {
    const val HOME = "home"
    const val SHOP = "shop"
    const val UNBOX = "unbox/{series}/{free}"
    const val COLLECTION = "collection"
    const val GAMES = "games"
    const val GAME = "game/{id}"
    const val PARENT_GATE = "parentGate"
    const val PARENT = "parent"

    fun unbox(series: String, free: Boolean) = "unbox/$series/$free"
    fun game(id: String) = "game/$id"
}

private fun NavHostController.goHome() {
    popBackStack(Routes.HOME, inclusive = false)
}

@Composable
fun MagicBoxApp(vm: AppViewModel) {
    val progress by vm.progress.collectAsStateWithLifecycle()
    val speaker = LocalSpeaker.current
    val sfx = LocalSfx.current
    LaunchedEffect(progress.soundOn) {
        speaker.enabled = progress.soundOn
        sfx.enabled = progress.soundOn
    }

    val today = vm.today()
    var breakUnlockGate by rememberSaveable { mutableStateOf(false) }

    // The whole UI is Persian, so lay it out right-to-left regardless of the device language.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (progress.isLimitReached(today)) {
            if (breakUnlockGate) {
                ParentGate(
                    onPassed = { breakUnlockGate = false; vm.unlockForToday() },
                    onCancel = { breakUnlockGate = false },
                )
            } else {
                BreakScreen(onParents = { breakUnlockGate = true })
            }
            return@CompositionLocalProvider
        }

        val nav = rememberNavController()
        val catalog = vm.catalog
        NavHost(navController = nav, startDestination = Routes.HOME) {
            composable(Routes.HOME) {
                HomeScreen(
                    progress = progress,
                    freeBoxReady = progress.freeBoxAvailable(today),
                    ownedCount = catalog.dolls.count { progress.owns(it.id) },
                    totalDolls = catalog.dolls.size,
                    onBoxes = { nav.navigate(Routes.SHOP) },
                    onGames = { nav.navigate(Routes.GAMES) },
                    onCollection = { nav.navigate(Routes.COLLECTION) },
                    onParents = { nav.navigate(Routes.PARENT_GATE) },
                )
            }
            composable(Routes.SHOP) {
                ShopScreen(
                    catalog = catalog,
                    progress = progress,
                    freeBoxReady = progress.freeBoxAvailable(today),
                    onOpen = { series, free -> nav.navigate(Routes.unbox(series, free)) },
                    onBack = { nav.popBackStack() },
                )
            }
            composable(
                Routes.UNBOX,
                arguments = listOf(
                    navArgument("series") { type = NavType.StringType },
                    navArgument("free") { type = NavType.BoolType },
                ),
            ) { entry ->
                val series = entry.arguments?.getString("series") ?: return@composable
                val free = entry.arguments?.getBoolean("free") ?: false
                UnboxScreen(
                    catalog = catalog,
                    progress = progress,
                    seriesId = series,
                    free = free,
                    openBox = vm::openBox,
                    onAnother = { nav.popBackStack(Routes.SHOP, inclusive = false) },
                    onCollection = {
                        nav.navigate(Routes.COLLECTION) { popUpTo(Routes.HOME) }
                    },
                    onHome = { nav.goHome() },
                )
            }
            composable(Routes.COLLECTION) {
                CollectionScreen(
                    catalog = catalog,
                    progress = progress,
                    onCraft = vm::craft,
                    onBack = { nav.popBackStack() },
                )
            }
            composable(Routes.GAMES) {
                GamesScreen(
                    progress = progress,
                    onPlay = { nav.navigate(Routes.game(it.id)) },
                    onBack = { nav.popBackStack() },
                )
            }
            composable(Routes.GAME, arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
                val game = GameInfo.fromId(entry.arguments?.getString("id") ?: "") ?: return@composable
                GameHost(
                    game = game,
                    catalog = catalog,
                    progress = progress,
                    onRecord = vm::recordGame,
                    onExit = { nav.popBackStack() },
                )
            }
            composable(Routes.PARENT_GATE) {
                ParentGate(
                    onPassed = {
                        nav.navigate(Routes.PARENT) { popUpTo(Routes.PARENT_GATE) { inclusive = true } }
                    },
                    onCancel = { nav.popBackStack() },
                )
            }
            composable(Routes.PARENT) {
                ParentScreen(
                    catalog = catalog,
                    progress = progress,
                    today = today,
                    onSound = vm::setSound,
                    onDailyLimit = vm::setDailyLimit,
                    onReset = vm::resetProgress,
                    onBack = { nav.popBackStack() },
                )
            }
        }
    }
}
