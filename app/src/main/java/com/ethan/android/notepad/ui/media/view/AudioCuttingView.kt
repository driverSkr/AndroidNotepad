package com.ethan.android.notepad.ui.media.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
/** 音频裁剪 */
fun AudioCuttingView(originPath: String = "") {

    Box(modifier = Modifier.fillMaxSize()) {
        Text("音频裁剪页", modifier = Modifier.align(Alignment.Center))
    }

}