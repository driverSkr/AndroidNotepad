package com.ethan.android.notepad.ui.component.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Black0C0C0F
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.Transparent
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White10
import com.ethan.android.notepad.theme.White40

@Composable
@Preview
fun TextFieldPage() {

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var feedbackType by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val options = listOf(
        context.getString(R.string.feature_suggestion),
        context.getString(R.string.technical_issues),
        context.getString(R.string.complaint),
        context.getString(R.string.purchase_problem),
        context.getString(R.string.refund_consultation)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .background(color = Black)
            .padding(horizontal = 16.dp)
    ) {

        Spacer(Modifier.height(12.dp))
        Text(text = "输入框", color = White, fontSize = 14.sp)
        TextInput(email, { email = it }, "请输入邮箱地址")

        Spacer(Modifier.height(12.dp))
        Text(text = "下拉选择框", color = White, fontSize = 14.sp)
        DropdownMenu(feedbackType, options, onOptionSelected = { feedbackType = it })

        Spacer(Modifier.height(12.dp))
        Text(text = "文本域", color = White, fontSize = 14.sp)
        MyTextField(content, { content = it }, "告诉我们您对程序的反馈")
    }
}

/**
 * 输入框
 */
@Composable
fun TextInput(
    text: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
) {

    TextField(
        value = text,
        onValueChange = {
            onValueChange(it)
        },
        placeholder = {
            Text(
                hint,
                fontSize = 14.sp,
                color = White40,
                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White10, // 聚焦时背景颜色
            unfocusedContainerColor = White10, // 未聚焦时背景颜色
            disabledContainerColor = White10, // 禁用时背景颜色
            cursorColor = White, // 光标颜色
            focusedTextColor = White, // 聚焦时文本颜色
            unfocusedTextColor = White, // 未聚焦时文本颜色
            focusedIndicatorColor = Transparent, // 聚焦时指示器颜色
            unfocusedIndicatorColor = Transparent, // 未聚焦时指示器颜色
            disabledIndicatorColor = Transparent, // 禁用时指示器颜色
        ),
        shape = MaterialTheme.shapes.small.copy(
            topStart = CornerSize(6.dp),
            topEnd = CornerSize(6.dp),
            bottomStart = CornerSize(6.dp),
            bottomEnd = CornerSize(6.dp)
        ),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * 下拉选择框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(feedbackType: String, options: List<String>, onOptionSelected: (String) -> Unit) {

    var expanded by remember { mutableStateOf(false) }
    val iconType = if (expanded) R.drawable.svg_icon_up else R.drawable.svg_icon_down

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            readOnly = true,
            value = feedbackType,
            onValueChange = {},
            placeholder = {
                Text(
                    options[0],
                    fontSize = 14.sp,
                    color = White40,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400)
                )
            },
            trailingIcon = {
                Image(
                    painter = painterResource(id = iconType),
                    contentDescription = ""
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White10, // 聚焦时背景颜色
                unfocusedContainerColor = White10, // 未聚焦时背景颜色
                disabledContainerColor = White10, // 禁用时背景颜色
                focusedTextColor = White, // 聚焦时文本颜色
                unfocusedTextColor = White, // 未聚焦时文本颜色
                focusedIndicatorColor = Transparent, // 聚焦时指示器颜色
                unfocusedIndicatorColor = Transparent, // 未聚焦时指示器颜色
                disabledIndicatorColor = Transparent, // 禁用时指示器颜色
            ),
            shape = MaterialTheme.shapes.small.copy(all = CornerSize(6.dp)),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = White
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(
                            selectionOption,
                            fontSize = 14.sp,
                            color = Black0C0C0F,
                            style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400)
                        )
                    },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    trailingIcon = {
                        if (feedbackType == selectionOption) Image(
                            painter = painterResource(
                                id = R.drawable.svg_icon_selected
                            ), contentDescription = ""
                        )
                    }
                )
            }
        }
    }
}

/**
 * 文本域
 */
@Composable
fun MyTextField(
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
        color = White40,
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
                color = White40,
                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400)
            )
        },
        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W400),
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(6.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White10, // 聚焦时背景颜色
            unfocusedContainerColor = White10, // 未聚焦时背景颜色
            disabledContainerColor = White10, // 禁用时背景颜色
            cursorColor = White, // 光标颜色
            focusedTextColor = White, // 聚焦时文本颜色
            unfocusedTextColor = White, // 未聚焦时文本颜色
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

@Composable
fun CustomBasicTextField(
    text: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = text,
        onValueChange = onValueChange,
        cursorBrush = SolidColor(White),
        textStyle = TextStyle.Default.copy(color = White, fontSize = 16.sp),
        modifier = modifier
            .background(color = White10, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 16.dp),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = hint,
                        style = TextStyle(fontSize = 16.sp, color = Color.DarkGray),
                        modifier = Modifier
                            .align(alignment = Alignment.CenterStart)
                            .padding(start = 16.dp)
                    )
                }
                innerTextField()
            }
        }
    )
}