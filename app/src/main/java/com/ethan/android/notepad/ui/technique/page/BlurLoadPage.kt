package com.ethan.android.notepad.ui.technique.page

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.ethan.android.notepad.common.utils.BitmapUtils
import com.ethan.android.notepad.common.utils.MediaUtils
import com.ethan.android.notepad.common.utils.ToastType
import com.ethan.android.notepad.common.utils.showToast
import com.ethan.android.notepad.common.view.TitleCardView
import com.ethan.android.notepad.repository.data.blurDataSource
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.theme.White16
import com.ethan.android.notepad.ui.material.dialog.view.rememberLoadingDialog
import com.ethan.android.notepad.ui.media.image.view.CommonMaskLoadImageView
import com.ethan.maskload.BlurHash
import com.ethan.maskload.BlurHashDecoder
import com.ethan.videoediting.FfmpegVE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 渐进式加载:由模糊到清晰
 * https://blog.51cto.com/u_16213304/11932585#:~:text=%E6%9C%AC%E6%96%87%E5%B0%86%E4%BB%8B%E7%BB%8DAndroid%E4%B8%AD%E6%B8%90%E8%BF%9B%E5%BC%8F%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E7%9A%84%E5%9F%BA%E6%9C%AC%E6%A6%82%E5%BF%B5%E3%80%81%E4%BC%98%E5%8A%BF%E3%80%81%E5%AE%9E%E7%8E%B0%E6%96%B9%E5%BC%8F%EF%BC%8C%E5%B9%B6%E6%8F%90%E4%BE%9B%E7%9B%B8%E5%BA%94%E7%9A%84%E4%BB%A3%E7%A0%81%E7%A4%BA%E4%BE%8B%E3%80%82%20%E6%B8%90%E8%BF%9B%E5%BC%8F%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E6%98%AF%E6%8C%87%E5%9C%A8%E5%8A%A0%E8%BD%BD%E5%9B%BE%E7%89%87%E6%97%B6%EF%BC%8C%E5%85%88%E5%8A%A0%E8%BD%BD%E5%87%BA%E4%BD%8E%E5%88%86%E8%BE%A8%E7%8E%87%E7%9A%84%E7%89%88%E6%9C%AC%EF%BC%8C%E7%84%B6%E5%90%8E%E9%80%90%E6%AD%A5%E6%9B%BF%E6%8D%A2%E6%88%90%E6%9B%B4%E9%AB%98%E5%88%86%E8%BE%A8%E7%8E%87%E7%9A%84%E7%89%88%E6%9C%AC%E3%80%82,%E8%BF%99%E6%A0%B7%E5%81%9A%E7%9A%84%E5%A5%BD%E5%A4%84%E6%98%AF%E7%94%A8%E6%88%B7%E5%8F%AF%E4%BB%A5%E5%9C%A8%E7%AD%89%E5%BE%85%E9%AB%98%E8%B4%A8%E9%87%8F%E5%9B%BE%E5%83%8F%E5%8A%A0%E8%BD%BD%E7%9A%84%E8%BF%87%E7%A8%8B%E4%B8%AD%EF%BC%8C%E7%9C%8B%E5%88%B0%E4%B8%80%E4%B8%AA%E6%A8%A1%E7%B3%8A%E4%BD%86%E5%8F%AF%E5%B1%95%E7%A4%BA%E7%9A%84%E5%9B%BE%E5%83%8F%EF%BC%8C%E5%87%8F%E5%B0%91%E4%BA%86%E7%A9%BA%E7%99%BD%E7%94%BB%E9%9D%A2%E7%9A%84%E5%87%BA%E7%8E%B0%EF%BC%8C%E6%8F%90%E9%AB%98%E4%BA%86%E7%94%A8%E6%88%B7%E4%BD%93%E9%AA%8C%E3%80%82%20%E6%9F%90%E4%BA%9B%E5%BA%93%EF%BC%88%E5%A6%82%20Glide%E3%80%81Picasso%EF%BC%89%E5%8F%AF%E4%BB%A5%E5%B8%AE%E5%8A%A9%E6%88%91%E4%BB%AC%E8%BD%BB%E6%9D%BE%E5%AE%9E%E7%8E%B0%E6%B8%90%E8%BF%9B%E5%BC%8F%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E3%80%82
 */
@Composable
fun BlurLoadPage() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loading = rememberLoadingDialog()
    val page = remember { mutableIntStateOf(0) }
    val blurHash = remember { mutableStateOf("U9BqYW-U2v4:rCR6JUIVE1V@#,%M9GIVwv-o") }
    val decodeBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val encodeTx = remember { mutableStateOf("编码后显示的mask code将会显示在此") }
    val encodeTime = remember { mutableLongStateOf(0) }
    val decodeTime = remember { mutableLongStateOf(0) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        scope.launch(Dispatchers.Default) {
            if (uri != null) {
                loading.value = true
                val path = MediaUtils.getRealPathFromUri(context, uri)
                val videoInfo = path?.let { FfmpegVE.getVideoInfo(it) }
                if (videoInfo != null) {
                    val bitmap = BitmapUtils.loadBitmap2Bmp(path)
                    encodeTime.longValue = timed {
                        blurHash.value = bitmap?.let { BlurHash.encode(it, componentX = 5, componentY = 4) } ?: ""
                        if (blurHash.value.isNotEmpty()) {
                            encodeTx.value = "maskCode = ${blurHash.value}"
                        }
                    }
                } else {
                    "选择失败!".showToast(context, ToastType.ERROR)
                }
                loading.value = false
            }
        }
    }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
        item {
            TitleCardView("BlurMask图像展示", contentModifier = Modifier.height(250.dp)) {
                BlurImageShowRowView()
            }
        }

        item {
            TitleCardView("多媒体模糊加载展示", modifier = Modifier.padding(top = 20.dp), contentModifier = Modifier.height(250.dp)) {
                BlurLoadShowRowView()
            }
        }

        item {
            TitleCardView("BlurMask编码与解码", modifier = Modifier.padding(vertical = 20.dp), contentModifier = Modifier.height(300.dp)) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(horizontal = 12.dp)) {
                        Button(modifier = Modifier
                            .weight(1f)
                            .height(40.dp), onClick = { page.intValue = 0 }) {
                            Text("编码", color = Black, fontSize = 12.sp)
                        }
                        Button(modifier = Modifier
                            .weight(1f)
                            .height(40.dp), onClick = { page.intValue = 1 }) {
                            Text("解码", color = Black, fontSize = 12.sp)
                        }
                    }
                    AnimatedContent(page.intValue, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (it == 0) { // 编码
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Button(modifier = Modifier.width(80.dp), onClick = { launcher.launch("image/*") }) {
                                        Text("选择图片", color = Black, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    SelectionContainer{
                                        Text(encodeTx.value, color = Black, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                    }
                                    if (encodeTime.longValue.toInt() != 0) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("耗时：${encodeTime.longValue}ms", color = Black, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                    }
                                }
                            } else {  // 解码
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Button(modifier = Modifier.width(80.dp), onClick = {
                                        decodeTime.longValue = timed {
                                            decodeBitmap.value = BlurHashDecoder.decode(blurHash.value, 24, 48)
                                        }
                                    }) {
                                        Text("点击解码", color = Black, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    decodeBitmap.value?.let { bitmap -> Image(bitmap = bitmap.asImageBitmap(), modifier = Modifier.width(48.dp).height(96.dp), contentDescription = null) }
                                    if (decodeTime.longValue.toInt() != 0) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("耗时：${decodeTime.longValue}ms", color = Black, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun BlurImageShowRowView() {
    LazyRow(modifier = Modifier
        .fillMaxWidth()
        .height(156.div(0.75f).dp), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(blurDataSource) { index, data ->
            Box(modifier = Modifier
                .width(156.dp)
                .height(156.div(0.75f).dp)
                .clip(shape = RoundedCornerShape(12.dp))
                .border(width = 1.dp, color = White16, shape = RoundedCornerShape(12.dp))
            ) {
                val maskBitmap = BlurHashDecoder.decode(data.mask, 18, 32)
                //模糊加载图
                maskBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun BlurLoadShowRowView() {
    LazyRow(modifier = Modifier
        .fillMaxWidth()
        .height(156.div(0.75f).dp), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(blurDataSource) { index, data ->
            Box(modifier = Modifier
                .width(156.dp)
                .height(156.div(0.75f).dp)
                .clip(shape = RoundedCornerShape(12.dp))
                .border(width = 1.dp, color = White16, shape = RoundedCornerShape(12.dp))
            ) {
                CommonMaskLoadImageView(url = data.url, maskCode = data.mask)
            }
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