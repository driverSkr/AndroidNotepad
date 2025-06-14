package com.ethan.android.notepad.ui.custom.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Gray
import com.ethan.android.notepad.theme.White

/**
 * 带标题的卡片视图
 */
@Composable
@Preview
fun TitleCardView(
    title: String = "示例",
    isShowTitle: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 12.dp)
    ) {
        if (isShowTitle) {
            Text(
                text = title,
                color = Gray,
                fontWeight = FontWeight.W400,
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal,
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 4.dp)
            )
        }
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = White, contentColor = Black, disabledContainerColor = White, disabledContentColor = Black),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Box(content = content, modifier = modifier, contentAlignment = Alignment.Center)
        }
    }
}