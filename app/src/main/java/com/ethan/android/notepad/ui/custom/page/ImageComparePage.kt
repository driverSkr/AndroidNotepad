package com.ethan.android.notepad.ui.custom.page

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.ui.custom.view.ImageContrastView
import com.ethan.android.notepad.ui.custom.view.ImageWithTextContrastView
import com.ethan.android.notepad.ui.custom.view.StatusBarsView

/**
 * 图片对比动画
 */
@Composable
@Preview
fun ImageComparePage() {
    val context = LocalContext.current
    val before = remember { BitmapFactory.decodeResource(context.resources, R.mipmap.img_breast_enlargement_auto_example_before).asImageBitmap() }
    val after = remember { BitmapFactory.decodeResource(context.resources, R.mipmap.img_breast_enlargement_auto_example_after).asImageBitmap() }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
        StatusBarsView("图片对比动画")

        Spacer(modifier = Modifier.height(20.dp))

        Text("普通对比动画", color = Black)
        Box(modifier = Modifier.size(180.dp).clip(RoundedCornerShape(6.dp))) {
            ImageContrastView(before = before, after = after, startFrom = 0.025F, endTo = 0.975F, isPlayAnim = true)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("带文字对比动画", color = Black)
        Box(modifier = Modifier.width(200.dp).height(300.dp).clip(RoundedCornerShape(6.dp))) {
            ImageWithTextContrastView()
        }
    }
}