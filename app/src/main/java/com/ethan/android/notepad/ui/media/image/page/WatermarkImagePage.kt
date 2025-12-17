package com.ethan.android.notepad.ui.media.image.page

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import coil3.compose.AsyncImage
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PathUtils
import com.ethan.android.notepad.R
import com.ethan.android.notepad.common.model.CardItem
import com.ethan.android.notepad.common.model.MediaType
import com.ethan.android.notepad.common.model.StampPadding
import com.ethan.android.notepad.common.model.WatermarkPosition
import com.ethan.android.notepad.common.utils.BitmapUtils
import com.ethan.android.notepad.common.utils.MediaUtils
import com.ethan.android.notepad.common.utils.ToastType
import com.ethan.android.notepad.common.utils.WaterMarkHelper
import com.ethan.android.notepad.common.utils.showToast
import com.ethan.android.notepad.common.view.ListCardView
import com.ethan.android.notepad.common.view.StatusBarsView
import com.ethan.android.notepad.common.view.TitleCardView
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.ui.dialog.view.rememberLoadingDialog
import com.ethan.videoediting.FfmpegVE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WatermarkImagePage() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loading = rememberLoadingDialog()
    val selectedPath = remember { mutableStateOf("") }
    val selectedMediaType = remember { mutableStateOf(MediaType.UNKNOWN) }
    val doWatermarkStyle = remember { mutableIntStateOf(0) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        scope.launch(Dispatchers.Default) {
            if (uri != null) {
                selectedMediaType.value = MediaUtils.getMediaTypeFromUri(context, uri)
                loading.value = true
                val path = MediaUtils.getRealPathFromUri(context, uri)
                val videoInfo = path?.let { FfmpegVE.getVideoInfo(it) }
                if (videoInfo != null) {
                    val watermark = ImageUtils.getBitmap(R.mipmap.ai_generate)
                    if (doWatermarkStyle.intValue == 0) {
                        val bmp = BitmapUtils.loadBitmap2Bmp(path)
                        val finalBmp = bmp?.let { WaterMarkHelper.addImageWatermark(context, it, watermark) }
                        if (finalBmp != null) {
                            selectedPath.value = BitmapUtils.saveBitmapAndReturnPath(context, finalBmp) ?: ""
                        } else {
                            "保存失败".showToast(context, ToastType.ERROR)
                        }
                    } else if (doWatermarkStyle.intValue == 1) {    // ffmpeg版
                        // 生成唯一的文件名
                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        val fileName = "watermarked_$timeStamp.jpg"
                        val exportPath = "${PathUtils.getExternalAppCachePath()}/water_mark/$fileName".apply {
                            FileUtils.createOrExistsDir(File(this).parentFile)
                        }
                        val waterPath = BitmapUtils.saveBitmapAndReturnPath(context, watermark)

                        val result = WaterMarkHelper.addImageWaterMark(path, exportPath, waterPath ?: "", WatermarkPosition.TOP_LEFT)
                        if (result) {
                            selectedPath.value = exportPath
                        } else {
                            "保存失败".showToast(context, ToastType.ERROR)
                        }
                    } else {    // 文字加水印
                        val bmp = BitmapUtils.loadBitmap2Bmp(path)
                        val finalBmp = bmp?.let { WaterMarkHelper.addTextWatermark(bmp, "这是文字水印", 15, R.color.White4, StampPadding(20f, 20f)) }
                        if (finalBmp != null) {
                            selectedPath.value = BitmapUtils.saveBitmapAndReturnPath(context, finalBmp) ?: ""
                        } else {
                            "保存失败".showToast(context, ToastType.ERROR)
                        }
                    }
                } else {
                    "选择失败!".showToast(context, ToastType.ERROR)
                }
                loading.value = false
            }
        }
    }

    val items = listOf(
        CardItem("图片加水印", false, isCompleted = false) {
            doWatermarkStyle.intValue = 0
            launcher.launch("image/*")
        },
        CardItem("图片加水印-ffmpeg版", false, isCompleted = false) {
            doWatermarkStyle.intValue = 1
            launcher.launch("image/*")
        },
        CardItem("图片加文字水印", false, isCompleted = false) {
            doWatermarkStyle.intValue = 2
            launcher.launch("image/*")
        },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "图片相关")

        ListCardView(items)

        TitleCardView(isShowTitle = false, modifier = Modifier.padding(vertical = 20.dp), contentModifier = Modifier.weight(1f)) {
            AnimatedContent(selectedPath.value) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (it.isEmpty()) {
                        Text("你选择的媒体将会展示在此", color = Black, fontSize = 16.sp)
                    } else {
                        when (selectedMediaType.value) {
                            MediaType.IMAGE -> AsyncImage(model = it, contentDescription = null)
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

