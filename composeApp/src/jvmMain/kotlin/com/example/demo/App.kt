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
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun App(vm: MainViewModel) {
    val s by vm.state.collectAsState()

    MaterialTheme {
        Column(Modifier.fillMaxSize().padding(12.dp)) {

            // 顶部：本地服务配置
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = s.baseUrl,
                    onValueChange = vm::setBaseUrl,
                    label = { Text("Base URL (OpenAI compat)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = s.model,
                    onValueChange = vm::setModel,
                    label = { Text("Model") },
                    modifier = Modifier.width(220.dp)
                )

                Button(onClick = vm::checkNow, enabled = !s.isLoading) { Text("Check (⌘Enter)") }
                OutlinedButton(onClick = vm::cancel, enabled = s.isLoading) { Text("Cancel (Esc)") }
            }

            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxSize()) {
                // 左侧：上下两个编辑框
                Column(Modifier.weight(1f).fillMaxHeight()) {
                    Text("Context (already written)", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = s.contextText,
                        onValueChange = vm::setContext,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        placeholder = { Text("Paste the previous paragraphs here...") }
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Draft (editing)", style = MaterialTheme.typography.titleSmall)
                        ModeChipRow(
                            current = s.mode,
                            onChange = vm::setMode
                        )
                    }

                    OutlinedTextField(
                        value = s.draftText,
                        onValueChange = vm::setDraft,
                        modifier = Modifier.weight(1f).fillMaxWidth()
                            .onPreviewKeyEvent { e ->
                                if (e.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                                // mac: ⌘ 是 Meta
                                when {
                                    e.key == Key.Enter && e.isMetaPressed && !e.isShiftPressed -> {
                                        vm.checkNow(); true
                                    }
                                    e.key == Key.Enter && e.isMetaPressed && e.isShiftPressed -> {
                                        vm.setMode(CheckMode.RewriteFormal)
                                        vm.checkNow()
                                        true
                                    }
                                    e.key == Key.Escape -> {
                                        vm.cancel(); true
                                    }
                                    else -> false
                                }
                            },
                        placeholder = { Text("Type your English here...") }
                    )
                }

                Spacer(Modifier.width(12.dp))

                // 右侧：纠正框
                Column(Modifier.weight(1f).fillMaxHeight()) {
                    Text("Correction", style = MaterialTheme.typography.titleSmall)

                    if (s.isLoading) {
                        LinearProgressIndicator(Modifier.fillMaxWidth().padding(vertical = 8.dp))
                    }

                    if (s.error != null) {
                        Text("Error: ${s.error}", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = s.rewritten.orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        placeholder = { Text("Your improved rewrite will appear here...") }
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = vm::applyRewriteToDraft,
                            enabled = s.rewritten.isBlank()
                        ) { Text("Apply to Draft") }

                        OutlinedButton(
                            onClick = vm::checkNow,
                            enabled = !s.isLoading
                        ) { Text("Re-check") }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text("Alternatives", style = MaterialTheme.typography.titleSmall)
                    LazyColumn(Modifier.fillMaxWidth().heightIn(max = 160.dp)) {
                        items(s.alternatives) { alt ->
                            AssistChip(
                                onClick = { vm.setDraft(alt); },
                                label = { Text(alt) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun ModeChipRow(current: CheckMode, onChange: (CheckMode) -> Unit) {
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
