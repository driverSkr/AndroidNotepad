package com.ethan.android.notepad.ui.component.page

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Black10
import com.ethan.android.notepad.theme.Purple8A49FF
import com.ethan.android.notepad.theme.Purple8A49FF_20
import com.ethan.android.notepad.ui.component.view.ProgressBarWithBubble
import kotlinx.coroutines.delay

@Composable
@Preview
fun ProgressIndicatorPage() {

    var progress by remember { mutableFloatStateOf(0.01f) }
    var rawProgress by remember { mutableFloatStateOf(0.01f) }
    //添加动画进度状态
    val animatedProgress by animateFloatAsState(targetValue = rawProgress, animationSpec = tween(durationMillis = 500), label = "progress") //500ms的过渡动画

    LaunchedEffect(Unit) {
        while (rawProgress <= 1) {
            delay(500)
            progress += 0.012f
            rawProgress += 0.01f
        }
    }

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f).padding(all = 10.dp)) {
        Text(text = "圆形进度条")
        CircularProgressIndicator(
            color = Black,
            trackColor = Black10,
            strokeWidth = 4.dp,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "长条形进度条")
        LinearProgressIndicator(
            progress = progress,
            color = Purple8A49FF,
            backgroundColor = Purple8A49FF_20,
            strokeCap = StrokeCap.Round,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(shape = RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Marvels的加载进度组件")
        ProgressBarWithBubble(animatedProgress, "", false, true)
    }
}