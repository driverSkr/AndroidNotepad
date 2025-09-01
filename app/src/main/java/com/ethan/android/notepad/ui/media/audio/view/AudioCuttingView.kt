package com.ethan.android.notepad.ui.media.audio.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.ethan.android.notepad.common.utils.antiShakeClick
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE
import com.ethan.android.notepad.theme.White
import com.ethan.android.notepad.theme.White60

/** 音频裁剪 */
@Composable
fun AudioCuttingView(originPath: MutableState<String>, finalPath: MutableState<String>, toCuttingPage: MutableState<Boolean> = mutableStateOf(false), needCropPath: MutableState<String> = mutableStateOf(""), source: String = "") {
    val context = LocalContext.current
    val audioPath = remember { mutableStateOf(originPath.value) }
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
                addMediaItem(MediaItem.fromUri(audioPath.value))

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

    LaunchedEffect(audioPath.value) {
        finalPath.value = audioPath.value
        audioName = FileUtils.getFileName(audioPath.value)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.wrapContentSize().align(Alignment.Center).offset(y = (-50).dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(playBtn), contentDescription = null, modifier = Modifier
                .size(48.dp)
                .antiShakeClick {
                    if (playState.value) {
                        // exoPlayer.playWhenReady = false
                        exoPlayer.pause()
                        playState.value = false
                    } else {
                        // exoPlayer.playWhenReady = true
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

            AudioTimeLine(audioPath.value, exoPlayer, playState, startTime, endTime, selectTime, currentTime)

            Spacer(modifier = Modifier.height(33.dp))

            Text("已选${selectTime.intValue}s",color = White60, fontSize = 14.sp, style = NO_PADDING_TEXT_STYLE.copy(fontWeight = FontWeight.W400))

            Spacer(modifier = Modifier.height(40.dp))

            Image(painter = painterResource(R.drawable.svg_icon_voice_delete), contentDescription = null, modifier = Modifier
                .size(48.dp)
                .antiShakeClick {
                    exoPlayer.clearMediaItems()
                    exoPlayer.playWhenReady = false
                    FileUtils.delete(audioPath.value)
                    originPath.value = ""
                    if (source == "record") {
                        needCropPath.value = ""
                        toCuttingPage.value = false
                    } else {
                        toCuttingPage.value = false
                    }
                })
        }
    }
}