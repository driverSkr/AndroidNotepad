package com.ethan.android.notepad.ui.dialog.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ethan.android.notepad.utils.setAlpha

@Composable
@Preview
fun rememberLoadingDialog(): MutableState<Boolean> {
    val loadingDialogState = remember { mutableStateOf(false) }
    if (loadingDialogState.value) {
        Dialog(onDismissRequest = { loadingDialogState.value = false }, properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {
            Box {
                CircularProgressIndicator(modifier = Modifier
                    .align(Alignment.Center)
                    .size(36.dp), color = Color.White, trackColor = Color.White.setAlpha(0.1F), strokeCap = StrokeCap.Round)

            }
        }
    }
    return loadingDialogState
}