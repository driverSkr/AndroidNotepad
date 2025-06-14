package com.ethan.android.notepad.ui.dialog.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Color0XFF37255B
import com.ethan.android.notepad.theme.Color412D5C_20
import com.ethan.android.notepad.theme.ColorFF7762_20
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White20
import com.ethan.android.notepad.theme.White70
import com.ethan.android.notepad.utils.antiShakeClick

@Composable
@Preview
fun cloneProgressDialog(): MutableState<Boolean> {
    val loadingDialogState = remember { mutableStateOf(false) }
    val diagonalGradientBrush = Brush.linearGradient(
        colors = listOf(ColorFF7762_20, Color412D5C_20),
        start = Offset.Zero, // 左上角
        end = Offset.Infinite // 右下角
    )

    if (loadingDialogState.value) {
        Dialog(onDismissRequest = { loadingDialogState.value = false }, properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {
            Card(modifier = Modifier
                .width(320.dp)
                .wrapContentHeight(),
                colors = CardColors(containerColor = Color0XFF37255B, contentColor = Black, disabledContainerColor = Color0XFF37255B, disabledContentColor = Black)
            ) {
                Box(modifier = Modifier.background(brush = diagonalGradientBrush, shape = RoundedCornerShape(12.dp))) {
                    Image(painter = painterResource(R.drawable.svg_icon_close_white), contentDescription = null, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 12.dp, end = 12.dp)
                        .antiShakeClick { loadingDialogState.value = false }
                    )

                    Column(modifier = Modifier
                        .wrapContentHeight()
                        .width(320.dp)
                        .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))
                        CircularProgressIndicator(modifier = Modifier.size(36.dp), strokeWidth = 4.dp, color = White, trackColor = White20, strokeCap = StrokeCap.Round)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("进度20%",color = White, fontSize = 16.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W600))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("处理中。。。",color = White70, fontSize = 16.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
    return loadingDialogState
}