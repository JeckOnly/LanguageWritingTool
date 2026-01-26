package com.example.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demo.presentation.viewmodel.MainViewModel
import com.example.demo.state.UiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TopConfigArea(
    s: UiState,
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {

        Row(
            modifier = modifier.height(60.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = s.baseUrl,
                onValueChange = vm::setBaseUrl,
                label = { Text("Base URL (OpenAI compat)") },
                modifier = Modifier.width(440.dp) // ✅ 固定宽
            )

            OutlinedTextField(
                value = s.model,
                onValueChange = vm::setModel,
                label = { Text("Model") },
                modifier = Modifier.width(220.dp) // ✅ 固定宽
            )

            OutlinedButton(
                onClick = vm::toggleTheme,
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(if (s.isDarkTheme) "Light" else "Dark")
            }

            Button(
                onClick = vm::checkNow,
                enabled = !s.isLoading,
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Check (⌘Enter)")
            }

            OutlinedButton(
                onClick = vm::cancel,
                enabled = s.isLoading,
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Cancel (Esc)")
            }
    }
}

