package com.ethan.android.notepad.ui.component.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.ui.component.view.DropdownMenu
import com.ethan.android.notepad.ui.component.view.MyTextField
import com.ethan.android.notepad.ui.component.view.TelephoneInput
import com.ethan.android.notepad.ui.component.view.TextInput
import com.ethan.android.notepad.ui.component.view.VerificationCodeInput
import com.ethan.android.notepad.common.view.StatusBarsViewWhite

@Composable
@Preview
fun TextFieldPage() {
    //todo BasicTextField
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

    Column(modifier = Modifier.fillMaxSize().background(color = Black).statusBarsPadding().padding(horizontal = 16.dp)) {
        StatusBarsViewWhite(title = "TextField定制输入框")

        LazyColumn {
            item {
                Spacer(Modifier.height(12.dp))
                Text(text = "输入框", color = White, fontSize = 14.sp)
                TextInput(email, { email = it }, "请输入邮箱地址")
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text(text = "手机号输入框", color = White, fontSize = 14.sp)
                TelephoneInput()
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text(text = "验证码输入框", color = White, fontSize = 14.sp)
                VerificationCodeInput()
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text(text = "下拉选择框", color = White, fontSize = 14.sp)
                DropdownMenu(feedbackType, options, onOptionSelected = { feedbackType = it })
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text(text = "文本域", color = White, fontSize = 14.sp)
                MyTextField(content = content, hint = "告诉我们您对程序的反馈") { content = it }
            }

            item {
                Spacer(Modifier.height(12.dp))
                Text(text = "文本域（带删除和文本数量显示）", color = White, fontSize = 14.sp)
                MyTextField()
            }

            item {
                Spacer(Modifier.height(20.dp).navigationBarsPadding())
            }
        }
    }
}