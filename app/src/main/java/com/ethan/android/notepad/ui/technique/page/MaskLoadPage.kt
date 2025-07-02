package com.ethan.android.notepad.ui.technique.page

import android.content.Intent
import android.graphics.Bitmap
import android.os.SystemClock
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import com.ethan.android.notepad.databinding.LayoutMaskLoadBinding
import com.ethan.android.notepad.extension.findBaseActivityVBind
import com.ethan.android.notepad.ui.custom.view.TitleCardView
import com.ethan.android.notepad.ui.technique.view.BlurHashActivity
import com.ethan.maskload.BlurHashDecoder
import com.ethan.android.notepad.extension.dp as dpInt

/**
 * 渐进式加载:由模糊到清晰
 * https://blog.51cto.com/u_16213304/11932585#:~:text=%E6%9C%AC%E6%96%87%E5%B0%86%E4%BB%8B%E7%BB%8DAndroid%E4%B8%AD%E6%B8%90%E8%BF%9B%E5%BC%8F%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E7%9A%84%E5%9F%BA%E6%9C%AC%E6%A6%82%E5%BF%B5%E3%80%81%E4%BC%98%E5%8A%BF%E3%80%81%E5%AE%9E%E7%8E%B0%E6%96%B9%E5%BC%8F%EF%BC%8C%E5%B9%B6%E6%8F%90%E4%BE%9B%E7%9B%B8%E5%BA%94%E7%9A%84%E4%BB%A3%E7%A0%81%E7%A4%BA%E4%BE%8B%E3%80%82%20%E6%B8%90%E8%BF%9B%E5%BC%8F%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E6%98%AF%E6%8C%87%E5%9C%A8%E5%8A%A0%E8%BD%BD%E5%9B%BE%E7%89%87%E6%97%B6%EF%BC%8C%E5%85%88%E5%8A%A0%E8%BD%BD%E5%87%BA%E4%BD%8E%E5%88%86%E8%BE%A8%E7%8E%87%E7%9A%84%E7%89%88%E6%9C%AC%EF%BC%8C%E7%84%B6%E5%90%8E%E9%80%90%E6%AD%A5%E6%9B%BF%E6%8D%A2%E6%88%90%E6%9B%B4%E9%AB%98%E5%88%86%E8%BE%A8%E7%8E%87%E7%9A%84%E7%89%88%E6%9C%AC%E3%80%82,%E8%BF%99%E6%A0%B7%E5%81%9A%E7%9A%84%E5%A5%BD%E5%A4%84%E6%98%AF%E7%94%A8%E6%88%B7%E5%8F%AF%E4%BB%A5%E5%9C%A8%E7%AD%89%E5%BE%85%E9%AB%98%E8%B4%A8%E9%87%8F%E5%9B%BE%E5%83%8F%E5%8A%A0%E8%BD%BD%E7%9A%84%E8%BF%87%E7%A8%8B%E4%B8%AD%EF%BC%8C%E7%9C%8B%E5%88%B0%E4%B8%80%E4%B8%AA%E6%A8%A1%E7%B3%8A%E4%BD%86%E5%8F%AF%E5%B1%95%E7%A4%BA%E7%9A%84%E5%9B%BE%E5%83%8F%EF%BC%8C%E5%87%8F%E5%B0%91%E4%BA%86%E7%A9%BA%E7%99%BD%E7%94%BB%E9%9D%A2%E7%9A%84%E5%87%BA%E7%8E%B0%EF%BC%8C%E6%8F%90%E9%AB%98%E4%BA%86%E7%94%A8%E6%88%B7%E4%BD%93%E9%AA%8C%E3%80%82%20%E6%9F%90%E4%BA%9B%E5%BA%93%EF%BC%88%E5%A6%82%20Glide%E3%80%81Picasso%EF%BC%89%E5%8F%AF%E4%BB%A5%E5%B8%AE%E5%8A%A9%E6%88%91%E4%BB%AC%E8%BD%BB%E6%9D%BE%E5%AE%9E%E7%8E%B0%E6%B8%90%E8%BF%9B%E5%BC%8F%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E3%80%82
 */
@Composable
fun MaskLoadPage() {
    val context = LocalContext.current

    LazyColumn(modifier = Modifier.fillMaxSize().statusBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {

        item {
            TitleCardView("模糊加载", modifier = Modifier.height(250.dp)) {
                MaskLoadImageView()
            }
        }

        item {
            AndroidView(factory = { c ->
                val binding = LayoutMaskLoadBinding.inflate(LayoutInflater.from(c))
                binding.tvEncode.setOnClickListener {
                    context.findBaseActivityVBind()?.let {
                        context.startActivity(Intent(it, BlurHashActivity::class.java))
                    }
                }
                binding.tvDecode.setOnClickListener {
                    var bitmap: Bitmap? = null
                    val time = timed {
                        bitmap = BlurHashDecoder.decode(binding.etInput.text.toString(), 24, 48)
                    }
                    binding.ivResult.layoutParams = LinearLayout.LayoutParams(240.dpInt, 480.dpInt)
                    binding.ivResult.setImageBitmap(bitmap)
                    binding.ivResultTime.text = "Time: $time ms"
                }
                binding.root
            },
                modifier = Modifier.padding(top = 20.dp))
        }

    }
}

@Composable
@Preview
fun MaskLoadImageView(maskCode: String = "URKJoW-;nS=cqFx[%fNv%#odNwNHEOVs\$*xG") {
    val isShowMask = remember { mutableStateOf(true) }

    Box(modifier = Modifier.width(120.dp).aspectRatio(0.5625f).clip(RoundedCornerShape(12.dp))) {
        val bitmap = BlurHashDecoder.decode(maskCode, 24, 48)
        AsyncImage(
            model = "https://material.hitpaw.com/static/c8dcdb02426dea4fd90a303bf3fde30b/upload/475af39b9672e4fce58153cc2de26a3eFin-tasticMermaid.webp",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onLoading = {
                isShowMask.value = true
            },
            onSuccess = {
                isShowMask.value = false
            },
        )

        if (isShowMask.value) {
            bitmap?.let { Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
        }
    }
}

/**
 * 计算耗时
 */
private inline fun timed(function: () -> Unit): Long {
    val start = SystemClock.elapsedRealtime()
    function()
    return SystemClock.elapsedRealtime() - start
}