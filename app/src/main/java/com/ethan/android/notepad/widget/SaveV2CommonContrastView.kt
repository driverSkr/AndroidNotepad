package com.ethan.android.notepad.widget

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.ethan.android.notepad.R
import com.ethan.android.notepad.utils.setAlpha

@Composable
fun SaveV2CommonContrastView() {
    val context = LocalContext.current
    val before = remember { BitmapFactory.decodeResource(context.resources, R.mipmap.img_breast_enlargement_auto_example_before) }
    val after = remember { BitmapFactory.decodeResource(context.resources, R.mipmap.img_breast_enlargement_auto_example_after) }

    Box(Modifier.padding(horizontal = 12.dp)) {
        AndroidView(factory = {
            CompareV2View(it).apply {
                setImage(before, after)
            }
        }, modifier = Modifier.clipToBounds())
        Text(stringResource(R.string.before), color = Color.White, fontSize = 12.sp,
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .padding(12.dp)
                .background(Color.Black.setAlpha(0.6F), RoundedCornerShape(6.dp))
                .padding(vertical = 4.dp, horizontal = 12.dp))
        Text(stringResource(R.string.after), color = Color.White, fontSize = 12.sp,
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(12.dp)
                .background(Color.Black.setAlpha(0.6F), RoundedCornerShape(6.dp))
                .padding(vertical = 4.dp, horizontal = 12.dp))

    }

}

