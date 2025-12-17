package com.ethan.android.notepad.ui.dialog.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Black0C0C0F
import com.ethan.android.notepad.theme.Black0C0C0F60
import com.ethan.android.notepad.theme.White

@SuppressLint("ComposableNaming")
@Composable
fun rememberConfirmDialog(
    title: String? = null,                      //标题
    content: String,                            //内容
    contentAlign: TextAlign = TextAlign.Start,  //内容布局
    mainTv: String,                             //主按钮内容
    secondaryTv: String? = null,                //次级按钮内容
    mainBtn: () -> Unit,                        //主按钮触发方法
    secondaryBtn: () -> Unit? = {}              //次级按钮触发方法
): MutableState<Boolean> {
    val confirmDialogState = remember { mutableStateOf(false) }

    if (confirmDialogState.value){
        Dialog(
            onDismissRequest = { confirmDialogState.value = false },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Card(modifier = Modifier
                .width(320.dp)
                .height(IntrinsicSize.Min),
                shape = RoundedCornerShape(12.dp),
                colors = cardColors(containerColor = White, contentColor = Black, disabledContainerColor = White, disabledContentColor = Black)
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                ) {
                    Image(painter = painterResource(id = R.drawable.svg_icon_close), contentDescription = null, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 12.dp, end = 12.dp)
                        .clickable {
                            secondaryBtn.invoke()
                            confirmDialogState.value = false
                        }
                    )
                }

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                ) {

                    if (title != null) {
                        Text(text = title, fontSize = 16.sp, color = Black0C0C0F, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp))
                    }

                    Text(text = content, fontSize = 16.sp, color = Black0C0C0F60, textAlign = contentAlign, fontWeight = FontWeight.W400, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(24.dp))

                    if (secondaryTv != null) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(
                                width = 1.dp,
                                color = Black0C0C0F,
                                shape = RoundedCornerShape(48.dp)
                            )
                            .clickable {
                                secondaryBtn.invoke()
                                confirmDialogState.value = false
                            }
                        ) {
                            Text(text = secondaryTv, fontSize = 14.sp, color = Black0C0C0F, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(color = Black0C0C0F, shape = RoundedCornerShape(48.dp))
                        .clickable {
                            mainBtn.invoke()
                            confirmDialogState.value = false
                        }
                    ) {
                        Text(text = mainTv, fontSize = 14.sp, color = White, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }

    return confirmDialogState
}