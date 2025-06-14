package com.ethan.android.notepad.ui.technique.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.LanguageUtils
import com.ethan.android.notepad.R
import com.ethan.android.notepad.extension.findBaseActivityVBind
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.Black40
import com.ethan.android.notepad.theme.Black80
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.Red
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White444447
import com.ethan.android.notepad.ui.custom.view.StatusBarsView
import com.ethan.android.notepad.ui.custom.view.TitleCardView
import com.ethan.android.notepad.utils.DataHelper
import com.ethan.android.notepad.utils.antiShakeClick
import java.util.Locale
import kotlin.random.Random

/**
 * 多语言设置
 * 可以手动设置多语言。同时BaseActivityVBind里的
 */
@Composable
@Preview
fun LanguagePage() {
    val context = LocalContext.current
    val currentLanguage = remember { mutableStateOf(Locale(DataHelper.getLanguage(context) ?: LanguageUtils.getSystemLanguage().language)) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var isCache by remember { mutableStateOf(false) }
    val selectTextIndex = remember { mutableIntStateOf(Random.nextInt(0, 10)) }
    val corpus = listOf(
        R.string.avatars_corpus1,
        R.string.avatars_corpus2,
        R.string.avatars_corpus3,
        R.string.avatars_corpus4,
        R.string.avatars_corpus5,
        R.string.avatars_corpus6,
        R.string.avatars_corpus7,
        R.string.avatars_corpus8,
        R.string.avatars_corpus9,
        R.string.avatars_corpus10
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "多语言自动切换", true)
        //强制刷新键（key）
        key(refreshKey) {
            TitleCardView(isShowTitle = false, modifier = Modifier
                .height(160.dp)
                .padding(horizontal = 8.dp, vertical = 12.dp)) {
                Box(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                ) {
                    Text(
                        stringResource(corpus[selectTextIndex.intValue]),
                        color = Black,
                        fontSize = 14.sp,
                        style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                        modifier = Modifier.padding(bottom = 40.dp), // 底部增加空白
                    )
                }

                Row(modifier = Modifier.align(Alignment.BottomStart), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "是否保存设置：",
                        color = Black80,
                        fontSize = 12.sp,
                        style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500),
                    )

                    Switch(isCache, onCheckedChange = { isCache = it }, modifier = Modifier.height(30.dp))
                }

                Text(
                    text = stringResource(R.string.avatars_switch_another),
                    color = White,
                    fontSize = 12.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W500),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(color = White444447, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 18.dp, vertical = 7.5.dp)
                        .antiShakeClick {
                            selectTextIndex.intValue = (selectTextIndex.intValue + 1) % corpus.size
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        TitleCardView("切换多语言", modifier = Modifier.height(36.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("简中",
                    color = if (currentLanguage.value.language == "zh") Red else Black,
                    fontSize = 14.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).antiShakeClick {
                        context.findBaseActivityVBind()?.setLocale("zh")
                        currentLanguage.value = Locale("zh")
                        if (isCache) {
                            DataHelper.setLanguage(context, "zh")
                        }
                        refreshKey ++
                    }
                )
                VerticalDivider(
                    thickness = 2.dp,
                    color = Black40,
                    modifier = Modifier.padding(vertical = 5.dp).clip(RoundedCornerShape(999.dp))
                )
                Text("繁中",
                    color = if (currentLanguage.value.language == "tw") Red else Black,
                    fontSize = 14.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).antiShakeClick {
                        context.findBaseActivityVBind()?.setLocale("tw")
                        currentLanguage.value = Locale("tw")
                        if (isCache) {
                            DataHelper.setLanguage(context, "tw")
                        }
                        refreshKey ++
                    }
                )
                VerticalDivider(
                    thickness = 2.dp,
                    color = Black40,
                    modifier = Modifier.padding(vertical = 5.dp).clip(RoundedCornerShape(999.dp))
                )
                Text("英语",
                    color = if (currentLanguage.value.language == "en") Red else Black,
                    fontSize = 14.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).antiShakeClick {
                        context.findBaseActivityVBind()?.setLocale("en")
                        currentLanguage.value = Locale("en")
                        if (isCache) {
                            DataHelper.setLanguage(context, "en")
                        }
                        refreshKey ++
                    }
                )
                VerticalDivider(
                    thickness = 2.dp,
                    color = Black40,
                    modifier = Modifier.padding(vertical = 5.dp).clip(RoundedCornerShape(999.dp))
                )
                Text("日语",
                    color = if (currentLanguage.value.language == "ja") Red else Black,
                    fontSize = 14.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).antiShakeClick {
                        context.findBaseActivityVBind()?.setLocale("ja")
                        currentLanguage.value = Locale("ja")
                        if (isCache) {
                            DataHelper.setLanguage(context, "ja")
                        }
                        refreshKey ++
                    }
                )
                VerticalDivider(
                    thickness = 2.dp,
                    color = Black40,
                    modifier = Modifier.padding(vertical = 5.dp).clip(RoundedCornerShape(999.dp))
                )
                Text("韩语",
                    color = if (currentLanguage.value.language == "ko") Red else Black,
                    fontSize = 14.sp,
                    style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).antiShakeClick {
                        context.findBaseActivityVBind()?.setLocale("ko")
                        currentLanguage.value = Locale("ko")
                        if (isCache) {
                            DataHelper.setLanguage(context, "ko")
                        }
                        refreshKey ++
                    }
                )
            }
        }
    }
}