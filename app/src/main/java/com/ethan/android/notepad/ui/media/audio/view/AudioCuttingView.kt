package com.ethan.android.notepad.ui.media.audio.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import com.blankj.utilcode.util.FileUtils
import com.ethan.android.notepad.R
import com.ethan.android.notepad.common.utils.ToastType
import com.ethan.android.notepad.common.utils.VideoHelper
import com.ethan.android.notepad.common.utils.antiShakeClick
import com.ethan.android.notepad.common.utils.formatHMSCTime
import com.ethan.android.notepad.common.utils.getAudioName
import com.ethan.android.notepad.common.utils.showToast
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White60
import com.ethan.android.notepad.ui.material.dialog.view.rememberLoadingDialog
import com.ethan.android.notepad.ui.media.audio.context.LocalAudioContextEntity
import com.ethan.android.notepad.ui.media.audio.context.ViewType
import com.ethan.videoediting.AudioCutting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/** 音频裁剪 */
@Composable
fun AudioCuttingView() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dialog = rememberLoadingDialog()
    val localAudio = LocalAudioContextEntity.current
    val tempPath = remember { mutableStateOf(localAudio.originPath) }
    var audioName by remember { mutableStateOf("") }
    val playState = remember { mutableStateOf(true) }
    val selectTime = remember { mutableIntStateOf(0) }
    val currentTime = remember { mutableStateOf("00:00:00") }
    val startTime = remember { mutableLongStateOf(0L) }
    val endTime = remember { mutableLongStateOf(0L) }
    val playBtn = if (playState.value) R.drawable.svg_icon_record_play else R.drawable.svg_icon_record_pause

    val videoSize = remember { mutableStateOf(IntSize.Zero) }
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true) // 自动处理耳机断开
            .build().apply {
                addMediaItem(MediaItem.fromUri(tempPath.value))

                addListener(object : Player.Listener {
                    override fun onVideoSizeChanged(size: VideoSize) {
                        super.onVideoSizeChanged(size)
                        videoSize.value = IntSize(size.width, size.height)
                    }
                })
                prepare()
                playWhenReady = true
            }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.release()
        }
    }

    LaunchedEffect(tempPath.value) {
        localAudio.finalPath = tempPath.value
        audioName = FileUtils.getFileName(tempPath.value)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.wrapContentSize().align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(playBtn), contentDescription = null, modifier = Modifier
                .size(48.dp)
                .antiShakeClick {
                    if (playState.value) {
                        exoPlayer.pause()
                        playState.value = false
                    } else {
                        exoPlayer.play()
                        playState.value = true
                    }
                })

            Spacer(modifier = Modifier.height(24.dp))

            Text(audioName,color = White, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                currentTime.value,
                color = White60,
                fontSize = 14.sp,
                style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400)
            )

            Spacer(modifier = Modifier.height(26.dp))

            AudioTimeLine(tempPath.value, exoPlayer, playState, startTime, endTime, selectTime, currentTime)

            Spacer(modifier = Modifier.height(33.dp))

            Text("已选${selectTime.intValue}s",color = White60, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))

            Spacer(modifier = Modifier.height(40.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Image(painter = painterResource(R.drawable.svg_icon_voice_delete), contentDescription = null, modifier = Modifier
                    .size(48.dp)
                    .antiShakeClick {
                        exoPlayer.clearMediaItems()
                        exoPlayer.playWhenReady = false
                        if (tempPath.value != localAudio.originPath) {
                            FileUtils.delete(tempPath.value)
                        }
                        localAudio.currentView = ViewType.Record
                    })

                Image(painter = painterResource(R.drawable.svg_icon_voice_save), contentDescription = null, modifier = Modifier
                    .size(48.dp)
                    .antiShakeClick {
                        scope.launch(Dispatchers.Default) {
                            dialog.value = true
                            val audioDir = File(context.externalCacheDir, "temporary_audio/cutting").apply { mkdirs() }
                            val outputPath = File(audioDir, getAudioName()).absolutePath
                            val result = AudioCutting.cropAudio(tempPath.value, outputPath, startTime.longValue.formatHMSCTime(), endTime.longValue.formatHMSCTime())
                            dialog.value = false
                            if (result.isNotBlank() && VideoHelper.getVideoInfo(result) != null) {
                                tempPath.value = result
                                // todo 裁剪完以后，需要重新更新播放器和裁剪框
                            } else {
                                println("裁剪失败：${result}")
                                "音频裁剪失败".showToast(context, ToastType.ERROR)
                            }
                        }
                    })
            }
        }
    }
}