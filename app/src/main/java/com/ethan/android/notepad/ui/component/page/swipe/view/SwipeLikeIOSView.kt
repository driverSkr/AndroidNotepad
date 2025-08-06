package com.ethan.android.notepad.ui.component.page.swipe.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.swipeable
import com.linversion.swipe.SwipeAction
import com.linversion.swipe.SwipeableActionsBox
import com.linversion.swipe.rememberSwipeableActionsState

@Composable
fun SwipeLikeIOSView() {
    val state = rememberSwipeableActionsState().apply { this.offset }
    val archive = SwipeAction(
        icon = rememberVectorPainter(Icons.TwoTone.Delete),
        background = Color.Green,
        onClick = { }
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