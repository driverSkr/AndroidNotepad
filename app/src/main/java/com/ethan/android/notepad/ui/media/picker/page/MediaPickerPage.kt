package com.ethan.android.notepad.ui.media.picker.page

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import com.ethan.android.notepad.common.model.CardItem
import com.ethan.android.notepad.common.model.MediaType
import com.ethan.android.notepad.common.utils.MediaUtils
import com.ethan.android.notepad.common.utils.ToastType
import com.ethan.android.notepad.common.utils.showToast
import com.ethan.android.notepad.common.view.ListCardView
import com.ethan.android.notepad.common.view.StatusBarsView
import com.ethan.android.notepad.common.view.TitleCardView
import com.ethan.android.notepad.theme.Black
import com.ethan.android.notepad.ui.dialog.view.rememberLoadingDialog
import com.ethan.android.notepad.ui.media.video.view.VideoView
import com.ethan.videoediting.FfmpegVE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MediaPickerPage() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loading = rememberLoadingDialog()
    val selectedPath = remember { mutableStateOf("") }
    val selectedMediaType = remember { mutableStateOf(MediaType.UNKNOWN) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        scope.launch(Dispatchers.Default) {
            if (uri != null) {
                selectedMediaType.value = MediaUtils.getMediaTypeFromUri(context, uri)
                loading.value = true
                val path = MediaUtils.getRealPathFromUri(context, uri)
                val videoInfo = path?.let { FfmpegVE.getVideoInfo(it) }
                loading.value = false
                if (videoInfo != null) {
                    selectedPath.value = path
                } else {
                    "选择失败!".showToast(context, ToastType.ERROR)
                }
            }
        }
    }

    val items = listOf(
        CardItem("系统相册选择视频") { launcher.launch("video/*") },
        CardItem("系统相册选择图片") { launcher.launch("image/*") },
        CardItem("系统相册选择音频", isCompleted = false) { },
        CardItem("系统相册选择所有类型") { launcher.launch("*/*") },
        CardItem("Vikpea相册选择器", true, isCompleted = false) {  },
        CardItem("自定义相册选择器", true, isCompleted = false) {  },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        StatusBarsView(title = "本地媒体选择")

        ListCardView(items)

        TitleCardView(isShowTitle = false, modifier = Modifier.padding(vertical = 20.dp), contentModifier = Modifier.weight(1f)) {
            AnimatedContent(selectedPath.value) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (it.isEmpty()) {
                        Text("你选择的媒体将会展示在此", color = Black, fontSize = 16.sp)
                    } else {
                        when (selectedMediaType.value) {
                            MediaType.IMAGE -> AsyncImage(model = it, contentDescription = null)
                            MediaType.VIDEO -> VideoView(it)
                            MediaType.AUDIO -> {}
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}