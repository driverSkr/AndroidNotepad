package com.ethan.android.notepad.ui.dialog.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Black0C0C0F
import com.ethan.android.notepad.theme.Black0C0C0F_20
import com.ethan.android.notepad.theme.Black0C0C0F_30
import com.ethan.android.notepad.theme.Black0C0C0F_40
import com.ethan.android.notepad.theme.Black10
import com.ethan.android.notepad.theme.Black40
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.Red
import com.ethan.android.notepad.theme.Transparent
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.ui.component.page.ClickableText
import com.ethan.android.notepad.utils.NetWorkUtils
import com.ethan.android.notepad.utils.ToastType
import com.ethan.android.notepad.utils.antiShakeClick
import com.ethan.android.notepad.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberLoginDialog(context: Context): SheetState {

    val sheetState = rememberModalBottomSheetState(true, confirmValueChange = { true })
    val scope = rememberCoroutineScope()

    var phone by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var countdown by remember { mutableIntStateOf(0) } // 倒计时剩余时间
    var isCounting by remember { mutableStateOf(false) } // 是否正在倒计时
    var isPhoneValid by remember { mutableStateOf(true) } // 手机号是否合法
    var isChecked by remember { mutableStateOf(false) } // 是否勾选协议
    val btnText = if (isCounting) "${countdown}s后重发" else "获取验证码"
    val selectState =
        if (isChecked) R.drawable.svg_icon_selected else R.drawable.svg_icon_not_selected

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

    // 隐藏即重置状态
    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            phone = ""
            verificationCode = ""
            isCounting = false
            isChecked = false
        }
    }

    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { scope.launch(Dispatchers.Default) { sheetState.hide() } },
            sheetState = sheetState,
            containerColor = White,
            dragHandle = null,
            contentWindowInsets = { WindowInsets(0,0,0,0) },
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(painter = painterResource(R.drawable.svg_icon_close),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 12.dp, end = 12.dp)
                            .clickable {
                                scope.launch(Dispatchers.Default) {
                                    sheetState.hide()
                                }
                            }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // 手机号输入框
                    TextField(
                        value = phone,
                        textStyle = TextStyle(
                            color = Black0C0C0F,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W500
                        ),
                        onValueChange = { newValue ->
                            if (newValue.length <= 11) { // 限制输入长度为 11 位
                                // 过滤非数字字符
                                val filteredValue = newValue.filter { it.isDigit() }
                                phone = filteredValue
                                isPhoneValid = validatePhoneNumber(phone) // 校验手机号
                            }
                        }, // 只保留数字
                        placeholder = {
                            Text(
                                "请输入手机号",
                                fontSize = 16.sp,
                                color = Black0C0C0F_30,
                                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Black10, // 聚焦时背景颜色
                            unfocusedContainerColor = Black10, // 未聚焦时背景颜色
                            disabledContainerColor = Black10, // 禁用时背景颜色
                            cursorColor = Black40, // 光标颜色
                            focusedTextColor = Black, // 聚焦时文本颜色
                            unfocusedTextColor = Black, // 未聚焦时文本颜色
                            focusedIndicatorColor = Transparent, // 聚焦时指示器颜色
                            unfocusedIndicatorColor = Transparent, // 未聚焦时指示器颜色
                            disabledIndicatorColor = Transparent, // 禁用时指示器颜色
                        ),
                        shape = MaterialTheme.shapes.small.copy(
                            topStart = CornerSize(12.dp),
                            topEnd = CornerSize(12.dp),
                            bottomStart = CornerSize(12.dp),
                            bottomEnd = CornerSize(12.dp)
                        ),
                        singleLine = true,
                        leadingIcon = {
                            Row(modifier = Modifier.padding(start = 16.dp)) {
                                Text(
                                    text = "+86",
                                    color = Black0C0C0F,
                                    fontSize = 16.sp,
                                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500),
                                    // modifier = Modifier.padding(end = 16.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Spacer(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(20.dp)
                                        .background(
                                            color = Black0C0C0F_20,
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                        },
                        visualTransformation = PhoneNumberTransformation(), // 使用自定义的视觉转换
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), // 设置键盘类型为数字键盘
                        modifier = Modifier.fillMaxWidth()
                    )
                    // 显示校验提示
                    if (phone.isNotEmpty() && !isPhoneValid) {
                        Text(
                            text = "请输入合法的手机号",
                            color = Red, // 红色字体提示
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // 验证码输入框
                    TextField(
                        value = verificationCode,
                        textStyle = TextStyle(
                            color = Black0C0C0F,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W500
                        ),
                        onValueChange = { verificationCode = it },
                        placeholder = {
                            Text(
                                "输入验证码",
                                fontSize = 16.sp,
                                color = Black0C0C0F_30,
                                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Black10, // 聚焦时背景颜色
                            unfocusedContainerColor = Black10, // 未聚焦时背景颜色
                            disabledContainerColor = Black10, // 禁用时背景颜色
                            cursorColor = Black40, // 光标颜色PurpleB31CF1
                            focusedTextColor = Black, // 聚焦时文本颜色
                            unfocusedTextColor = Black, // 未聚焦时文本颜色
                            focusedIndicatorColor = Transparent, // 聚焦时指示器颜色
                            unfocusedIndicatorColor = Transparent, // 未聚焦时指示器颜色
                            disabledIndicatorColor = Transparent, // 禁用时指示器颜色
                        ),
                        shape = MaterialTheme.shapes.small.copy(
                            topStart = CornerSize(12.dp),
                            topEnd = CornerSize(12.dp),
                            bottomStart = CornerSize(12.dp),
                            bottomEnd = CornerSize(12.dp)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // 设置键盘类型为数字键盘
                        trailingIcon = {
                            Text(
                                text = btnText,
                                color = if (isCounting) Black0C0C0F_40 else Black0C0C0F,
                                fontSize = 16.sp,
                                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500),
                                modifier = Modifier
                                    .clickable(
                                        enabled = !isCounting,
                                        onClick = {
                                            if (isPhoneValid && phone.isNotEmpty()) {
                                                isCounting = true // 开始倒计时
                                                countdown = 60 // 设置倒计时时间
                                            } else {
                                                "请输入正确的手机号".showToast(
                                                    context,
                                                    ToastType.HINT
                                                )
                                            }
                                        }
                                    )
                                    .padding(end = 16.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(painter = painterResource(selectState),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 4.dp)
                                .antiShakeClick { isChecked = !isChecked })
                        ClickableText()
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Black, shape = RoundedCornerShape(999.dp))
                        .padding(vertical = 15.dp)
                        .clickable {
                            if (!isPhoneValid || phone.isEmpty()) {
                                "请输入正确的手机号".showToast(context, ToastType.HINT)
                            } else if (verificationCode.isEmpty()) {
                                "请输入验证码".showToast(context, ToastType.HINT)
                            } else if (!isChecked) {
                                "请勾选协议".showToast(context, ToastType.HINT)
                            } else {
                                scope.launch(Dispatchers.Default) {
                                    if (NetWorkUtils.checkNetWork()) {
                                        sheetState.hide()
                                        withContext(Dispatchers.Main) {
                                            "登录成功".showToast(context, ToastType.SUCCESS)
                                        }
                                    } else {
                                        sheetState.hide()
                                        scope.launch(Dispatchers.Main) {
                                            "网络异常".showToast(context, ToastType.ERROR)
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Text(
                            "登录",
                            fontSize = 19.sp,
                            textAlign = TextAlign.Center,
                            color = White,
                            style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W800),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
    return sheetState
}

// 校验手机号是否合法
fun validatePhoneNumber(phone: String): Boolean {
    val regex = Regex("^(1[3-9]\\d{9}|(147|148|166|195|198|199)\\d{8})$")
    return regex.matches(phone)
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