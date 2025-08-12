package com.ethan.android.notepad.ui.component.page.swipe.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import me.saket.swipe.rememberSwipeableActionsState


@Composable
fun SwipeLikeIOSView2() {

    val state = rememberSwipeableActionsState()
    val archive = SwipeAction(
        icon = rememberVectorPainter(Icons.TwoTone.Delete),
        background = Color.Red,
        weight = 0.5,
        onSwipe = {},
    )

    SwipeableActionsBox(
        state = state,
        endActions = listOf(archive),
        swipeThreshold = 80.dp,
        modifier = Modifier
    ) {
        VoiceItemView()
    }
}