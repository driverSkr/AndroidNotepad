package com.ethan.android.notepad.ui.dialog.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.ethan.android.notepad.theme.Purple712FE7
import com.ethan.android.notepad.theme.Purple9B64FF
import com.ethan.android.notepad.theme.PurpleCEA9FF_10
import com.ethan.android.notepad.theme.White

@Composable
@Preview
fun rememberConfirmDialog(
    title: String = "",                              // 标题
    content: String = "",                            // 内容
    mainTv: String = "",                             // 主按钮内容
    secondaryTv: String = "",                        // 次级按钮内容
    mainBtn: () -> Unit = {},                        // 主按钮触发方法
    secondaryBtn: () -> Unit = {},                   // 次级按钮触发方法
    contentAlign: TextAlign = TextAlign.Center,      // 内容布局
): MutableState<Boolean> {
    val confirmDialogState = remember { mutableStateOf(false) }

    val diagonalGradientBrush = Brush.linearGradient(
        colors = listOf(ColorFF7762_20, Color412D5C_20),
        start = Offset.Zero, // 左上角
        end = Offset.Infinite // 右下角
    )

    if (confirmDialogState.value) {
        Dialog(
            onDismissRequest = { confirmDialogState.value = false },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Card(modifier = Modifier
                .width(360.dp)
                .wrapContentHeight(),
                colors = CardColors(containerColor = Color0XFF37255B, contentColor = Black, disabledContainerColor = Color0XFF37255B, disabledContentColor = Black)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(brush = diagonalGradientBrush, shape = RoundedCornerShape(12.dp))
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                    ) {
                        Image(painter = painterResource(R.drawable.svg_icon_close_white), contentDescription = null, modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 12.dp)
                            .clickable { confirmDialogState.value = false })
                    }

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                    ) {

                        if (title.isNotEmpty()) {
                            Text(title, fontSize = 16.sp, color = White, textAlign = TextAlign.Center, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W600), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                        }

                        Text(content, fontSize = 14.sp, color = White, textAlign = contentAlign, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400), modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()) {
                            if (secondaryTv.isNotEmpty()) {
                                Box(modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .background(color = PurpleCEA9FF_10, shape = RoundedCornerShape(16.dp))
                                    .clickable {
                                        secondaryBtn.invoke()
                                        confirmDialogState.value = false
                                    }
                                ) {
                                    Text(secondaryTv, fontSize = 12.sp, color = White, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W600), modifier = Modifier.align(Alignment.Center))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            Box(modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(brush = Brush.horizontalGradient(colorStops = arrayOf(0.5f to Purple9B64FF, 1f to Purple712FE7)), shape = RoundedCornerShape(16.dp))
                                .clickable {
                                    mainBtn.invoke()
                                    confirmDialogState.value = false
                                }
                            ) {
                                Text(mainTv, fontSize = 12.sp, color = White, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W600), modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                }
            }
        }
    }

    return confirmDialogState
}