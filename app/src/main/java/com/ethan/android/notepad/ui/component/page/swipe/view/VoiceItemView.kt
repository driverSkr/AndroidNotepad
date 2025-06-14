package com.ethan.android.notepad.ui.component.page.swipe.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black40
import com.ethan.android.notepad.theme.Black60
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.Purple9258FA
import com.ethan.android.notepad.theme.Transparent
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White50
import com.ethan.android.notepad.theme.White8
import com.ethan.android.notepad.utils.antiShakeClick

/** 音频项 */
@Composable
@Preview
fun VoiceItemView(modifier: Modifier = Modifier, isSelected: Boolean = false, onclick: () -> Unit = {}) {

    val isPlay = remember { mutableStateOf(false) }
    val recordPlayBtn = if (isPlay.value) R.drawable.svg_icon_voice_play else R.drawable.svg_icon_voice_pause

    Row(modifier = modifier
        .fillMaxWidth()
        .height(80.dp)
        .border(
            width = 2.dp,
            color = if (isSelected) Purple9258FA else Transparent,
            shape = RoundedCornerShape(12.dp)
        )
        .background(color = if (isSelected) Black60 else Black40, shape = RoundedCornerShape(12.dp))
        .padding(vertical = 8.dp, horizontal = 7.dp)
        .antiShakeClick { onclick.invoke() }
    ) {
        Box(modifier = Modifier.antiShakeClick {
            isPlay.value = !isPlay.value
        }) {
            Image(painter = painterResource(R.mipmap.voice_item_img), contentScale = ContentScale.Crop, contentDescription = null, modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
            )

            Image(painter = painterResource(recordPlayBtn), contentDescription = null, modifier = Modifier.align(Alignment.Center))
        }

        Column(modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 8.dp)) {
            Text(
                text = "Ethan",
                color = White,
                fontSize = 14.sp,
                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500)
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.wrapContentSize()) {
                Text(
                    text = "Young",
                    color = White50,
                    fontSize = 10.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(color = White8, shape = RoundedCornerShape(24.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Text(
                    text = "male",
                    color = White50,
                    fontSize = 10.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(color = White8, shape = RoundedCornerShape(24.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}