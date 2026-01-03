package com.example.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.demo.di.initKoin
import com.example.demo.domain.CheckEnglishUseCase
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() = application {

    initKoin()

    // 2) 取出 MainViewModel
    val vm: MainViewModel = getKoin().get()

    Window(
        onCloseRequest = {
            vm.cancel()       // 你已有的 cancel/close
            stopKoin()        // 可选：退出时关闭 Koin
            exitApplication()
        },
        title = "English Fixer (Local LLM)"
    ) {
        App(vm)
    }
}
