package com.ethan.android.notepad.ui.custom

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
import com.ethan.android.notepad.ui.custom.model.PageType
import com.ethan.android.notepad.ui.custom.page.ImageComparePage
import com.ethan.android.notepad.ui.technique.page.TechniquePage
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf

class CustomPreviewActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context, pageType: PageType) {
            context.intentOf<CustomPreviewActivity> {
                +("pageType" to pageType)
                startActivity(context)
            }
        }
    }

    private val pageType by bundle<PageType>("pageType")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            when(pageType) {
                                PageType.ImageComparePage -> ImageComparePage()
                                else -> TechniquePage()
                            }
                        }
                    }
                }
            }
        }
    }
}