package com.ethan.android.notepad.ui.test.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.theme.Black

@Composable
fun TestPage1() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Activity打开关闭动画", color = Black, fontSize = 16.sp, modifier = Modifier.align(Alignment.Center))
    }
}