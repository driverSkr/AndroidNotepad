package com.ethan.android.notepad.ui.media.video

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
import com.ethan.android.notepad.ui.media.video.page.VideoPage
import com.skydoves.bundler.intentOf

class VideoActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context) {
            context.intentOf<VideoActivity> {
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
                        Surface(modifier = Modifier.Companion.fillMaxSize(), color = Transparent) {
                            VideoPage()
                        }
                    }
                }
            }
        }
    }
}