package com.ethan.android.notepad.ui.main

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.ethan.android.notepad.base.BaseActivityVBind
import com.ethan.android.notepad.databinding.LayoutComposeContainerBinding
import com.ethan.android.notepad.theme.ComposeProjectTheme
import com.ethan.android.notepad.theme.Transparent
import com.ethan.android.notepad.ui.main.page.MainPage
import com.skydoves.bundler.intentOf

//todo 1.状态同步的几种实现方式（接口回调、livedata、flow、EventBus、BroadcastReceiver、SharedPreferences/DataStore、mutableStateOf）
//todo 2.音频录制、播放的多种方式
//todo 3.数据库存储的使用（room）
//todo 4.视频播放的多种方式（MediaPlayer、ExoPlayer）
//todo 5.音视频裁剪（ffmpeg）
//todo 6.配置AI工具帮助写代码
//todo 7.分页（Flow、自定义）
//todo 8.侧滑删除组件
//todo 9.权限请求方式
//todo 10.主包蒙层
//todo 11.多语言设置及语言切换
//todo 12.谷歌登录、支付组件
//todo 13.支付宝、微信支付组件
//todo 14.边框渐变色
class MainActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context) {
            context.intentOf<MainActivity> {
                startActivity(context)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            MainPage()
                        }
                    }
                }
            }
        }
    }
}