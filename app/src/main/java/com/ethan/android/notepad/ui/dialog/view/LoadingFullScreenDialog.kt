package com.ethan.android.notepad.ui.dialog.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.White

@Composable
fun FullScreenLoadingDialog(
    activity: FragmentActivity,
    show: Boolean,
    onDismiss: () -> Unit,
    state: MutableLiveData<Int>
) {
    val text = remember { mutableStateOf("oss上传中") }
    LaunchedEffect(Unit) {
        state.observe(activity) {
            when(it) {
                0 -> text.value = "排队中"
                1 -> text.value = "处理中"
                2 -> text.value = "处理完成"
                3 -> text.value = "回传成功"
            }
        }
    }

    if (show) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false, // 关键：禁用默认宽度限制
                decorFitsSystemWindows = false // 关键：让对话框适应系统窗口，包括状态栏
            )
        ) {
            // 使用可拖动的Box填满整个对话框
            Box(
                modifier = Modifier
                    .fillMaxSize()  // 填满对话框
                    .background(Black.copy(alpha = 0.6f))
                    .windowInsetsPadding(WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)) // 移除所有内边距
            ) {
                Text(
                    text.value,
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}