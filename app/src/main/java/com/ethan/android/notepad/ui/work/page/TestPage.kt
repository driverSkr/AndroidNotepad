package com.ethan.android.notepad.ui.work.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.ethan.android.notepad.ui.custom.view.StatusBarsView
import com.ethan.android.notepad.ui.technique.TechniquePreviewActivity
import com.ethan.android.notepad.ui.technique.model.PageType

@Composable
@Preview
fun TestPage() {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView("测试代码", true)
        Button(modifier = Modifier.width(150.dp), onClick = {
            TechniquePreviewActivity.launch(context, PageType.LoadAnimation)
        }) {
            Text("加载动画")
        }
    }
}