package com.ethan.android.notepad.ui.media.audio.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.blankj.utilcode.util.PermissionUtils
import com.ethan.android.notepad.R
import com.ethan.android.notepad.common.utils.AudioRecorder
import com.ethan.android.notepad.common.utils.MyPermissionUtils
import com.ethan.android.notepad.common.utils.ToastType
import com.ethan.android.notepad.common.utils.antiShakeClick
import com.ethan.android.notepad.common.utils.formatMSCTime
import com.ethan.android.notepad.common.utils.getAudioName
import com.ethan.android.notepad.common.utils.invisible
import com.ethan.android.notepad.common.utils.showToast
import com.ethan.android.notepad.theme.Grey20
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White20
import com.ethan.android.notepad.theme.White60
import com.ethan.android.notepad.ui.animate.BreathingLight
import com.ethan.android.notepad.ui.material.dialog.view.rememberConfirmDialog
import com.ethan.android.notepad.ui.material.dialog.view.rememberLoadingDialog
import com.ethan.android.notepad.ui.media.audio.context.LocalAudioContextEntity
import com.ethan.android.notepad.ui.media.audio.context.ViewType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
@Preview
/** 音频录制、预览 */
fun AudioRecordView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val localAudio = LocalAudioContextEntity.current
    val scope = rememberCoroutineScope()
    val loading = rememberLoadingDialog()
    val audioRecorder = remember { AudioRecorder(60 * 1000L) }
    val audioPlayBtn = if (audioRecorder.playState == AudioRecorder.PlayState.PLAYING) R.drawable.svg_icon_record_play else R.drawable.svg_icon_record_pause

    val recordGif = remember {
        ImageRequest.Builder(context)
            .data("android.resource://${context.packageName}/${R.raw.audio_recording}")
            .decoderFactory(GifDecoder.Factory())
            .build()
    }
    val playGif = remember {
        ImageRequest.Builder(context)
            .data("android.resource://${context.packageName}/${R.raw.audio_playing}")
            .decoderFactory(GifDecoder.Factory())
            .build()
    }

    val permissionDialog = rememberConfirmDialog(
        title = "设置权限",
        content = "请在系统设置中开启录音权限",
        mainTv = "前往设置",
        secondaryTv = "取消",
        mainBtn = { PermissionUtils.launchAppDetailsSettings() }
    )

    // 确保在Composable退出时清理资源
    DisposableEffect(Unit) {
        onDispose {
            audioRecorder.release()
        }
    }

    AnimatedContent(audioRecorder.recordState, label = "") {
        val recordBtn = when (it) {
            AudioRecorder.RecordState.IDLE -> R.drawable.svg_icon_voice_recording
            AudioRecorder.RecordState.RECORDING -> R.drawable.svg_icon_voice_record_pause
            AudioRecorder.RecordState.PAUSED -> {
                if (audioRecorder.playState == AudioRecorder.PlayState.PLAYING) {
                    R.drawable.svg_icon_voice_continue_ban
                } else {
                    R.drawable.svg_icon_voice_continue
                }
            }
        }
        Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier
                .invisible(it == AudioRecorder.RecordState.RECORDING)
                .wrapContentSize()
                .background(color = White20, shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BreathingLight()
                Spacer(modifier = Modifier.width(5.dp))
                Text("记录中",color = White, fontSize = 12.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 40.dp), contentAlignment = Alignment.Center) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.invisible(audioRecorder.playState == AudioRecorder.PlayState.PLAYING || it == AudioRecorder.RecordState.RECORDING)) {
                        SubcomposeAsyncImage(
                            model = playGif,
                            modifier = Modifier.fillMaxWidth().invisible(audioRecorder.playState == AudioRecorder.PlayState.PLAYING),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            success = { success ->
                                SubcomposeAsyncImageContent(
                                    painter = success.painter,
                                    contentDescription = null
                                )
                            }
                        )

                        SubcomposeAsyncImage(
                            model = recordGif,
                            modifier = Modifier.fillMaxWidth().invisible(it == AudioRecorder.RecordState.RECORDING),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            success = { success ->
                                SubcomposeAsyncImageContent(
                                    painter = success.painter,
                                    contentDescription = null
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Image(painter = painterResource(audioPlayBtn), contentDescription = null, modifier = Modifier
                        .invisible(it == AudioRecorder.RecordState.PAUSED)
                        .antiShakeClick {
                            if (audioRecorder.playState == AudioRecorder.PlayState.PLAYING) {
                                audioRecorder.pausePlaying()
                            } else {
                                audioRecorder.startPlaying()
                            }
                        }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (it == AudioRecorder.RecordState.PAUSED && audioRecorder.playState == AudioRecorder.PlayState.PLAYING) {
                    Text(audioRecorder.currentPlayPositionMs.formatMSCTime(),color = White60, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                }
                if (audioRecorder.playState != AudioRecorder.PlayState.PLAYING) {
                    Text(audioRecorder.currentDurationMs.formatMSCTime(),color = White60, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                }
                if (it != AudioRecorder.RecordState.IDLE && audioRecorder.playState != AudioRecorder.PlayState.PLAYING) {
                    Box(modifier = Modifier.padding(horizontal = 7.dp).width(1.dp).height(12.dp).background(color = Grey20, shape = RoundedCornerShape(2.dp)))
                    Text("01:00:00",color = White60, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.svg_icon_voice_delete), contentDescription = null, modifier = Modifier
                    .invisible(it == AudioRecorder.RecordState.PAUSED && audioRecorder.playState != AudioRecorder.PlayState.PLAYING)
                    .antiShakeClick {
                        audioRecorder.deleteFile()
                        "你删除了录音".showToast(context, ToastType.SUCCESS)
                    }
                )
                Image(painter = painterResource(recordBtn), contentDescription = null, modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .clickable(enabled = audioRecorder.playState != AudioRecorder.PlayState.PLAYING) {
                        scope.launch(Dispatchers.Default) {
                            val granted = MyPermissionUtils.checkRecordPermission(false, permissionDialog)
                            if (granted) {
                                when(it) {
                                    AudioRecorder.RecordState.IDLE -> {
                                        context.externalCacheDir?.let { dir -> audioRecorder.startRecording(context, dir) }
                                    }
                                    AudioRecorder.RecordState.RECORDING -> {
                                        context.externalCacheDir?.let { dir -> audioRecorder.pauseRecording(dir) }
                                    }
                                    AudioRecorder.RecordState.PAUSED -> {
                                        context.externalCacheDir?.let { dir -> audioRecorder.startRecording(context, dir) }
                                    }
                                }
                            }
                        }
                    }
                )
                Image(painter = painterResource(R.drawable.svg_icon_voice_save), contentDescription = null, modifier = Modifier
                    .invisible(it == AudioRecorder.RecordState.PAUSED && audioRecorder.playState != AudioRecorder.PlayState.PLAYING)
                    .clickable(enabled = audioRecorder.currentDurationMs > 10000) {
                        scope.launch(Dispatchers.Default) {
                            loading.value = true
                            val audioDir = File(context.externalCacheDir, "temporary_audio/record").apply { mkdirs() }
                            val outputFile = File(audioDir, getAudioName()).apply { createNewFile() }
                            val result = audioRecorder.stopRecording(outputFile){}
                            loading.value = false
                            if (result) {
                                "保存成功,后续操作需补充！".showToast(context, ToastType.SUCCESS)
                                localAudio.originPath = outputFile.path
                                localAudio.currentView = ViewType.Cutting
                            } else {
                                "保存失败".showToast(context, ToastType.ERROR)
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (it) {
                AudioRecorder.RecordState.IDLE -> Text("点击开始录音",color = White, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                AudioRecorder.RecordState.RECORDING -> Text("", fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                AudioRecorder.RecordState.PAUSED -> {
                    if (audioRecorder.currentDurationMs <= 10000) {
                        Text("需要至少10s，请继续录制",color = White, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}