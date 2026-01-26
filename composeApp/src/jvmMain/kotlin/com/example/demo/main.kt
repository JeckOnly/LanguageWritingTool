package com.example.demo

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demo.di.initKoin
import com.example.demo.presentation.viewmodel.MainViewModel
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.java.KoinJavaComponent.getKoin
import java.awt.Dimension

fun main() = application {
    initKoin()

    val state = rememberWindowState(
        width = 1200.dp,
        height = 800.dp
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "English Fixer (Local LLM)",
        state = state
    ) {
        // ✅ 设置最小窗口尺寸（awt Dimension 是 px，所以要 dp->px）
        val density = LocalDensity.current
        LaunchedEffect(Unit) {
            val minW = with(density) { 1200.dp.roundToPx() }
            val minH = with(density) { 800.dp.roundToPx() }
            window.minimumSize = Dimension(minW, minH)
        }

        val vm: MainViewModel = viewModel {
            MainViewModel(getKoin().get(), getKoin().get())
        }

        DisposableEffect(Unit) {
            onDispose {
                vm.cancel()
                stopKoin()
            }
        }

        App(vm)
    }
}
