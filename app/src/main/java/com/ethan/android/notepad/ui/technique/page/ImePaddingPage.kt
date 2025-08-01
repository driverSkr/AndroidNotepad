package com.ethan.android.notepad.ui.technique.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Blue
import com.ethan.android.notepad.theme.Red
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.ui.component.page.MyTextField
import com.ethan.android.notepad.utils.antiShakeClick

@Composable
@Preview
fun ImePaddingPage() {
    var content by remember { mutableStateOf("") }
    val focusManger = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Black)
        .statusBarsPadding()
        .navigationBarsPadding()
        .imePadding()
        .padding(horizontal = 12.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp)) {
            item {
                MyTextField(content, { content = it }, "这是个输入框")
            }

            items(5) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 15.dp)
                    .background(color = Red, shape = RoundedCornerShape(12.dp))
                )
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(10.dp)
            .background(color = Blue, shape = RoundedCornerShape(16.dp))
            .antiShakeClick {
                focusManger.clearFocus() //让输入框失去焦点
                keyboardController?.hide()  //收起软键盘
            }
        ) {
            Text(text = "这是个按钮", color = White, fontSize = 16.sp, modifier = Modifier.align(Alignment.Center))
        }
    }
}