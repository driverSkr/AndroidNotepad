package com.ethan.android.notepad.ui.component.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black0C0C0F
import com.ethan.android.notepad.theme.Black242427
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.Transparent
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White10
import com.ethan.android.notepad.theme.White20
import com.ethan.android.notepad.theme.White30
import com.ethan.android.notepad.theme.White40
import com.ethan.android.notepad.utils.VerificationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
 * 手机号输入框
 */
@Composable
fun TelephoneInput() {
    var phone by remember { mutableStateOf("") }
    var isPhoneValid by remember { mutableStateOf(true) } // 手机号是否合法

    // 手机号输入框
    TextField(
        value = phone,
        textStyle = TextStyle(color = White, fontSize = 20.sp, fontWeight = FontWeight.W500),
        onValueChange = { newValue ->
            if (newValue.length <= 11) { // 限制输入长度为 11 位
                // 过滤非数字字符
                val filteredValue = newValue.filter { it.isDigit() }
                phone = filteredValue
                isPhoneValid = VerificationUtils.validatePhoneNumber(phone) // 校验手机号
            }
        }, // 只保留数字
        placeholder = { Text("请输入手机号", fontSize = 16.sp, color = White30, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500)) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White10, // 聚焦时背景颜色
            unfocusedContainerColor = White10, // 未聚焦时背景颜色
            disabledContainerColor = White10, // 禁用时背景颜色
            cursorColor = White40, // 光标颜色
            focusedTextColor = White, // 聚焦时文本颜色
            unfocusedTextColor = White, // 未聚焦时文本颜色
            focusedIndicatorColor = Transparent, // 聚焦时指示器颜色
            unfocusedIndicatorColor = Transparent, // 未聚焦时指示器颜色
            disabledIndicatorColor = Transparent, // 禁用时指示器颜色
        ),
        shape = MaterialTheme.shapes.small.copy(
            topStart = CornerSize(12.dp),
            topEnd = CornerSize(12.dp),
            bottomStart = CornerSize(12.dp),
            bottomEnd = CornerSize(12.dp)),
        singleLine = true,
        leadingIcon = {
            Row(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = "+86",
                    color = White,
                    fontSize = 16.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500),
                    // modifier = Modifier.padding(end = 16.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Spacer(modifier = Modifier.width(1.dp).height(20.dp).background(color = White20, shape = RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(16.dp))
            }
        },
        visualTransformation = PhoneNumberTransformation(), // 使用自定义的视觉转换
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // 设置键盘类型为数字键盘
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * 验证码输入框
 */
@Composable
fun VerificationCodeInput() {
    val scope = rememberCoroutineScope()
    var verificationCode by remember { mutableStateOf("") }
    var countdown by remember { mutableIntStateOf(0) } // 倒计时剩余时间
    var isCounting by remember { mutableStateOf(false) } // 是否正在倒计时
    val btnText = if (isCounting) "${countdown}s后重发" else "获取验证码"

    // 启动倒计时
    LaunchedEffect(isCounting) {
        if (isCounting) {
            while (countdown > 0) {
                delay(1000) // 等待 1 秒
                countdown--
            }
            isCounting = false // 倒计时结束
        }
    }

    TextField(
        value = verificationCode,
        textStyle = TextStyle(color = White, fontSize = 20.sp, fontWeight = FontWeight.W500),
        onValueChange = {
             if (it.length <= 6) {
                 verificationCode = it
             }
        },
        placeholder = { Text("请输入验证码", fontSize = 16.sp, color = White30, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500)) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White10, // 聚焦时背景颜色
            unfocusedContainerColor = White10, // 未聚焦时背景颜色
            disabledContainerColor = White10, // 禁用时背景颜色
            cursorColor = White40, // 光标颜色PurpleB31CF1
            focusedTextColor = White, // 聚焦时文本颜色
            unfocusedTextColor = White, // 未聚焦时文本颜色
            focusedIndicatorColor = Transparent, // 聚焦时指示器颜色
            unfocusedIndicatorColor = Transparent, // 未聚焦时指示器颜色
            disabledIndicatorColor = Transparent, // 禁用时指示器颜色
        ),
        shape = MaterialTheme.shapes.small.copy(
            topStart = CornerSize(12.dp),
            topEnd = CornerSize(12.dp),
            bottomStart = CornerSize(12.dp),
            bottomEnd = CornerSize(12.dp)),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // 设置键盘类型为数字键盘
        trailingIcon = {
            Text(
                text = btnText,
                color = if (isCounting) White40 else White,
                fontSize = 16.sp,
                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500),
                modifier = Modifier
                    .clickable (
                        enabled = !isCounting,
                        onClick = {
                            scope.launch(Dispatchers.Default) {
                                withContext(Dispatchers.Main) {
                                    isCounting = true // 开始倒计时
                                    countdown = 60 // 设置倒计时时间
                                }
                            }
                        }
                    )
                    .padding(end = 16.dp)
            )
        },
        modifier = Modifier.fillMaxWidth()
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
    modifier: Modifier = Modifier,
    content: String,
    hint: String,
    maxLength: Int = Int.MAX_VALUE,
    onValueChange: (String) -> Unit,
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

/**
 * 文本域（带删除和文本数量显示）
 */
@Composable
fun MyTextField(
    modifier: Modifier = Modifier,
    maxLength: Int = 1500
) {
    val prompt = remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxWidth().height(248.dp).background(color = Black242427, shape = RoundedCornerShape(8.dp))) {
        TextField(
            value = prompt.value,
            onValueChange = { prompt.value = it.take(maxLength) },
            placeholder = { Text("描述你想生成的画面和动作，例如微笑特写，张开双臂，冷色调等", fontSize = 12.sp, color = White30, fontWeight = FontWeight.W400) },
            textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.W400),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Transparent, // 聚焦时背景颜色
                unfocusedContainerColor = Transparent, // 未聚焦时背景颜色
                disabledContainerColor = Transparent, // 禁用时背景颜色
                cursorColor = White, // 光标颜色
                focusedTextColor = White, // 聚焦时文本颜色
                unfocusedTextColor = White, // 未聚焦时文本颜色
                focusedIndicatorColor = Transparent, // 聚焦时指示器颜色
                unfocusedIndicatorColor = Transparent, // 未聚焦时指示器颜色
                disabledIndicatorColor = Transparent, // 禁用时指示器颜色
            ),
            enabled = true,
            modifier = modifier.padding(bottom = 18.dp).fillMaxSize()
        )

        Row(modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(prompt.value.isNotEmpty()) {
                Image(
                    painter = painterResource(R.drawable.svg_icon_delete),
                    contentDescription = "Left icon",
                    modifier = Modifier.size(20.dp).clickable{ prompt.value = "" }
                )
            }

            Spacer(modifier.weight(1f))

            Row(modifier = Modifier) {
                Text("${prompt.value.length}", fontSize = 12.sp, color = if (prompt.value.isNotEmpty()) White else White30, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                Text("/$maxLength", fontSize = 12.sp, color = White30, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
            }
        }
    }
}

private class PhoneNumberTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // 格式化电话号码为 344 格式
        val formatted = buildString {
            for (i in text.indices) {
                append(text[i])
                when (i) {
                    2 -> append(" ") // 在第 3 位后插入 -
                    6 -> append(" ")  // 在第 7 位后插入空格
                }
            }
        }

        // 创建 OffsetMapping，用于映射实际文本和显示文本之间的光标位置
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 6 -> offset + 1 // 插入了一个 -
                    else -> offset + 2       // 插入了一个 - 和一个空格
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 3 -> offset
                    offset <= 8 -> offset - 1 // 减去一个 -
                    else -> offset - 2       // 减去一个 - 和一个空格
                }
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}