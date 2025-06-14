package com.ethan.android.notepad.ui.component.page

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.ethan.android.notepad.R
import com.ethan.android.notepad.base.Constants
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Blue
import com.ethan.android.notepad.theme.Cyan
import com.ethan.android.notepad.theme.DarkOrange
import com.ethan.android.notepad.theme.Pink40
import com.ethan.android.notepad.utils.LaunchUtils
import com.ethan.android.notepad.utils.TextSpanUtils

@Composable
@Preview
/**
 * Text 对应View中的 TextView
 */
fun TextPage() {
    val brushColor = Brush.horizontalGradient(colorStops = arrayOf(0.5f to DarkOrange, 1f to Cyan))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(all = 10.dp)
    ) {
        Text(
            text = "一个简单的文本",
            color = Blue,
            fontWeight = FontWeight.W400,
            fontSize = 24.sp,
            fontStyle = FontStyle.Normal,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "使用斜体",
            color = Black,
            fontWeight = FontWeight.W400,
            fontSize = 24.sp,
            fontStyle = FontStyle.Italic,
            softWrap = false,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "渐变色文本",
            style = TextStyle(
                brush = brushColor,
                fontWeight = FontWeight.W700,
                fontSize = 24.sp,
                fontStyle = FontStyle.Italic
            ),
            softWrap = false,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("超限制展示省略号",
            fontSize = 16.sp,
            color = Pink40,
            fontWeight = FontWeight.W400,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(100.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        ClickableText()
    }
}

@Composable
fun ClickableText() {
    AndroidView(
        factory = { context ->
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.textview, null, false)
            val content = "已阅读并同意用户协议和隐私政策"
            val span1 = "用户协议"
            val span2 = "隐私政策"
            val click1 = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    LaunchUtils.launchWeb(context, Constants.TERMS_OF_USE, "用户协议")
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }
            }
            val click2 = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    LaunchUtils.launchWeb(context, Constants.PRIVACY_POLICY, "隐私政策")
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }
            }
            val spans = arrayOf(
                TextSpanUtils.Span(span1, R.color._0088FF, click1),
                TextSpanUtils.Span(span2, R.color._0088FF, click2)
            )
            TextSpanUtils.setSpanText(context, content, spans, view.findViewById(R.id.text))

            view
        },
        modifier = Modifier.wrapContentSize()
    )
}