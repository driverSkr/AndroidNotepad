package com.ethan.android.notepad.ui.media.image

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
import com.ethan.android.notepad.ui.media.image.context.ImagePageType
import com.ethan.android.notepad.ui.media.image.page.ImageComparePage
import com.ethan.android.notepad.ui.media.image.page.ImageLoadPage
import com.ethan.android.notepad.ui.media.image.page.ImageZoomPage
import com.ethan.android.notepad.ui.media.image.page.WatermarkImagePage
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf

class ImagePreviewActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context, pageType: ImagePageType) {
            context.intentOf<ImagePreviewActivity> {
                +("pageType" to pageType)
                startActivity(context)
            }
        }
    }

    private val pageType by bundle<ImagePageType>("pageType")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            when(pageType) {
                                ImagePageType.Watermark -> WatermarkImagePage()
                                ImagePageType.ImageZoom -> ImageZoomPage()
                                ImagePageType.ImageCompare -> ImageComparePage()
                                ImagePageType.ImageLoad -> ImageLoadPage()
                                else -> WatermarkImagePage()
                            }
                        }
                    }
                }
            }
        }
    }
}