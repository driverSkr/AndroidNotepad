package com.ethan.android.notepad.ui.dialog.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ethan.android.notepad.theme.Gray
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.utils.setAlpha

@Composable
fun rememberLoadingWithTitleDialog(title: String): MutableState<Boolean> {
    val loadingDialogState = remember { mutableStateOf(false) }
    if (loadingDialogState.value) {
        Dialog(onDismissRequest = { loadingDialogState.value = false }, properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {

            Box {
                Column(modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .background(
                        color = Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(36.dp), color = Color.White, trackColor = Color.White.setAlpha(0.1F), strokeCap = StrokeCap.Round)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(title, color = White)
                }

            }
        }
    }
    return loadingDialogState
}