package com.ethan.android.notepad.ui.technique.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ethan.android.notepad.ui.custom.model.CardItem
import com.ethan.android.notepad.ui.custom.view.ListCardView
import com.ethan.android.notepad.ui.custom.view.StatusBarsView
import com.ethan.android.notepad.ui.technique.TechniquePreviewActivity
import com.ethan.android.notepad.ui.technique.model.PageType

/**
 * 工作中学到的技巧、技术、知识合集
 * Modifier.onGloballyPositioned {  }
 * Modifier.onPlaced {  }
 */
@Composable
@Preview
fun TechniquePage() {

    val context = LocalContext.current

    val items1 = listOf(
        CardItem("Pair和Triple的使用", true, isCompleted = false),
        CardItem("aspectRatio设置宽高比", true, isCompleted = false),
        CardItem("共享元素转场动画", true, isCompleted = false),
        CardItem("模糊到清晰加载", true, isCompleted = false) { TechniquePreviewActivity.launch(context, PageType.LoadAnimation) },
    )

    val items2 = listOf(
        CardItem("状态同步的几种实现方式", true, isCompleted = false),
        CardItem("分页请求的实现", true, isCompleted = false),
        CardItem("权限请求方式", true, isCompleted = false),
        CardItem("多语言设置及语言切换", true) { TechniquePreviewActivity.launch(context, PageType.LanguagePage) },
        CardItem("FlowBus的使用", true, isCompleted = false),
    )

    val items3 = listOf(
        CardItem("相册选择页", true, isCompleted = false),
        CardItem("腾讯缓存组件MMKV的使用", true, isCompleted = false),
        CardItem("腾讯pag动画组件libpag的使用", true, isCompleted = false),
        CardItem("高斯模糊组件haze的使用", true, isCompleted = false),
        CardItem("谷歌登录组件", true, isCompleted = false),
        CardItem("阿里云融合认证", true, isCompleted = false),
        CardItem("支付宝、微信支付组件", true, isCompleted = false),
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        StatusBarsView("技术、技巧、知识", true)

        ListCardView(items = items1)

        Spacer(modifier = Modifier.height(20.dp))
        ListCardView(items = items2)

        Spacer(modifier = Modifier.height(20.dp))
        ListCardView(items = items3, modifier = Modifier.navigationBarsPadding())
    }
}