package com.ethan.android.notepad.ui.technique.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.DarkCyan
import com.ethan.android.notepad.theme.LightBLue
import com.ethan.android.notepad.theme.LightGreen
import com.ethan.android.notepad.theme.Orange
import com.ethan.android.notepad.theme.Pink

@Composable
fun AspectRatioPage() {

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(start = 10.dp, end = 10.dp, bottom = 30.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.div(1f)).background(color = DarkCyan, shape = RoundedCornerShape(8.dp))) {
            Text("宽高比：1*1", color = Black, fontSize = 12.sp, modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(4.div(3f)).background(color = Orange, shape = RoundedCornerShape(8.dp))) {
            Text("宽高比：4*3", color = Black, fontSize = 12.sp, modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(3.div(4f)).background(color = LightBLue, shape = RoundedCornerShape(8.dp))) {
            Text("宽高比：3*4", color = Black, fontSize = 12.sp, modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(9.div(6f)).background(color = Pink, shape = RoundedCornerShape(8.dp))) {
            Text("宽高比：9*6", color = Black, fontSize = 12.sp, modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(6.div(9f)).background(color = LightGreen, shape = RoundedCornerShape(8.dp))) {
            Text("宽高比：6*9", color = Black, fontSize = 12.sp, modifier = Modifier.align(Alignment.Center))
        }
    }
}