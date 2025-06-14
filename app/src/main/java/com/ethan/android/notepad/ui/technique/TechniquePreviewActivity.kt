package com.ethan.android.notepad.ui.technique

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
import com.ethan.android.notepad.ui.technique.model.PageType
import com.ethan.android.notepad.ui.technique.page.LanguagePage
import com.ethan.android.notepad.ui.technique.page.LoadAnimationPage
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf

class TechniquePreviewActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context, pageType: PageType) {
            context.intentOf<TechniquePreviewActivity> {
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
                                PageType.LanguagePage -> LanguagePage()
                                PageType.LoadAnimation -> LoadAnimationPage()
                                else -> LanguagePage()
                            }
                        }
                    }
                }
            }
        }
    }
}