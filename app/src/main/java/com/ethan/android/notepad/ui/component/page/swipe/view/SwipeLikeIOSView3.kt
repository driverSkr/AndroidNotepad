package com.ethan.android.notepad.ui.component.page.swipe.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import com.ethan.android.notepad.ui.component.page.swipe.lib.SwipeAction
import com.ethan.android.notepad.ui.component.page.swipe.lib.SwipeableActionsBox
import com.ethan.android.notepad.ui.component.page.swipe.lib.rememberSwipeableActionsState
import kotlinx.coroutines.launch

@Composable
fun SwipeLikeIOSView3() {
    val scope = rememberCoroutineScope()
    val state = rememberSwipeableActionsState().apply { this.offset }
    val archive = SwipeAction(
        icon = rememberVectorPainter(Icons.TwoTone.Delete),
        background = Color.Green,
        onClick = { }
    )

    val isEnable = remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            Button(modifier = Modifier.width(50.dp).height(30.dp), onClick = {
                scope.launch {
                    state.handleReset()
                }
            }) {
                Text(text = "复位", color = Color.Black)
            }

            Spacer(modifier = Modifier.width(20.dp))
            Button(modifier = Modifier.width(50.dp).height(30.dp), onClick = {
                isEnable.value = !isEnable.value
            }) {
                Text(text = "禁用", color = Color.Black)
            }
        }
        SwipeableActionsBox(
            state = state,
            endActions = listOf(archive),
            swipeThreshold = 80.dp,
            enabled = isEnable.value,
            modifier = Modifier
        ) {
            VoiceItemView()
        }
    }

}