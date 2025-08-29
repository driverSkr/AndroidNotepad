package com.ethan.android.notepad.ui.technique.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.ethan.android.notepad.common.utils.setAlpha
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Cyan
import com.ethan.android.notepad.theme.DarkCyan
import com.ethan.android.notepad.theme.LightBLue
import com.ethan.android.notepad.theme.LightGreen
import com.ethan.android.notepad.theme.LightPink
import com.ethan.android.notepad.theme.RedFF5F2D
import com.ethan.android.notepad.theme.Transparent
import com.ethan.android.notepad.theme.White
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@Composable
fun GaussianBlurPage() {
    val hazeState = HazeState()
    var position by remember { mutableStateOf(Offset.Zero) }
    val alpha = -(position.y) / 400F

    Box(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.fillMaxSize().haze(hazeState)) {
            LazyColumn {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp).background(color = Cyan).onGloballyPositioned {
                        position = it.positionInParent()
                    })
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp).background(color = RedFF5F2D))
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp).background(color = LightGreen))
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp).background(color = DarkCyan))
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp).background(color = LightBLue))
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp).background(color = LightPink))
                }
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .hazeChild(hazeState, style = HazeStyle(backgroundColor = Black, tint = null, blurRadius = 12.dp * alpha))
            .background(brush = Brush.verticalGradient(0f to Black.setAlpha(alpha), 0.3f to Black.setAlpha(alpha), 1f to Transparent))
        ) {
            Text("这是高斯模糊", color = White, modifier = Modifier.align(Alignment.Center))
        }
    }
}