package com.ethan.android.notepad.ui.room.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.ethan.android.notepad.room.AppDataBase
import com.ethan.android.notepad.room.entity.User
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.PurpleBE6DFF
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.ui.component.ComponentActivity
import com.ethan.android.notepad.ui.custom.model.CardItem
import com.ethan.android.notepad.ui.custom.view.ListCardView
import com.ethan.android.notepad.ui.custom.view.StatusBarsView
import com.ethan.android.notepad.utils.antiShakeClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun RoomPage() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val items = listOf(
        CardItem("数据库的使用（room）", true) { ComponentActivity.launch(context) },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "Room数据库的使用")
        ListCardView(items = items)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier
                .wrapContentSize()
                .background(color = PurpleBE6DFF, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .antiShakeClick {
                    scope.launch(Dispatchers.Default) {
                        val userDao = AppDataBase
                            .invoke(context)
                            .userDao()
                        userDao.insert(
                            User(
                                name = "Ethan",
                                phone = 17607016172,
                                password = "123456",
                                email = "2237913536@qq.com",
                                address = "西乡径贝新村",
                                gender = "male",
                                age = 25
                            )
                        )
                    }
                }
            ) {
                Text(
                    "添加用户",
                    color = White,
                    fontSize = 16.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }
    }
}