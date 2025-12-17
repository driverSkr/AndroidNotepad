package com.ethan.android.notepad.ui.dialog.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black80
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.Purple712FE7
import com.ethan.android.notepad.theme.Purple790AC9
import com.ethan.android.notepad.theme.PurpleBC5DFF
import com.ethan.android.notepad.theme.PurpleBE6DFF
import com.ethan.android.notepad.theme.White

@Composable
fun rememberGiftBagDialog(giftCoinsCount: Int): MutableState<Boolean> {
    val giftBagDialogState = remember { mutableStateOf(false) }
    val brushColor = Brush.horizontalGradient(colorStops = arrayOf(0.5f to PurpleBC5DFF, 1f to Purple790AC9))

    if (giftBagDialogState.value) {
        Dialog(
            onDismissRequest = { giftBagDialogState.value = false },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Column (
                modifier = Modifier.fillMaxSize().offset(y = (-50).dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.wrapContentSize()) {
                    Image(painter = painterResource(R.mipmap.gift_bag_dialog_bg), contentDescription = null)
                    Image(painter = painterResource(R.drawable.svg_icon_close_gift), contentDescription = null, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-14).dp, y = 42.dp)
                        .clickable {
                            giftBagDialogState.value = false
                        }
                    )

                    Text("新人礼包",
                        fontSize = 20.sp,
                        color = Black80,
                        style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W800),
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.fillMaxWidth().padding(start = 28.dp, end = 28.dp, top = 133.dp).align(Alignment.TopCenter)
                    )

                    Row(modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.Center)
                        .offset(y = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painter = painterResource(R.mipmap.gift_bag_gold_coin), contentDescription = null)
                        Text("+",
                            fontSize = 32.sp,
                            color = Black80,
                            style = TextStyle(
                                brush = brushColor,
                                fontWeight = FontWeight.W900
                            ),
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.offset(x = (-20).dp)
                        )
                        Text("$giftCoinsCount",
                            fontSize = 48.sp,
                            color = Black80,
                            style = TextStyle(
                                brush = brushColor,
                                fontWeight = FontWeight.W900
                            ),
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.offset(x = (-20).dp, y = (-5).dp)
                        )
                    }

                    Text("恭喜你，升级获得30积分！",
                        fontSize = 14.sp,
                        color = Black80,
                        style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(start = 28.dp, end = 28.dp, bottom = 45.dp).align(Alignment.BottomCenter)
                    )

                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier
                    .wrapContentSize()
                    .background(
                        brush = Brush.verticalGradient(colors = listOf(PurpleBE6DFF, Purple712FE7)),
                        shape = RoundedCornerShape(59.dp)
                    )
                    .clickable {
                        giftBagDialogState.value = false
                    }
                    .padding(vertical = 14.dp, horizontal = 95.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("领取", color = White, fontSize = 16.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W600), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }

    return giftBagDialogState
}