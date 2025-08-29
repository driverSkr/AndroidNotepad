package com.ethan.android.notepad.ui.material

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
import com.ethan.android.notepad.ui.material.compare.page.ImageComparePage
import com.ethan.android.notepad.ui.material.dialog.page.DialogPage
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf

class MaterialPreviewActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context, pageType: PageType) {
            context.intentOf<MaterialPreviewActivity> {
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
                                PageType.Dialog -> DialogPage()
                                PageType.ImageCompare -> ImageComparePage()
                                null -> TODO()
                            }
                        }
                    }
                }
            }
        }
    }
}