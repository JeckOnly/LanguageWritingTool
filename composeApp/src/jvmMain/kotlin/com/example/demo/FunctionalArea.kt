package com.example.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import com.example.demo.data.repo.CheckMode
import com.example.demo.presentation.viewmodel.MainViewModel
import com.example.demo.state.UiState

@Composable
 fun FunctionalArea(
    s: UiState,
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        // 左侧
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
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .onPreviewKeyEvent { e ->
                        if (e.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

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

        // 右侧
        Column(Modifier.weight(1f).fillMaxHeight()) {
            // 右侧：纠正框
            Column(Modifier.weight(1f).fillMaxHeight()) {
                Text("Correction", style = MaterialTheme.typography.titleSmall)

                if (s.isLoading) {
                    LinearProgressIndicator(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                }

                if (s.error != null) {
                    Text(
                        text = "Error: ${s.error}",
                        color = MaterialTheme.colorScheme.error
                    )
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
                        enabled = s.rewritten?.isNotBlank() == true && !s.isLoading
                    ) { Text("Apply to Draft") }

                    OutlinedButton(
                        onClick = vm::addRewriteToContext,
                        enabled = s.rewritten?.isNotBlank() == true && !s.isLoading
                    ) { Text("Add Context") }

                    OutlinedButton(
                        onClick = vm::checkNow,
                        enabled = !s.isLoading
                    ) { Text("Re-check") }
                }


                Spacer(Modifier.height(10.dp))

                Text("Alternatives", style = MaterialTheme.typography.titleSmall)
                LazyColumn(
                    Modifier.fillMaxWidth().heightIn(max = 160.dp)
                ) {
                    items(s.alternatives) { alt ->
                        AssistChip(
                            onClick = { vm.setDraft(alt) },
                            label = { Text(alt) },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}