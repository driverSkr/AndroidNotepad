package com.ethan.android.notepad.ui.media.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import com.ethan.android.notepad.R
import com.ethan.android.notepad.utils.AsyncImageUtils
import com.ethan.android.notepad.utils.invisible
import com.ethan.maskload.BlurHashDecoder

/**
 * 优化图片不在显示的情况下，就不使用动图加载器进行处理，这样可以优化性能，仅展示 PleasHold
 */
@Composable
fun OptimizeImageView(url: String) {
    val context = LocalContext.current
    val isVisible = remember { mutableStateOf(false) }
    val displayMetrics = context.resources.displayMetrics
    val widthPixels = displayMetrics.widthPixels
    val heightPixels = displayMetrics.heightPixels
    Box(modifier = Modifier
        .fillMaxSize()
        .onGloballyPositioned { coordinates ->
            val position = coordinates.positionInRoot()
            val size = coordinates.size
            isVisible.value = position.x + size.width > 0f && position.x < widthPixels.toFloat() && position.y + size.height > 0f && position.y < heightPixels.toFloat()
        }) {
        if (isVisible.value) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(url).build(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.mipmap.not_load_task_img),
                error = painterResource(id = R.mipmap.not_load_task_img),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun CommonMaskLoadImageView(url: String, maskCode: String) {
    val context = LocalContext.current
    val isShowMask = remember { mutableStateOf(true) }
    val maskBitmap = BlurHashDecoder.decode(maskCode, 18, 32)
    val isVisible = remember { mutableStateOf(false) }
    val displayMetrics = context.resources.displayMetrics
    val widthPixels = displayMetrics.widthPixels
    val heightPixels = displayMetrics.heightPixels

    Box(modifier = Modifier
        .fillMaxSize()
        .onGloballyPositioned { coordinates ->
            //布局的左上角相对于根布局的偏移量
            val position = coordinates.positionInRoot()
            val size = coordinates.size
            isVisible.value = position.x + size.width > 0f &&  // 右边界 > 屏幕左边缘
                    position.x + size.width < widthPixels.toFloat() &&  // 左边界 < 屏幕右边缘
                    position.y + size.height > 0f && // 下边界 > 屏幕上边缘
                    position.y + size.height < heightPixels.toFloat() // 上边界 < 屏幕下边缘
        }
    ) {
        //动图，如webp
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onLoading = { isShowMask.value = true },
            onSuccess = { isShowMask.value = false },
            modifier = Modifier
                .fillMaxSize()
                .invisible(isVisible.value)
        )
        //首帧图
        AsyncImage(
            model = ImageRequest.Builder(context).data(url)
                .transformations(AsyncImageUtils.FrameIndexTransformation(0, url.substringAfterLast("/")))
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .invisible(!isVisible.value)
        )
        //模糊加载图
        if (isShowMask.value) {
            maskBitmap?.let { Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
        }
    }
}