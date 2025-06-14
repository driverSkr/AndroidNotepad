package com.ethan.android.notepad.ui.custom.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Black10
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.Red
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.ui.custom.model.CardItem
import com.ethan.android.notepad.utils.antiShakeClick
import com.ethan.android.notepad.utils.invisible

/**
 * 包含列表项的卡片视图
 */
@Composable
fun ListCardView(items: List<CardItem>, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White, contentColor = Black, disabledContainerColor = White, disabledContentColor = Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.0.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 12.dp),
    ) {
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .height((50 * items.size).dp)) {
            items(items) {
                ItemView(it, isNotEnd = it != items.last())
            }
        }
    }
}

@Composable
@Preview
fun ItemView(item: CardItem = CardItem("示例", true), isNotEnd: Boolean = true, modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(horizontal = 12.dp)
        .antiShakeClick {
            item.onClick()
        }
    ) {
        Row(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                item.name,
                color = Black,
                fontSize = 16.sp,
                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(modifier = Modifier
                .size(8.dp)
                .invisible(!item.isCompleted)
                .background(color = Red, shape = RoundedCornerShape(999.dp)))

        }

        if (item.isNeedEndIcon) {
            Image(
                painter = painterResource(id = R.drawable.svg_icon_next_black40),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        if (isNotEnd) {
            HorizontalDivider(
                thickness = 1.dp,
                color = Black10,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(999.dp))
            )
        }
    }
}