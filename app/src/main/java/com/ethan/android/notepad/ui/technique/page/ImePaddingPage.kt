package com.ethan.android.notepad.ui.technique.page

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.ethan.android.notepad.common.utils.ToastType
import com.ethan.android.notepad.common.utils.antiShakeClick
import com.ethan.android.notepad.common.utils.showToast
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Black10
import com.ethan.android.notepad.theme.Black40
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.Purple8A49FF
import com.ethan.android.notepad.theme.Transparent
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.colorList
import com.ethan.android.notepad.common.view.StatusBarsWithExplainView

@Composable
@Preview
fun ImePaddingPage() {
    val context = LocalContext.current
    var content by remember { mutableStateOf("") }
    val focusManger = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .imePadding()
    ) {
        StatusBarsWithExplainView(title = "imePadding", content = "自动增加底部内边距以适应软键盘")
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 12.dp).padding(top = 8.dp)) {
            item {
                CustomTextField(content, { content = it }, "这是个输入框")
            }

            itemsIndexed(colorList) { _, hex ->
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 15.dp)
                    .background(color = Color(hex.toColorInt()), shape = RoundedCornerShape(12.dp))
                )
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(10.dp)
            .padding(horizontal = 12.dp)
            .background(color = Purple8A49FF, shape = RoundedCornerShape(16.dp))
            .antiShakeClick {
                focusManger.clearFocus() //让输入框失去焦点
                keyboardController?.hide()  //收起软键盘
                "focusManger.clearFocus()让输入框失去焦点\nkeyboardController?.hide()收起软键盘".showToast(context, ToastType.HINT, Toast.LENGTH_LONG)
            }
        ) {
            Text(text = "这是个按钮", color = White, fontSize = 16.sp, modifier = Modifier.align(Alignment.Center))
        }
    }
}

/**
 * 文本域
 */
@Composable
fun CustomTextField(
    content: String,
    onValueChange: (String) -> Unit,
    hint: String,
    maxLength: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
) {

    val trimmedContent = content.take(maxLength)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderModifier = if (isFocused) Modifier.border(
        width = 1.dp,
        color = Black40,
        shape = RoundedCornerShape(6.dp)
    ) else Modifier

    TextField(
        value = trimmedContent,
        onValueChange = {
            if (it.length <= maxLength) {
                onValueChange(it)
            }
        },
        placeholder = {
            Text(
                hint,
                fontSize = 14.sp,
                color = Black40,
                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400)
            )
        },
        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W400),
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(6.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Black10, // 聚焦时背景颜色
            unfocusedContainerColor = Black10, // 未聚焦时背景颜色
            disabledContainerColor = Black10, // 禁用时背景颜色
            cursorColor = Black, // 光标颜色
            focusedTextColor = Black, // 聚焦时文本颜色
            unfocusedTextColor = Black, // 未聚焦时文本颜色
            focusedIndicatorColor = Transparent, // 聚焦时指示器颜色
            unfocusedIndicatorColor = Transparent, // 未聚焦时指示器颜色
            disabledIndicatorColor = Transparent, // 禁用时指示器颜色
        ),
        interactionSource = interactionSource,
        enabled = true,
        modifier = modifier
            .fillMaxWidth()
            .then(borderModifier)
            .heightIn(min = 180.dp, max = 300.dp)
    )
}