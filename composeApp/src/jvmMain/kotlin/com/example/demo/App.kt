package com.example.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import com.example.demo.data.repo.CheckMode
import com.example.demo.presentation.viewmodel.MainViewModel
import com.example.demo.ui.EnglishFixerTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun App(vm: MainViewModel) {
    val s by vm.state.collectAsState()

    EnglishFixerTheme(darkTheme = s.isDarkTheme) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().padding(12.dp)) {

                TopConfigArea(
                    s = s,
                    vm = vm,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                FunctionalArea(
                    s = s,
                    vm = vm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // 关键：只拉伸这里
                )
            }
        }
    }
}


@Composable
fun ModeChipRow(current: CheckMode, onChange: (CheckMode) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FilterChip(
            selected = current == CheckMode.RewriteNatural,
            onClick = { onChange(CheckMode.RewriteNatural) },
            label = { Text("Natural") }
        )
        FilterChip(
            selected = current == CheckMode.RewriteFormal,
            onClick = { onChange(CheckMode.RewriteFormal) },
            label = { Text("Formal") }
        )
        FilterChip(
            selected = current == CheckMode.RewriteConcise,
            onClick = { onChange(CheckMode.RewriteConcise) },
            label = { Text("Concise") }
        )
    }
}

@Preview
@Composable
fun PreviewModeChipRow() {
    ModeChipRow(CheckMode.RewriteNatural) {}
}
