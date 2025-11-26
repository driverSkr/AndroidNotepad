package com.ethan.android.notepad.ui.media.image.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ImageZoomExample() {
    var imagePath by remember { mutableStateOf<String?>(null) }
    var currentScale by remember { mutableStateOf(1f) }
    val transformState = rememberTransformState()

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 控制栏
        ControlBar(
            currentScale = currentScale,
            transformState = transformState,
            onImageSelected = { path ->
                imagePath = path
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )

        // 图片显示区域
        ZoomableImage(
            imagePath = imagePath,
            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            minScale = 0.3f,
            maxScale = 10f,
            onScaleChange = { scale ->
                currentScale = scale
            }
        )

        // 状态信息栏
        StatusInfo(
            currentScale = currentScale,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )
    }
}

@Composable
private fun ControlBar(
    currentScale: Float,
    transformState: TransformState,
    onImageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 缩放信息
        Text(
            text = "缩放: ${"%.2f".format(currentScale)}x",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        // 控制按钮
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    // 重置变换
                    transformState.reset()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("重置")
            }

            Button(
                onClick = {
                    // 选择示例图片（这里使用本地资源）
                    onImageSelected("/storage/emulated/0/Android/data/com.ethan.android.notepad/cache/export/1000092143.jpg")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("加载图片")
            }
        }
    }
}

@Composable
private fun StatusInfo(
    currentScale: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when {
                currentScale < 1f -> "缩小视图"
                currentScale > 1f -> "放大视图"
                else -> "原始尺寸"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// 预览
@Preview(showBackground = true)
@Composable
fun ImageZoomExamplePreview() {
    MaterialTheme {
        ImageZoomExample()
    }
}