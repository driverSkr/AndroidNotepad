package com.ethan.android.notepad.ui.technique.page

import android.view.LayoutInflater
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.ethan.android.notepad.databinding.ImageLoadAnimationBinding
import com.ethan.android.notepad.utils.AlbumUtils

@Composable
@Preview
fun LoadAnimationPage() {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { c ->
            val binding = ImageLoadAnimationBinding.inflate(LayoutInflater.from(c))
            AlbumUtils.loadImageBlurToClear(c, "https://material.hitpaw.com/static/90687c340367c36e2d81871be53f67dc/upload/f97d3e341a1ed168502c4fa0c3ad83ec11.png", binding.loadImg)
            Glide.with(c)
                .load("https://material.hitpaw.com/static/2b3c945069f76d51bf3dff9c078fcd91/upload/b84ea9b05f6fff85583dbf2d76d3f17810.png")
                .into(binding.loadImg1)
            binding.root
        },
            modifier = Modifier.align(Alignment.Center ))
    }
}