package com.ethan.android.notepad.ui.media.view

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.PermissionUtils
import com.ethan.android.notepad.R
import com.ethan.android.notepad.theme.Grey20
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White10
import com.ethan.android.notepad.theme.White60
import com.ethan.android.notepad.ui.dialog.view.rememberConfirmDialog
import com.ethan.android.notepad.utils.AudioRecorder
import com.ethan.android.notepad.utils.MyPermissionUtils
import com.ethan.android.notepad.utils.ToastType
import com.ethan.android.notepad.utils.antiShakeClick
import com.ethan.android.notepad.utils.invisible
import com.ethan.android.notepad.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
@Preview
fun RecordView(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val audioRecorder = remember { AudioRecorder(context) }
    var recordState by remember { mutableStateOf(RecordState.NotStarted) }
    var audioPath by remember { mutableStateOf("") }
    val recordPlayBtn = if (audioRecorder.isPlaying) R.drawable.svg_icon_record_play else R.drawable.svg_icon_record_pause
    val permissionDialog = rememberConfirmDialog(
        "请求权限",
        "请求录音权限",
        "去应用设置",
        "取消",
        { PermissionUtils.launchAppDetailsSettings() }
    )

    // 确保在Composable退出时清理资源
    DisposableEffect(Unit) {
        onDispose {
            audioRecorder.cleanup()
        }
    }
    LaunchedEffect(audioRecorder.isPlaying) {
        println("ethan 播放状态：${audioRecorder.isPlaying}")
    }

    AnimatedContent(recordState, label = "") {
        val recordBtn = when (it) {
            RecordState.NotStarted -> R.drawable.svg_icon_voice_recording
            RecordState.Recording -> R.drawable.svg_icon_voice_record_pause
            RecordState.Complete -> R.drawable.svg_icon_voice_continue
        }
        Column(modifier = modifier.fillMaxSize().navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {

            Row(modifier = Modifier
                .invisible(it == RecordState.Recording)
                .wrapContentSize()
                .background(color = White10, shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BreathingLight()
                Spacer(modifier = Modifier.width(5.dp))
                Text("Recording",color = White, fontSize = 12.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth().background(color = White10)) {

                Image(painter = painterResource(recordPlayBtn), contentDescription = null, modifier = Modifier
                    .align(Alignment.Center)
                    .invisible(it == RecordState.Complete)
                    .antiShakeClick {
                        if (audioRecorder.isPlaying) {
                            audioRecorder.stopPlaying()
                        } else {
                            audioRecorder.playRecording(audioPath)
                        }
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(audioRecorder.formattedCurrentTime,color = White60, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                if (it != RecordState.NotStarted/* && !audioRecorder.isPlaying*/) {
                    Box(modifier = Modifier.padding(horizontal = 7.dp).width(1.dp).height(12.dp).background(color = Grey20, shape = RoundedCornerShape(2.dp)))
                    Text(audioRecorder.formattedTotalTime,color = White60, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.svg_icon_voice_delete), contentDescription = null, modifier = Modifier
                    .invisible(it == RecordState.Complete && !audioRecorder.isPlaying)
                    .antiShakeClick {
                        if (audioRecorder.deleteRecording(audioPath)) {
                            recordState = RecordState.NotStarted
                            "你删除了录音".showToast(context, ToastType.SUCCESS)
                        } else {
                            "删除失败".showToast(context, ToastType.ERROR)
                        }
                    }
                )
                Image(painter = painterResource(recordBtn), contentDescription = null, modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .antiShakeClick {
                        scope.launch(Dispatchers.Default) {
                            val granted = MyPermissionUtils.checkRecordPermission(false, permissionDialog)
                            if (granted) {
                                if (!audioRecorder.isRecording && recordState != RecordState.Complete) {
                                    val fileName = "audio_${System.currentTimeMillis()}.mp3"
                                    val cacheDir = context.externalCacheDir
                                    val audioDir = File(cacheDir, "cache_audio").apply {
                                        mkdirs() // 自动创建所有父目录
                                    }
                                    audioPath = File(audioDir, fileName).absolutePath

                                    // 检查目录是否可写
                                    if (!audioDir.canWrite()) {
                                        Log.e("AudioRecorder", "目录不可写: $audioDir")
                                    } else {
                                        audioRecorder.startRecording(audioPath)
                                        recordState = RecordState.Recording
                                    }
                                } else if (audioRecorder.isRecording && recordState != RecordState.Complete) {
                                    audioRecorder.stopRecording()
                                    recordState = RecordState.Complete
                                }
                            }
                        }
                    })
                Image(painter = painterResource(R.drawable.svg_icon_voice_save), contentDescription = null, modifier = Modifier
                    .invisible(it == RecordState.Complete && !audioRecorder.isPlaying)
                    .antiShakeClick {
                        "你点击了保存按钮".showToast(context, ToastType.HINT)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (it) {
                RecordState.NotStarted -> Text("点击开始录音",color = White, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                RecordState.Recording -> Text("", fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                RecordState.Complete -> Text("需要至少3s请继续录音",color = White, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
            }
        }
    }
}



enum class RecordState {
    NotStarted,
    Recording,
    Complete
}