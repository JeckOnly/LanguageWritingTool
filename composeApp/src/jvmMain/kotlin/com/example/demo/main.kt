package com.example.demo

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demo.di.initKoin
import com.example.demo.presentation.viewmodel.MainViewModel
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "English Fixer (Local LLM)"
    ) {
        val vm: MainViewModel = viewModel {
            MainViewModel(getKoin().get(), getKoin().get())
        }

        // 关窗时做清理（比把 vm 放到 onCloseRequest 更自然）
        DisposableEffect(Unit) {
            onDispose {
                vm.cancel()
                stopKoin()
            }
        }

        App(vm)
    }
}
